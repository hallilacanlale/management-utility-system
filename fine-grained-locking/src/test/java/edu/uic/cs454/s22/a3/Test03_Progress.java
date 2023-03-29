package edu.uic.cs454.s22.a3;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Test03_Progress {

    private static final int WORK = 500_000;

    @Test
    public void progressGetOneTestingSite() {
        Lab l = Lab.createLab();
        TestingSite slow, fast;
        DiagnosticTest[] ds = new DiagnosticTest[WORK];
        Set<DiagnosticTest> slowContents = new HashSet<>();

        // Set up
        {
            int slowCapacity = ds.length - 1;
            slow = l.createTestingSite(slowCapacity);

            for (int i = 1 ; i < slowCapacity ; i++) {
                ds[i] = l.createDiagnosticTest(i);
                slowContents.add(ds[i]);
            }

            Assert.assertTrue(l.sampleDiagnosticTests(slow, slowContents));

            fast = l.createTestingSite(1);
            ds[0] = l.createDiagnosticTest(0);
            Assert.assertTrue(l.sampleDiagnosticTests(fast, Set.of(ds[0])));
        }

        for (int i = 0 ; i < 10 ; i++) {
            AtomicReference<Thread> firstToFinish = new AtomicReference<>(null);

            // Thread T1 gets all the doses from slow
            Thread t1 = new Thread(() -> {
                Set<DiagnosticTest> contents = l.getDiagnosticTests(slow);
                firstToFinish.compareAndExchange(null, Thread.currentThread());
                Assert.assertEquals(slowContents, contents);
            });

            // Thread T2 gets all the doses from fast
            Thread t2 = new Thread(() -> {
                Set<DiagnosticTest> contents = l.getDiagnosticTests(fast);
                firstToFinish.compareAndExchange(null, Thread.currentThread());
                Assert.assertEquals(Set.of(ds[0]), contents);
            });

            runAllThreads(t1, t2);

            // Thread T2 started after T1 but finished first
            Assert.assertEquals(t2, firstToFinish.get());

            System.gc();
        }
    }

    @Test
    public void progressGetTestingSite() {
        Lab l = Lab.createLab();
        TestingSite slow, fast;
        DiagnosticTest[] ds = new DiagnosticTest[WORK];
        Set<DiagnosticTest> everything = new HashSet<>();

        // Set up
        {
            int slowCapacity = ds.length - 1;
            slow = l.createTestingSite(slowCapacity);

            for (int i = 1 ; i < slowCapacity ; i++) {
                ds[i] = l.createDiagnosticTest(i);
                everything.add(ds[i]);
            }

            Assert.assertTrue(l.sampleDiagnosticTests(slow, everything));

            fast = l.createTestingSite(1);
            ds[0] = l.createDiagnosticTest(0);
            Assert.assertTrue(l.sampleDiagnosticTests(fast, Set.of(ds[0])));
            everything.add(ds[0]);
        }


        for (int i = 0 ; i < 10 ; i++) {
            AtomicReference<Thread> firstToFinish = new AtomicReference<>(null);

            // Thread T1 gets all the doses
            Thread t1 = new Thread(() -> {
                Set<DiagnosticTest> contents = l.getDiagnosticTests();
                firstToFinish.compareAndExchange(null, Thread.currentThread());
                Assert.assertEquals(everything, contents);
            });

            // Thread T2 gets all the doses from fast
            Thread t2 = new Thread(() -> {
                Set<DiagnosticTest> contents = l.getDiagnosticTests(fast);
                firstToFinish.compareAndExchange(null, Thread.currentThread());
                Assert.assertEquals(Set.of(ds[0]), contents);
            });

            runAllThreads(t1, t2);

            // Thread T2 started after T1 but finished first
            Assert.assertEquals(t2, firstToFinish.get());

            System.gc();
        }
    }

    @Test
    public void progressPositiveNegative() {
        for (int lucky = 0 ; lucky < 10 ; lucky++) {
            {
                Lab l = Lab.createLab();
                TestingSite slow, fast;
                DiagnosticTest[] ds = new DiagnosticTest[WORK];
                Set<DiagnosticTest> slowContents = new HashSet<>();

                // Set up
                {
                    int slowCapacity = ds.length - 1;
                    slow = l.createTestingSite(slowCapacity);

                    for (int i = 1; i < slowCapacity; i++) {
                        ds[i] = l.createDiagnosticTest(i);
                        slowContents.add(ds[i]);
                    }

                    Assert.assertTrue(l.sampleDiagnosticTests(slow, slowContents));

                    fast = l.createTestingSite(1);
                    ds[0] = l.createDiagnosticTest(0);
                    Assert.assertTrue(l.sampleDiagnosticTests(fast, Set.of(ds[0])));
                }

                AtomicReference<Thread> firstToFinish = new AtomicReference<>(null);

                // Thread T1 uses all the doses from slow
                Thread t1 = new Thread(() -> {
                    boolean result = l.positive(slow, slowContents);
                    firstToFinish.compareAndExchange(null, Thread.currentThread());
                    Assert.assertTrue(result);
                    Assert.assertEquals(l.getDiagnosticTests(slow), Set.of());
                });

                // Thread T2 uses all the doses from fast
                Thread t2 = new Thread(() -> {
                    boolean result = l.negative(fast, Set.of(ds[0]));
                    firstToFinish.compareAndExchange(null, Thread.currentThread());
                    Assert.assertTrue(result);
                    Assert.assertEquals(l.getDiagnosticTests(fast), Set.of());
                });

                runAllThreads(t1, t2);

                // Thread T2 started after T1 but finished first
                Assert.assertEquals(t2, firstToFinish.get());

                Assert.assertEquals(Set.of(), l.getDiagnosticTests());
            }

            System.gc();
        }
    }

    /*default*/ static void runAllThreads(Thread ... threads) {

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
