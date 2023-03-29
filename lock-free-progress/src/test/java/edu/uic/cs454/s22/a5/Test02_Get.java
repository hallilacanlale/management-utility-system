package edu.uic.cs454.s22.a5;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test02_Get {
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
        DiagnosticTest[] tests = new DiagnosticTest[size];

        for (int i = 0 ; i < size ; i++) {
            tests[i] = l.createDiagnosticTest(i);
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(tests[i]);
            l.sampleDiagnosticTests(c, toAdd);

            Assert.assertNotSame(toAdd, l.getDiagnosticTests());
            Assert.assertNotSame(toAdd, l.getDiagnosticTests(c));

            toAdd.clear();

            Set<DiagnosticTest> expected = new HashSet<>();
            for (int j = 0 ; j <= i ; j++)
                expected.add(tests[j]);

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
        DiagnosticTest[] tests = new DiagnosticTest[size];
        Set<DiagnosticTest> toAdd = new HashSet<>();
        Set<DiagnosticTest> expected = new HashSet<>();

        for (int i : indexes) {
            tests[i] = l.createDiagnosticTest(i);
            toAdd.add(tests[i]);
            expected.add(tests[i]);
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
    public void testCapacityAddManyTestingSites() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(10);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1,v2);
            l.sampleDiagnosticTests(c1, toAdd);

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));
        }

        {
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(v1);
            l.sampleDiagnosticTests(c1, toAdd);
            toAdd.clear();
            toAdd.add(v2);
            l.sampleDiagnosticTests(c1, toAdd);

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(c2));
        }
    }

    @Test
    public void testCapacityDuplicatedDiagnosticTest() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(10);
        TestingSite c2 = l.createTestingSite(10);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(v1);
            l.sampleDiagnosticTests(c1, toAdd);
            l.sampleDiagnosticTests(c2, toAdd);

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(c2));
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));

            expected = Set.of(v2);
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));

            expected = Set.of(v1, v2);
            Assert.assertEquals(expected, l.getDiagnosticTests());
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c1, toAdd);
            l.sampleDiagnosticTests(c2, toAdd);

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));

            expected = Set.of(v2);
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));

            expected = Set.of(v1, v2);
            Assert.assertEquals(expected, l.getDiagnosticTests());
        }
    }

    @Test
    public void testCapacityDuplicatedItem() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(10);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        {
            l.sampleDiagnosticTests(c, Set.of(v1));
            l.sampleDiagnosticTests(c, Set.of(v1));

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests());
//            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v1, v2));

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v2));
            l.sampleDiagnosticTests(c, Set.of(v2));

            Set<DiagnosticTest> expected = Set.of(v1, v2);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }
    }



}
