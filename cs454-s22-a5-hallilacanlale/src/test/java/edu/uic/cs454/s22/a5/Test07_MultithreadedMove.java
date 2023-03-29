package edu.uic.cs454.s22.a5;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class Test07_MultithreadedMove {


    @Test
    public void testMove() {
        int n = 5;
        Lab l = Lab.createLab();

        Thread[] threads = new Thread[8];

        TestingSite[] tss = new TestingSite[10];
        int size = 2 * n;

        DiagnosticTest[] tests = new DiagnosticTest[tss.length * n];

        for (int i = 0 ; i < tss.length ; i++) {
            tss[i] = l.createTestingSite(size);
            for (int j = 0 ; j < n ; j++) {
                int testNo = i * n + j;
                DiagnosticTest test = l.createDiagnosticTest(testNo);
                tests[testNo] = test;
                l.sampleDiagnosticTests(tss[i], Set.of(test));
            }
        }

        for (int i = 0 ; i < threads.length ; i++) {
            threads[i] = new Thread(() -> {
                Random r = new Random();
                for (int j = 0 ; j < 2_500 ; j++) {
                    int from = r.nextInt(tss.length);
                    int to   = r.nextInt(tss.length);

                    while (to == from) {
                        to = r.nextInt(tss.length);
                    }

                    Optional<DiagnosticTest> testsToMove = l.getDiagnosticTests(tss[from]).stream().findAny();
                    if (testsToMove.isPresent())
                        l.moveDiagnosticTests(tss[from], tss[to], Set.of(testsToMove.get()));
                }
            });
        }


        Set<DiagnosticTest> expectedAllDiagnosticTests = new HashSet<>(Set.of(tests));
        Assert.assertEquals(expectedAllDiagnosticTests, l.getDiagnosticTests());

        Test05_MultithreadedAdd.runAllThreads(threads);

        // Everything in the lab there is still there
        Assert.assertEquals(expectedAllDiagnosticTests, l.getDiagnosticTests());

        for (TestingSite s : tss) {
            for (Object i : l.getDiagnosticTests(s)) {
                // Each object is in only one shelf
                Assert.assertTrue(expectedAllDiagnosticTests.remove(i));
            }
        }

        // The contents of all shelfs are everything that las in the lab
        Assert.assertTrue(expectedAllDiagnosticTests.isEmpty());
    }
}
