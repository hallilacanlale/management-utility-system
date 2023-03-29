package edu.uic.cs454.s22.a5;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class Test06_MultithreadedRemove {

    @Test
    public void testRemove() {
        Lab l = Lab.createLab();

        TestingSite[] tss = new TestingSite[10];
        int size = 2_500;

        for (int i = 0 ; i < tss.length ; i++) {
            tss[i] = l.createTestingSite(size);
            Set<DiagnosticTest> tests = new HashSet<>();
            for (int j = 0 ; j < size ; j++) {
                DiagnosticTest t = l.createDiagnosticTest(i*size + j);
                tests.add(t);
            }
            l.sampleDiagnosticTests(tss[i], tests);
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
                    TestingSite s = tss[rnd.nextInt(tss.length)];
                    // Get any test
                    Optional<DiagnosticTest> t = l.getDiagnosticTests(s).stream().findAny();
                    if (!t.isPresent())
                        continue;

                    boolean result;
                    if (removed%2 == 0)
                        result = l.positive(s, Set.of(t.get()));
                    else
                        result = l.negative(s, Set.of(t.get()));

                    if (result)
                        removed++;
                }
            });
        }

        Test05_MultithreadedAdd.runAllThreads(threads);

        Assert.assertEquals(0, l.getDiagnosticTests().size());
        Assert.assertEquals(Set.of(), l.getDiagnosticTests());

        for (int i = 0 ; i < tss.length ; i++)
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(tss[i]));

    }
}
