package edu.uic.cs454.s22.a1;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test02_SingleTestingSiteContents {
    @Test
    public void testCapacityAdd() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);


        {
            l.sampleDiagnosticTests(c, Set.of(v1, v2));

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }

        {
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(v1);
            l.sampleDiagnosticTests(c, toAdd);
            toAdd.clear();
            toAdd.add(v2);
            l.sampleDiagnosticTests(c, toAdd);

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }
    }

    @Test
    public void testCapacitySameDescription() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        testCapacitySameDescription(sequentialIndexes);
        testCapacitySameDescription(shuffledIndexesList);
    }

    @Test
    public void testUsingOwnSet() {
        Lab l = Lab.createLab();
        int size = 10;
        TestingSite c = l.createTestingSite(size);
        DiagnosticTest[] diagnosticTests = new DiagnosticTest[size];

        for (int i = 0 ; i < size ; i++) {
            diagnosticTests[i] = l.createDiagnosticTest(i);
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(diagnosticTests[i]);
            l.sampleDiagnosticTests(c, toAdd);

            Assert.assertNotSame(toAdd, l.getDiagnosticTests());
            Assert.assertNotSame(toAdd, l.getDiagnosticTests(c));

            toAdd.clear();

            Set<DiagnosticTest> expected = new HashSet<>();
            for (int j = 0 ; j <= i ; j++)
                expected.add(diagnosticTests[j]);

            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            l.getDiagnosticTests().clear();
            l.getDiagnosticTests(c).clear();

            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }
    }

    public void testCapacitySameDescription(List<Integer> indexes) {
        int size = indexes.size();
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(size);
        DiagnosticTest[] diagnosticTests = new DiagnosticTest[size];
        Set<DiagnosticTest> toAdd = new HashSet<>();
        Set<DiagnosticTest> expected = new HashSet<>();

        for (int i : indexes) {
            diagnosticTests[i] = l.createDiagnosticTest(i);
            toAdd.add(diagnosticTests[i]);
            expected.add(diagnosticTests[i]);
        }

        l.sampleDiagnosticTests(c, toAdd);

        Assert.assertEquals(size, expected.size());
        Assert.assertEquals(expected, l.getDiagnosticTests());
        l.getDiagnosticTests().clear();
        Assert.assertEquals(size, expected.size());
        Assert.assertEquals(expected, l.getDiagnosticTests());

        Assert.assertEquals(size, l.getDiagnosticTests(c).size());
        Assert.assertEquals(expected, l.getDiagnosticTests(c));
        l.getDiagnosticTests(c).clear();
        Assert.assertEquals(size, l.getDiagnosticTests(c).size());
        Assert.assertEquals(expected, l.getDiagnosticTests(c));
    }

    @Test
    public void testRemoveFromTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);

        {
            DiagnosticTest v1 = l.createDiagnosticTest(0);
            DiagnosticTest v2 = l.createDiagnosticTest(1);

            Set<DiagnosticTest> items = Set.of(v1, v2);
            l.sampleDiagnosticTests(c, items);
            l.negative(c, items);

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }

        {
            DiagnosticTest v1 = l.createDiagnosticTest(2);
            DiagnosticTest v2 = l.createDiagnosticTest(3);

            Set<DiagnosticTest> items = new HashSet<>();
            items.add(v1);
            l.sampleDiagnosticTests(c, items);
            items.clear();
            items.add(v2);
            l.sampleDiagnosticTests(c, items);


            items.clear();
            items.add(v1);
            l.positive(c, items);

            Set<DiagnosticTest> expected = Set.of(v2);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            items.clear();
            items.add(v2);
            l.positive(c, items);

            expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }
    }

    @Test
    public void testRemoveFromEmptyTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        {
            Set<DiagnosticTest> items = Set.of(v1, v2);

            l.negative(c, items);

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }

        {
            Set<DiagnosticTest> items = new HashSet<>();
            items.add(v1);
            items.add(v2);

            l.sampleDiagnosticTests(c, items);

            Set<DiagnosticTest> expected = Set.of(v1, v2);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            l.positive(c, items);
            l.negative(c, items);

            expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            items.clear();
            items.add(v1);
            l.negative(c, items);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            items.clear();
            items.add(v2);
            l.positive(c, items);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }
    }

    @Test
    public void testRemoveDiagnosticTestNotInTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        {
            Set<DiagnosticTest> items = new HashSet<>();
            items.add(v1);
            l.sampleDiagnosticTests(c, Set.of(v1));

            items.clear();
            items.add(v2);
            l.positive(c, items);

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            l.getDiagnosticTests().clear();
            l.getDiagnosticTests(c).clear();

            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }
    }

    @Test
    public void testDiagnosticTestChangesCorrectly() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(3);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);
        DiagnosticTest v3 = l.createDiagnosticTest(2);

        {
            Assert.assertEquals(DiagnosticTest.Status.READY, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v3.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v3.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v3.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v3));

            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v3.getStatus());
        }

        {
            l.positive(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v3.getStatus());
        }

        {
            l.negative(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v3.getStatus());
        }

        {
            l.invalid(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v3.getStatus());
        }

        {
            l.negative(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v3.getStatus());
        }

        {
            l.positive(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v3.getStatus());
        }

        {
            l.invalid(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v3.getStatus());
        }

        {
            l.invalid(c, Set.of(v3));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v3.getStatus());
        }

        {
            l.positive(c, Set.of(v3));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v3.getStatus());
        }

        {
            l.negative(c, Set.of(v3));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v3.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v3.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v3.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v3));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v3.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v1, v2, v3));

            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v2.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v3.getStatus());
        }


    }
}
