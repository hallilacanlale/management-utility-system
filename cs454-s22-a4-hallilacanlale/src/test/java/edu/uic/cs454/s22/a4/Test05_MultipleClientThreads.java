package edu.uic.cs454.s22.a4;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test05_MultipleClientThreads {

    @Test
    public void testSample() {
        Lab l = Lab.createLab();

        TestingSite[] clinics = new TestingSite[10];
        int size = 1_000;

        for (int i = 0 ; i < clinics.length ; i++) {
            clinics[i] = l.createTestingSite(size);
        }

        Thread[] threads = new Thread[10];
        Set<DiagnosticTest>[] allDiagnosticTests = new Set[threads.length];

        for (int i = 0 ; i < threads.length ; i++) {
            int threadID = i;
            allDiagnosticTests[i] = new HashSet<>();
            threads[i] = new Thread(() -> {
                Random rnd = new Random();
                for (int j = 0 ; j < size ; j++) {
                    DiagnosticTest test = l.createDiagnosticTest(threadID*size + j);
                    Set<DiagnosticTest> tests = Set.of(test);
                    allDiagnosticTests[threadID].add(test);
                    while (true) {
                        // Pick a clinic at random
                        TestingSite c = clinics[rnd.nextInt(clinics.length)];
                        // TestingSite is full or add failed
                        if (l.getDiagnosticTests(c).size() == size || !l.sampleDiagnosticTests(c, tests))
                            // Try again on another clinic
                            continue;
                        break;
                    }
                }
            });
        }

        AtomicBoolean exceptionThrown = startAllThreads(threads);

        for (TestingSite c : clinics) {
            c.startThread();
        }

        joinAllThreads(threads);
        Assert.assertFalse(exceptionThrown.get());

        Assert.assertEquals(size*clinics.length, l.getDiagnosticTests().size());

        Set<DiagnosticTest> expectedAllDiagnosticTests = new HashSet<>();
        for (int i = 0 ; i < threads.length ; i++)
            expectedAllDiagnosticTests.addAll(allDiagnosticTests[i]);

        Assert.assertEquals(expectedAllDiagnosticTests, l.getDiagnosticTests());

        Set<DiagnosticTest> testsOnTestingSites = new HashSet<>();
        for (int i = 0 ; i < clinics.length ; i++)
            testsOnTestingSites.addAll(l.getDiagnosticTests(clinics[i]));

        Assert.assertEquals(expectedAllDiagnosticTests, testsOnTestingSites);

        Set<DiagnosticTest> contents = new HashSet<>();
        for (TestingSite c : clinics) {
            c.stopThread();

            Assert.assertFalse(c.didThrowException());
            contents.addAll(c.getSampledTests());
        }

        Assert.assertEquals(expectedAllDiagnosticTests, contents);
    }

    // Each thread (out of 10 threads) removes 100 tests from lab with 1000 tests
    @Test
    public void testRemove() {
        Lab l = Lab.createLab();

        TestingSite[] clinics = new TestingSite[10];
        int size = 100;

        for (int i = 0 ; i < clinics.length ; i++) {
            clinics[i] = l.createTestingSite(size);
            Set<DiagnosticTest> tests = new HashSet<>();
            for (int j = 0 ; j < size ; j++) {
                DiagnosticTest test = l.createDiagnosticTest(i*size + j);
                tests.add(test);
            }
            l.sampleDiagnosticTestsAsync(clinics[i], tests);
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
                    // Get any test
                    Optional<DiagnosticTest> test = l.getDiagnosticTests(s).stream().findAny();
                    if (!test.isPresent())
                        continue;

                    boolean result;
                    if (removed%2 == 0)
                        result = l.positive(s, Set.of(test.get()));
                    else
                        result = l.negative(s, Set.of(test.get()));

                    if (result)
                        removed++;
                }
            });
        }

        AtomicBoolean exceptionThrown = startAllThreads(threads);

        for (TestingSite c : clinics)
            c.startThread();

        joinAllThreads(threads);
        Assert.assertFalse(exceptionThrown.get());

        Assert.assertEquals(0, l.getDiagnosticTests().size());
        Assert.assertEquals(Set.of(), l.getDiagnosticTests());

        for (int i = 0 ; i < clinics.length ; i++)
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(clinics[i]));

        for (TestingSite c : clinics) {
            c.stopThread();
            Assert.assertFalse(c.didThrowException());
            Assert.assertEquals(Set.of(), c.getSampledTests());
        }

    }

    /*default*/ static AtomicBoolean startAllThreads(Thread[] threads) {

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

        return exceptionThrown;
    }

    /*default*/ static void joinAllThreads(Thread[] threads) {

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
    }
}
