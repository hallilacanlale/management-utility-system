package edu.uic.cs454.s22.a5;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test05_MultithreadedAdd {

    @Test
    public void testAdd() {
        Lab l = Lab.createLab();

        TestingSite[] tss = new TestingSite[10];
        int size = 2_500;

        for (int i = 0 ; i < tss.length ; i++)
            tss[i] = l.createTestingSite(size);

        Thread[] threads = new Thread[10];
        Set<DiagnosticTest>[] allDiagnosticTests = new Set[threads.length];

        for (int i = 0 ; i < threads.length ; i++) {
            int threadID = i;
            allDiagnosticTests[i] = new HashSet<>();
            threads[i] = new Thread(() -> {
                Random rnd = new Random();
                for (int j = 0 ; j < size ; j++) {
                    DiagnosticTest t = l.createDiagnosticTest(threadID*size + j);
                    Set<DiagnosticTest> ts = Set.of(t);
                    allDiagnosticTests[threadID].add(t);
                    while (true) {
                        // Pick a ts at random
                        TestingSite c = tss[rnd.nextInt(tss.length)];
                        // TestingSite is full or add failed
                        if (l.getDiagnosticTests(c).size() == size || !l.sampleDiagnosticTests(c, ts))
                            // Try again on another ts
                            continue;
                        break;
                    }
                }
            });
        }

        runAllThreads(threads);

        Assert.assertEquals(size*tss.length, l.getDiagnosticTests().size());

        Set<DiagnosticTest> expectedAllDiagnosticTests = new HashSet<>();
        for (int i = 0 ; i < threads.length ; i++)
            expectedAllDiagnosticTests.addAll(allDiagnosticTests[i]);

        Assert.assertEquals(expectedAllDiagnosticTests, l.getDiagnosticTests());

        Set<DiagnosticTest> testsOnTestingSites = new HashSet<>();
        for (int i = 0 ; i < tss.length ; i++)
            testsOnTestingSites.addAll(l.getDiagnosticTests(tss[i]));

        Assert.assertEquals(expectedAllDiagnosticTests, testsOnTestingSites);
    }

    /*default*/ static void runAllThreads(Thread[] threads) {

        AtomicBoolean exceptionThrown = new AtomicBoolean(false);

        // Uncaught exceptions cause tests to fail
        for (int i = 0 ; i < threads.length ; i++) {
            threads[i].setUncaughtExceptionHandler((t,ex) -> {
                System.err.println(ex.getMessage());
                ex.printStackTrace();
                exceptionThrown.set(true);
            });
        }

        // Start all threads
        for (int i = 0 ; i < threads.length ; i++)
            threads[i].start();

        // Wait for all threads to finish
        for (int i = 0 ; i < threads.length ; i++) {
            while (threads[i].isAlive()) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }

        // Make sure no thread threw an exception
        Assert.assertFalse(exceptionThrown.get());
    }
}
