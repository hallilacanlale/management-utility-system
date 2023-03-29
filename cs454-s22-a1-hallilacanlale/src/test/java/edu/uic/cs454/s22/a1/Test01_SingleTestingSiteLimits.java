package edu.uic.cs454.s22.a1;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test01_SingleTestingSiteLimits {
    @Test
    public void testDiagnosticTestIdentity() {
        Lab l = Lab.createLab();

        int size = 10;
        DiagnosticTest[] diagTestss = new DiagnosticTest[size];

        Set<DiagnosticTest> toAdd = new HashSet<>();
        for (int i = 0 ; i < size ; i++) {
            diagTestss[i] = l.createDiagnosticTest(i);
            toAdd.add(diagTestss[i]);
        }

        Assert.assertEquals(size, toAdd.size());
        Assert.assertEquals(toAdd, Set.of(diagTestss));
    }

    @Test
    public void testMaxCapacity() {
        Lab l = Lab.createLab();
        int size = 10;
        int test = 20;
        TestingSite c = l.createTestingSite(size);

        for (int i = 0 ; i < test ; i++) {
            DiagnosticTest v = l.createDiagnosticTest(i);

            if (i < size)
                Assert.assertTrue(l.sampleDiagnosticTests(c, Set.of(v)));
            else
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(v)));
        }
    }

    @Test
    public void testRemoveDiagnosticTest() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        testRemoveDiagnosticTest(sequentialIndexes);
        testRemoveDiagnosticTest(shuffledIndexesList);
    }

    public void testRemoveDiagnosticTest(List<Integer> indexes) {

        Lab l = Lab.createLab();
        int size = indexes.size();
        TestingSite c = l.createTestingSite(size);

        DiagnosticTest[] diagTestss = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            diagTestss[i] = l.createDiagnosticTest(i);
        }

        for (int i : indexes) {
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(diagTestss[i]);
            Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
            switch (i%3) {
                case 0:
                    Assert.assertTrue(l.negative(c, toAdd));
                    break;
                case 1:
                    Assert.assertTrue(l.positive(c, toAdd));
                    break;
                case 2:
                    Assert.assertTrue(l.invalid(c, toAdd));
                    break;
                default:
                    throw new Error("Dead code");
            }
        }

        {
            for (int i = 0 ; i < size ; i++) {
                diagTestss[i] = l.createDiagnosticTest(size+i);
            }

            Set<DiagnosticTest> toAdd = new HashSet<>();
            for (int i : indexes) {
                toAdd.add(diagTestss[i]);
            }

            Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
            Assert.assertTrue(l.negative(c, toAdd));
        }

        {
            for (int i = 0 ; i < size ; i++) {
                diagTestss[i] = l.createDiagnosticTest((size*2)+i);
            }

            Set<DiagnosticTest> toAdd = new HashSet<>();
            for (int i : indexes) {
                toAdd.add(diagTestss[i]);
            }

            Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
            Assert.assertTrue(l.positive(c, toAdd));
        }

        {
            for (int i = 0 ; i < size ; i++) {
                diagTestss[i] = l.createDiagnosticTest((size*2)+i);
            }

            Set<DiagnosticTest> toAdd = new HashSet<>();
            for (int i : indexes) {
                toAdd.add(diagTestss[i]);
            }

            Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
            Assert.assertTrue(l.invalid(c, toAdd));
        }
    }


    @Test
    public void testRemoveFromEmptyTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        {
            Assert.assertFalse(l.negative(c, Set.of(v1)));
            Assert.assertFalse(l.negative(c, Set.of(v2)));
            Assert.assertFalse(l.negative(c, Set.of(v1, v2)));

            Assert.assertFalse(l.positive(c, Set.of(v1)));
            Assert.assertFalse(l.positive(c, Set.of(v2)));
            Assert.assertFalse(l.positive(c, Set.of(v1, v2)));

            Assert.assertFalse(l.invalid(c, Set.of(v1)));
            Assert.assertFalse(l.invalid(c, Set.of(v2)));
            Assert.assertFalse(l.invalid(c, Set.of(v1, v2)));
        }
    }

    @Test
    public void testSetIdentity() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        Set<DiagnosticTest> diagTestss = new HashSet<>();
        diagTestss.add(v1);
        diagTestss.add(v2);

        {
            Assert.assertTrue(l.sampleDiagnosticTests(c, diagTestss));
            Assert.assertTrue(l.negative(c, diagTestss));
            Assert.assertFalse(l.negative(c, diagTestss));
            Assert.assertFalse(l.positive(c, diagTestss));
            Assert.assertFalse(l.invalid(c, diagTestss));
        }

        {
            diagTestss.clear();
            diagTestss.add(v1);
            Assert.assertFalse(l.positive(c, diagTestss));
            Assert.assertFalse(l.negative(c, diagTestss));
            Assert.assertFalse(l.invalid(c, diagTestss));
        }

        {
            diagTestss.clear();
            diagTestss.add(v2);
            Assert.assertFalse(l.positive(c, diagTestss));
            Assert.assertFalse(l.negative(c, diagTestss));
            Assert.assertFalse(l.invalid(c, diagTestss));
        }
    }

    @Test
    public void testRemoveDiagnosticTestNotInTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        {
            Set<DiagnosticTest> diagTestss = new HashSet<>();
            diagTestss.add(v1);
            Assert.assertTrue(l.sampleDiagnosticTests(c, diagTestss));

            diagTestss.clear();
            diagTestss.add(v2);
            Assert.assertFalse(l.positive(c, diagTestss));
            Assert.assertFalse(l.negative(c, diagTestss));
            Assert.assertFalse(l.invalid(c, diagTestss));
        }
    }
}