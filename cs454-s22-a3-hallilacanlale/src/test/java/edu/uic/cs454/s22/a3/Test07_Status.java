package edu.uic.cs454.s22.a3;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test07_Status {

    @Test
    public void getStatusIsLinearizable() {
        for (int lucky = 0 ; lucky < 25 ; lucky++) {
            Lab l = Lab.createLab();
            Set<DiagnosticTest>[] groups = new Set[50];
            Set<DiagnosticTest> everything = new HashSet<>();
            int capacity = 200_000;
            TestingSite c = l.createTestingSite(capacity);

            // Set-up
            {
                int id = 0;
                for (int i = 0 ; i < groups.length ; i++) {
                    Set<DiagnosticTest> s = new HashSet<>();
                    for (int j = 0 ; j < capacity / groups.length ; j++) {
                        DiagnosticTest d = l.createDiagnosticTest(id++);
                        s.add(d);
                        everything.add(d);
                    }

                    groups[i] = s;
                }

                Assert.assertTrue(l.sampleDiagnosticTests(c, everything));
            }

            AtomicBoolean done = new AtomicBoolean(false);

            // Thread 1 marks as positive or negative all the tests, group by group
            Thread t1 = new Thread(() -> {
                Random r = new Random();
                for (int i = 0 ; i < groups.length ; i++) {
                    if (r.nextBoolean())
                        Assert.assertTrue(l.positive(c, groups[i]));
                    else
                        Assert.assertTrue(l.negative(c, groups[i]));
                }

                done.set(true);
            });

            // Thread 2 takes snapshots of the tests in the lab
            Thread t2 = new Thread(() -> {
                Random r = new Random();

                while (!done.get()) {
                    // Pick a group at random
                    Set<DiagnosticTest> g = groups[r.nextInt(groups.length)];

                    // Get the status of a test
                    DiagnosticTest.Status s = null;

                    if (s == DiagnosticTest.Status.SAMPLED)
                        continue;


                    for (DiagnosticTest d : g) {
                        if (s == null)
                            s = d.getStatus();

                        if (s == DiagnosticTest.Status.SAMPLED)
                            break;

                        // Ensure that all the other tests have the same status, either POSITIVE or NEGATIVE
                        Assert.assertEquals(s, d.getStatus());

                    }
                }
            });

            Test03_Progress.runAllThreads(t1, t2);

            Assert.assertEquals(Set.of(), l.getDiagnosticTests());

        }
    }

}
