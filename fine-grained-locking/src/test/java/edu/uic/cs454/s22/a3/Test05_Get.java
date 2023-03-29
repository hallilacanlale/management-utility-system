package edu.uic.cs454.s22.a3;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test05_Get {
    @Test
    public void testDeadlockingMoveGet() {
        Lab l = Lab.createLab();
        TestingSite[] cs = new TestingSite[10_000];
        DiagnosticTest[] ds = new DiagnosticTest[cs.length];

        // Set up
        {
            for (int i = 0 ; i < cs.length ; i++) {
                cs[i] = l.createTestingSite(cs.length);
                ds[i] = l.createDiagnosticTest(i);

                Assert.assertTrue(l.sampleDiagnosticTests(cs[i], Set.of(ds[i])));
            }
        }

        AtomicBoolean done = new AtomicBoolean(false);

        // Thread 1, move
        Thread t1 = new Thread(() -> {
            for (int i = 0 ; i < cs.length ; i++) {
                Set<DiagnosticTest> s = Set.of(ds[i]);
                Assert.assertTrue(l.moveDiagnosticTests(cs[i], cs[(i+1)%cs.length], s));
            }

            done.set(true);
        });

        Set<DiagnosticTest> expected = Set.of(ds);
        List<TestingSite> lst = Arrays.asList(cs);

        // Thread 2, get contents
        Thread t2 = new Thread(() -> {
            while (!done.get()) {
                l.getDiagnosticTests(lst);
            }
        });

        List<TestingSite> rev = new LinkedList<>(lst);
        Collections.reverse(rev);

        // Thread 3, get contents in reverse
        Thread t3 = new Thread(() -> {
            while (!done.get()) {
                l.getDiagnosticTests(rev);
            }
        });

        Test03_Progress.runAllThreads(t1, t2, t3);

        // No tests were lost
        Assert.assertEquals(expected, l.getDiagnosticTests());
        Assert.assertEquals(expected, l.getDiagnosticTests(lst));
        Assert.assertEquals(expected, l.getDiagnosticTests(rev));
    }

    @Test
    public void testGetIsLinearizabile() {
        Lab l = Lab.createLab();
        TestingSite[] cs = new TestingSite[10_000];
        DiagnosticTest[] ds = new DiagnosticTest[cs.length];
        Set<DiagnosticTest> allDoses = new HashSet<>();

        // Set up
        {
            for (int i = 0 ; i < cs.length ; i++) {
                cs[i] = l.createTestingSite(cs.length);
                ds[i] = l.createDiagnosticTest(i);
                allDoses.add(ds[i]);

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

        // Thread 2 ensures it always sees all doses while Thread 1 moves doses
        Thread t2 = new Thread(() -> {
            while (!done.get()) {
                Assert.assertEquals(allDoses, l.getDiagnosticTests());
            }
        });

        Test03_Progress.runAllThreads(t2, t1);

        // No doses were lost
        Assert.assertEquals(Set.of(ds), l.getDiagnosticTests(Arrays.asList(cs)));
    }

}
