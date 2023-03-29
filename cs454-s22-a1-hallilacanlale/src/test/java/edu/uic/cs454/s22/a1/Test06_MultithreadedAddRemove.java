package edu.uic.cs454.s22.a1;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test06_MultithreadedAddRemove {

    // Each thread adds 100 diagTests, final lab should have 1000 diagTests

    @Test
    public void testAdd() {
        Lab l = Lab.createLab();

        TestingSite[] clinics = new TestingSite[10];
        int size = 100;

        for (int i = 0 ; i < clinics.length ; i++)
            clinics[i] = l.createTestingSite(size);

        Thread[] threads = new Thread[10];
        Set<DiagnosticTest>[] allDiagnosticTests = new Set[threads.length];

        for (int i = 0 ; i < threads.length ; i++) {
            int threadID = i;
            allDiagnosticTests[i] = new HashSet<>();
            threads[i] = new Thread(() -> {
                Random rnd = new Random();
                for (int j = 0 ; j < size ; j++) {
                    DiagnosticTest diagTests = l.createDiagnosticTest(threadID*size + j);
                    Set<DiagnosticTest> diagTestss = Set.of(diagTests);
                    allDiagnosticTests[threadID].add(diagTests);
                    while (true) {
                        // Pick a clinic at random
                        TestingSite c = clinics[rnd.nextInt(clinics.length)];
                        // TestingSite is full or add failed
                        if (l.getDiagnosticTests(c).size() == size || !l.sampleDiagnosticTests(c, diagTestss))
                            // Try again on another clinic
                            continue;
                        break;
                    }
                }
            });
        }

        runAllThreads(threads);

        Assert.assertEquals(size*clinics.length, l.getDiagnosticTests().size());

        Set<DiagnosticTest> expectedAllDiagnosticTests = new HashSet<>();
        for (int i = 0 ; i < threads.length ; i++)
            expectedAllDiagnosticTests.addAll(allDiagnosticTests[i]);

        Assert.assertEquals(expectedAllDiagnosticTests, l.getDiagnosticTests());

        Set<DiagnosticTest> diagTestssOnTestingSites = new HashSet<>();
        for (int i = 0 ; i < clinics.length ; i++)
            diagTestssOnTestingSites.addAll(l.getDiagnosticTests(clinics[i]));

        Assert.assertEquals(expectedAllDiagnosticTests, diagTestssOnTestingSites);
    }

    // Each thread (out of 10 threads) removes 100 diagTests from lab with 1000 diagTests
    @Test
    public void testRemove() {
        Lab l = Lab.createLab();

        TestingSite[] clinics = new TestingSite[10];
        int size = 100;

        for (int i = 0 ; i < clinics.length ; i++) {
            clinics[i] = l.createTestingSite(size);
            Set<DiagnosticTest> diagTestss = new HashSet<>();
            for (int j = 0 ; j < size ; j++) {
                DiagnosticTest diagTests = l.createDiagnosticTest(i*size + j);
                diagTestss.add(diagTests);
            }
            l.sampleDiagnosticTests(clinics[i], diagTestss);
        }

        Thread[] threads = new Thread[10];
        Set<DiagnosticTest>[] allDiagnosticTests = new Set[threads.length];

        for (int i = 0 ; i < threads.length ; i++) {
            int threadID = i;
            allDiagnosticTests[i] = new HashSet<>();
            threads[i] = new Thread(() -> {
                Random rnd = new Random();
                int removed = 0;
                while (removed < size) {
                    TestingSite s = clinics[rnd.nextInt(clinics.length)];
                    // Get any diagTests
                    Optional<DiagnosticTest> diagTests = l.getDiagnosticTests(s).stream().findAny();
                    if (!diagTests.isPresent())
                        continue;

                    boolean result;
                    if (removed%2 == 0)
                        result = l.positive(s, Set.of(diagTests.get()));
                    else
                        result = l.negative(s, Set.of(diagTests.get()));

                    if (result)
                        removed++;
                }
            });
        }

        Test06_MultithreadedAddRemove.runAllThreads(threads);

        Assert.assertEquals(0, l.getDiagnosticTests().size());
        Assert.assertEquals(Set.of(), l.getDiagnosticTests());

        for (int i = 0 ; i < clinics.length ; i++)
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(clinics[i]));

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