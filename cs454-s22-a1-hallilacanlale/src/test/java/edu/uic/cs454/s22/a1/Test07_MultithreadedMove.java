package edu.uic.cs454.s22.a1;

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

        TestingSite[] clinics = new TestingSite[10];
        int size = 2 * n;

        DiagnosticTest[] diagTestss = new DiagnosticTest[clinics.length * n];

        for (int i = 0 ; i < clinics.length ; i++) {
            clinics[i] = l.createTestingSite(size);
            for (int j = 0 ; j < n ; j++) {
                int diagTestsNo = i * n + j;
                DiagnosticTest diagTests = l.createDiagnosticTest(diagTestsNo);
                diagTestss[diagTestsNo] = diagTests;
                l.sampleDiagnosticTests(clinics[i], Set.of(diagTests));
            }
        }

        for (int i = 0 ; i < threads.length ; i++) {
            threads[i] = new Thread(() -> {
                Random r = new Random();
                for (int j = 0 ; j < 10_000 ; j++) {
                    int from = r.nextInt(clinics.length);
                    int to   = r.nextInt(clinics.length);
                    Optional<DiagnosticTest> diagTestsToMove = l.getDiagnosticTests(clinics[from]).stream().findAny();
                    if (diagTestsToMove.isPresent())
                        l.moveDiagnosticTests(clinics[from], clinics[to], Set.of(diagTestsToMove.get()));
                }
            });
        }


        Set<DiagnosticTest> expectedAllDiagnosticTests = new HashSet<>(Set.of(diagTestss));
        Assert.assertEquals(expectedAllDiagnosticTests, l.getDiagnosticTests());

        Test06_MultithreadedAddRemove.runAllThreads(threads);

        // Everything that las there is still there
        Assert.assertEquals(expectedAllDiagnosticTests, l.getDiagnosticTests());

        for (TestingSite s : clinics) {
            for (Object i : l.getDiagnosticTests(s)) {
                // Each object is in only one shelf
                Assert.assertTrue(expectedAllDiagnosticTests.remove(i));
            }
        }

        // The contents of all shelfs are everything that las in the larehouse
        Assert.assertTrue(expectedAllDiagnosticTests.isEmpty());
    }
}