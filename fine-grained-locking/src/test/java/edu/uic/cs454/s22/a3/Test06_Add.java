package edu.uic.cs454.s22.a3;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Test06_Add {
    @Test
    public void addIsLinearizable() {
        Lab l = Lab.createLab();
        DiagnosticTest[] ds = new DiagnosticTest[2_500];
        TestingSite[] cs = new TestingSite[ds.length * 2];

        AtomicReference<Thread>[] winner = new AtomicReference[ds.length];


        // Set-up
        {
            for (int i = 0 ; i < cs.length ; i++) {
                cs[i] = l.createTestingSite(2);
            }

            for (int i = 0 ; i < ds.length ; i++) {
                ds[i] = l.createDiagnosticTest(i);
                winner[i] = new AtomicReference<>(null);
            }
        }

        // Thread 1 attempts to insert the same dose on clinics 0-499
        Thread t1 = new Thread(() -> {
            for (int i = 0 ; i < ds.length ; i++) {
                boolean result = l.sampleDiagnosticTests(cs[i], Set.of(ds[i]));
                if (result) {
                    Thread prior = winner[i].compareAndExchange(null, Thread.currentThread());
                    // null -> I won
                    // not null -> Both me and the other thread were able to add the same dose to different clinics
                    Assert.assertNull(prior);
                }
            }
        });

        // Thread 2 attempts to insert the same dose on clinics 500-1000
        Thread t2 = new Thread(() -> {
            for (int i = 0 ; i < ds.length ; i++) {
                boolean result = l.sampleDiagnosticTests(cs[i + ds.length], Set.of(ds[i]));
                if (result) {
                    Thread prior = winner[i].compareAndExchange(null, Thread.currentThread());
                    // null -> I won
                    // not null -> Both me and the other thread were able to add the same dose to different clinics
                    Assert.assertNull(prior);
                }
            }
        });

        Test03_Progress.runAllThreads(t1, t2);
        Assert.assertEquals(Set.of(ds), l.getDiagnosticTests());
        Set<DiagnosticTest> allTests = new HashSet<>();
        for (TestingSite ts : cs) {
            for (Object t : l.getDiagnosticTests(ts)) {
                Assert.assertFalse(allTests.contains(t));
            }
            allTests.addAll(l.getDiagnosticTests(ts));
        }
    }

    @Test
    public void testDeadlockingMoveAdd() {
        Lab l = Lab.createLab();
        TestingSite[] cs = new TestingSite[10_000];
        TestingSite[] reverse = new TestingSite[10_000];
        DiagnosticTest[] ds = new DiagnosticTest[cs.length];

        // Set up
        {
            for (int i = 0 ; i < cs.length ; i++) {
                cs[i] = l.createTestingSite(cs.length);
                reverse[cs.length-1-i] = cs[i];
                ds[i] = l.createDiagnosticTest(i);

                Assert.assertTrue(l.sampleDiagnosticTests(cs[i], Set.of(ds[i])));
            }
        }

        AtomicBoolean done = new AtomicBoolean(false);

        // Thread 1, move
        Thread t1 = new Thread(() -> {
            for (int i = cs.length - 1 ; i > 0 ; i--) {
                Set<DiagnosticTest> s = Set.of(ds[i]);
                Assert.assertTrue(l.moveDiagnosticTests(cs[i], cs[(i-1)], s));
            }

            done.set(true);
        });

        TestingSite toAdd = l.createTestingSite(Integer.MAX_VALUE);

        // Thread 2, add
        Thread t2 = new Thread(() -> {
            int id = 1000;
            while (!done.get() && id < Integer.MAX_VALUE) {
                DiagnosticTest d = l.createDiagnosticTest(id);
                Assert.assertTrue(l.sampleDiagnosticTests(toAdd, Set.of(d)));
            }
        });

        Test03_Progress.runAllThreads(t1, t2);

        // No doses were lost
        Assert.assertEquals(Set.of(ds), l.getDiagnosticTests(Arrays.asList(cs)));
    }


}
