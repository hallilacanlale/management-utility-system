package edu.uic.cs454.s22.a3;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class Test04_Move {

    @Test
    public void testDeadlockingMoveMove() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        // Set up
        {
            l.sampleDiagnosticTests(c1, Set.of(v1,v2));
        }

        int n = 500_000;

        // Thread 1, move v1 back and forth
        Thread t1 = new Thread(() -> {
            for (int i = 0 ; i < n ; i++) {
                Set<DiagnosticTest> s = Set.of(v1);
                Assert.assertTrue(l.moveDiagnosticTests(c1, c2, s));
                Assert.assertTrue(l.moveDiagnosticTests(c2, c1, s));
            }
        });

        // Thread 2, move v2 back and forth
        Thread t2 = new Thread(() -> {
            for (int i = 0 ; i < n ; i++) {
                Set<DiagnosticTest> s = Set.of(v2);
                Assert.assertTrue(l.moveDiagnosticTests(c1, c2, s));
                Assert.assertTrue(l.moveDiagnosticTests(c2, c1, s));
            }
        });

        Test03_Progress.runAllThreads(t1, t2);

        // No doses were lost
        Assert.assertEquals(Set.of(v1, v2), l.getDiagnosticTests());
        Assert.assertEquals(Set.of(v1, v2), l.getDiagnosticTests(List.of(c1, c2)));
    }
}
