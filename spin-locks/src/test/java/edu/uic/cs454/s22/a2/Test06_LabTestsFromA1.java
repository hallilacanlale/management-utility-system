package edu.uic.cs454.s22.a2;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test06_LabTestsFromA1 {
    @Test
    public void test01DiagnosticTestIdentity() {
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
    public void test01MaxCapacity() {
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
    public void test01RemoveDiagnosticTest() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        test01RemoveDiagnosticTest(sequentialIndexes);
        test01RemoveDiagnosticTest(shuffledIndexesList);
    }

    public void test01RemoveDiagnosticTest(List<Integer> indexes) {

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
    public void test01RemoveFromEmptyTestingSite() {
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
    public void test01SetIdentity() {
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
    public void test01RemoveDiagnosticTestNotInTestingSite() {
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

    @Test
    public void test02CapacityAdd() {
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
    public void test02CapacitySameDescription() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        test02CapacitySameDescription(sequentialIndexes);
        test02CapacitySameDescription(shuffledIndexesList);
    }

    @Test
    public void test02UsingOwnSet() {
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

    public void test02CapacitySameDescription(List<Integer> indexes) {
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
    public void test02RemoveFromTestingSite() {
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
    public void test02RemoveFromEmptyTestingSite() {
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
    public void test02RemoveDiagnosticTestNotInTestingSite() {
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
    public void test02DiagnosticTestChangesCorrectly() {
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

    @Test
    public void test03MaxCapacity() {
        Lab l = Lab.createLab();
        int test = 20;
        TestingSite[] clinics = new TestingSite[10];

        for (int i = 0 ; i < clinics.length ; i++)
            clinics[i] = l.createTestingSite(i+1);

        int count = 0;

        for (int i = 0 ; i < test ; i++) {
            for (int j = 0 ; i < clinics.length ; i++) {
                TestingSite s = clinics[j];
                DiagnosticTest diagTests = l.createDiagnosticTest(count++);
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(diagTests);

                if (i <= j)
                    Assert.assertTrue(l.sampleDiagnosticTests(s, toAdd));
                else
                    Assert.assertFalse(l.sampleDiagnosticTests(s, toAdd));
            }
        }
    }

    @Test
    public void moveDiagnosticTestToEmptyTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);
        DiagnosticTest v3 = l.createDiagnosticTest(3);
        DiagnosticTest v4 = l.createDiagnosticTest(4);

        Set<DiagnosticTest> toAdd = Set.of(v1, v2);
        Assert.assertTrue(l.sampleDiagnosticTests(c1, toAdd));

        Set<DiagnosticTest> toMove = Set.of(v1, v2);
        Assert.assertTrue(l.moveDiagnosticTests(c1, c2, toMove));
        Assert.assertFalse(l.negative(c1, toMove));
        Assert.assertFalse(l.positive(c1, toMove));

        Set<DiagnosticTest> toAddMore = Set.of(v3, v4);
        Assert.assertTrue(l.sampleDiagnosticTests(c1, toAddMore));
        Assert.assertTrue(l.negative(c2, toMove));
    }

    @Test
    public void test03NoRoomInDestination() {
        Lab l = Lab.createLab();
        TestingSite from = l.createTestingSite(1);
        TestingSite to = l.createTestingSite(1);

        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        l.sampleDiagnosticTests(from, Set.of(v1));
        l.sampleDiagnosticTests(to, Set.of(v2));

        {
            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests(from));
        }

        {
            Set<DiagnosticTest> expected = Set.of(v2);
            Assert.assertEquals(expected, l.getDiagnosticTests(to));
        }

        Assert.assertFalse(l.moveDiagnosticTests(from, to, Set.of(v1)));

        {
            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests(from));
        }

        {
            Set<DiagnosticTest> expected = Set.of(v2);
            Assert.assertEquals(expected, l.getDiagnosticTests(to));
        }

    }

    @Test
    public void test03RemoveFromLab() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList1);
        Collections.shuffle(shuffledIndexesList2);

        test03RemoveFromTestingSite(sequentialIndexes, sequentialIndexes);
        test03RemoveFromTestingSite(shuffledIndexesList1, shuffledIndexesList2);
    }

    public void test03RemoveFromTestingSite(List<Integer> diagTestsIndexes, List<Integer> shelfIndexes) {

        Lab l = Lab.createLab();
        int size = diagTestsIndexes.size();
        TestingSite[] clinics = new TestingSite[shelfIndexes.size()];

        for (int i = 0 ; i < clinics.length ; i++)
            clinics[i] = l.createTestingSite(size);

        DiagnosticTest[] diagTests = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++)
            diagTests[i] = l.createDiagnosticTest(i);

        for (int i : diagTestsIndexes) {
            Set<DiagnosticTest> toAdd = new HashSet<>();
            TestingSite s = clinics[shelfIndexes.get(i)];
            TestingSite ss = clinics[shelfIndexes.get((i+1)%shelfIndexes.size())];
            toAdd.add(diagTests[i]);
            Assert.assertFalse(l.negative(s, toAdd));
            Assert.assertFalse(l.positive(ss, toAdd));

            Assert.assertTrue(l.sampleDiagnosticTests(s, toAdd));

            Assert.assertFalse(l.negative(ss, toAdd));
            Assert.assertFalse(l.positive(ss, toAdd));

            switch (i%3) {
                case 0:
                    Assert.assertTrue(l.positive(s, toAdd));
                    break;
                case 1:
                    Assert.assertTrue(l.negative(s, toAdd));
                    break;
                case 2:
                    Assert.assertTrue(l.invalid(s, toAdd));
                    break;
                default:
                    throw new Error("Dead code");
            }
        }
    }

    @Test
    public void test03RemoveFromEmptyTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> diagTests = new HashSet<>();
            diagTests.add(v1);
            diagTests.add(v2);

            Assert.assertFalse(l.negative(c1, diagTests));
            Assert.assertFalse(l.positive(c1, diagTests));
            Assert.assertFalse(l.invalid(c1, diagTests));
            Assert.assertFalse(l.negative(c2, diagTests));
            Assert.assertFalse(l.positive(c2, diagTests));
            Assert.assertFalse(l.invalid(c2, diagTests));
        }

        {
            Assert.assertTrue(l.sampleDiagnosticTests(c1, Set.of(v1)));
            Assert.assertTrue(l.sampleDiagnosticTests(c2, Set.of(v2)));
            Assert.assertTrue(l.negative(c1, Set.of(v1)));
            Assert.assertTrue(l.positive(c2, Set.of(v2)));

            Set<DiagnosticTest> diagTests = new HashSet<>();
            diagTests.add(v1);
            diagTests.add(v2);

            Assert.assertFalse(l.negative(c1, diagTests));
            Assert.assertFalse(l.positive(c1, diagTests));
            Assert.assertFalse(l.negative(c2, diagTests));
            Assert.assertFalse(l.positive(c2, diagTests));

            diagTests.clear();
            diagTests.add(v1);
            Assert.assertFalse(l.negative(c1, diagTests));
            Assert.assertFalse(l.positive(c1, diagTests));
            Assert.assertFalse(l.negative(c2, diagTests));
            Assert.assertFalse(l.positive(c2, diagTests));

            diagTests.clear();
            diagTests.add(v2);
            Assert.assertFalse(l.negative(c1, diagTests));
            Assert.assertFalse(l.positive(c1, diagTests));
            Assert.assertFalse(l.negative(c2, diagTests));
            Assert.assertFalse(l.positive(c2, diagTests));
        }
    }

    @Test
    public void moveDiagnosticTestToFullTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);
        DiagnosticTest v3 = l.createDiagnosticTest(3);
        DiagnosticTest v4 = l.createDiagnosticTest(4);

        Set<DiagnosticTest> toAdd = Set.of(v1, v2);
        Assert.assertTrue(l.sampleDiagnosticTests(c1, toAdd));

        Set<DiagnosticTest> toAddMore = Set.of(v3, v4);
        Assert.assertTrue(l.sampleDiagnosticTests(c2, toAddMore));

        Set<DiagnosticTest> toMove = Set.of(v1, v2);
        Assert.assertFalse(l.moveDiagnosticTests(c1, c2, toMove));


        Assert.assertTrue(l.negative(c2, Set.of(v4)));
        Assert.assertFalse(l.moveDiagnosticTests(c1, c2, toMove));
        Assert.assertTrue(l.moveDiagnosticTests(c1, c2, Set.of(v1)));
    }

    @Test
    public void test04moveDiagnosticTestFromEmptyTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(10);
        TestingSite c2 = l.createTestingSite(10);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);
        DiagnosticTest v3 = l.createDiagnosticTest(3);
        DiagnosticTest v4 = l.createDiagnosticTest(4);

        Set<DiagnosticTest> toAdd = Set.of(v1, v2);
        Assert.assertTrue(l.sampleDiagnosticTests(c1, toAdd));

        Set<DiagnosticTest> toMove = Set.of(v3, v4);
        Assert.assertFalse(l.moveDiagnosticTests(c2, c1, toMove));
    }

    @Test
    public void test04CapacityDuplicatedItem() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(10);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        {
            l.sampleDiagnosticTests(c, Set.of(v1));
            l.sampleDiagnosticTests(c, Set.of(v1));

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
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

    @Test
    public void test04DiagnosticTestAlreadyInTestingSite() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        test04DiagnosticTestAlreadyInTestingSite(sequentialIndexes);
        test04DiagnosticTestAlreadyInTestingSite(shuffledIndexesList);
    }

    private void test04DiagnosticTestAlreadyInTestingSite(List<Integer> indexes) {
        int size = indexes.size();
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(size);

        DiagnosticTest[] diagTestss = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            diagTestss[i] = l.createDiagnosticTest(i);
        }

        for (int i : indexes) {
            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(diagTestss[i]);
                Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(diagTestss[i])));
            }

            if (i > 0) {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(diagTestss[i]);
                toAdd.add(diagTestss[i-1]);
                Assert.assertFalse(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(diagTestss[i], diagTestss[i-1])));
            }

            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(diagTestss[i]);
                DiagnosticTest anotherDiagnosticTest = l.createDiagnosticTest(size+i);
                toAdd.add(anotherDiagnosticTest);
                Assert.assertFalse(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(diagTestss[i], anotherDiagnosticTest)));
            }
        }
    }

    @Test
    public void test04CannotReuseDiagnosticTests() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        test04CannotReuseDiagnosticTests(sequentialIndexes);
        test04CannotReuseDiagnosticTests(shuffledIndexesList);
    }

    public void test04CannotReuseDiagnosticTests(List<Integer> indexes) {
        Lab l = Lab.createLab();
        int size = indexes.size();
        TestingSite c = l.createTestingSite(size);

        DiagnosticTest[] diagTestss = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            diagTestss[i] = l.createDiagnosticTest(i);
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(diagTestss[i]);
            Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
        }

        for (int i : indexes) {
            Set<DiagnosticTest> vs = Set.of(diagTestss[i]);
            switch (i%3) {
                case 0:
                    Assert.assertTrue(l.negative(c, vs));
                    break;
                case 1:
                    Assert.assertTrue(l.positive(c, vs));
                    break;
                case 2:
                    Assert.assertTrue(l.invalid(c, vs));
                    break;
                default:
                    throw new Error("Dead code");
            }

            Assert.assertFalse(l.sampleDiagnosticTests(c, vs));
            Assert.assertFalse(l.negative(c, vs));
        }
    }

    @Test
    public void test04DiagnosticTestAlreadyInLab() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList1);
        Collections.shuffle(shuffledIndexesList2);

        test04DiagnosticTestAlreadyInLab(sequentialIndexes, sequentialIndexes);
        test04DiagnosticTestAlreadyInLab(shuffledIndexesList1, shuffledIndexesList2);
    }

    private void test04DiagnosticTestAlreadyInLab(List<Integer> diagTestsIndexes, List<Integer> shelfIndexes) {
        int size = diagTestsIndexes.size();
        Lab l = Lab.createLab();
        TestingSite[] clinics = new TestingSite[size];

        for (int i = 0 ; i < size ; i++)
            clinics[i] = l.createTestingSite(size);

        int count = 0;

        DiagnosticTest[] diagTestss = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            diagTestss[i] = l.createDiagnosticTest(count++);
        }


        for (int i : diagTestsIndexes) {
            TestingSite s  = clinics[shelfIndexes.get(i)];
            TestingSite ss = clinics[shelfIndexes.get((i+1)%size)];

            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(diagTestss[i]);
                Assert.assertTrue(l.sampleDiagnosticTests(s, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(s, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(s, Set.of(diagTestss[i])));

                Assert.assertFalse(l.sampleDiagnosticTests(ss, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(ss, Set.of(diagTestss[i])));
            }

            if (i > 0) {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(diagTestss[i]);
                toAdd.add(diagTestss[i-1]);
                Assert.assertFalse(l.sampleDiagnosticTests(s, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(s, Set.of(diagTestss[i], diagTestss[i-1])));
            }


            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(diagTestss[i]);
                DiagnosticTest anotherDiagnosticTest = l.createDiagnosticTest(count++);
                toAdd.add(anotherDiagnosticTest);
                Assert.assertFalse(l.sampleDiagnosticTests(s, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(s, Set.of(diagTestss[i], anotherDiagnosticTest)));

                Assert.assertFalse(l.sampleDiagnosticTests(ss, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(ss, Set.of(diagTestss[i])));
            }
        }
    }



    @Test
    public void test04CapacityAdd() {
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
    public void test04CapacityDuplicatedDiagnosticTest() {
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
    public void test04RemoveFromTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);

        int count = 0;

        {
            DiagnosticTest v1 = l.createDiagnosticTest(count++);
            DiagnosticTest v2 = l.createDiagnosticTest(count++);

            Set<DiagnosticTest> diagTestss = new HashSet<>();
            diagTestss.add(v1);
            diagTestss.add(v2);
            l.sampleDiagnosticTests(c1, diagTestss);
            l.positive(c2, diagTestss);
            l.positive(c1, diagTestss);

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));
        }

        {
            DiagnosticTest v1 = l.createDiagnosticTest(count++);
            DiagnosticTest v2 = l.createDiagnosticTest(count++);
            DiagnosticTest v3 = l.createDiagnosticTest(count++);

            Set<DiagnosticTest> diagTestss = new HashSet<>();
            diagTestss.add(v1);
            diagTestss.add(v3);
            l.sampleDiagnosticTests(c1, diagTestss);
            diagTestss.clear();
            diagTestss.add(v2);
            l.sampleDiagnosticTests(c2, diagTestss);


            diagTestss.clear();
            diagTestss.add(v1);
            l.positive(c1, diagTestss);
            l.sampleDiagnosticTests(c2, diagTestss);
            l.sampleDiagnosticTests(c1, diagTestss);

            Set<DiagnosticTest> expected = Set.of(v2, v3);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(Set.of(v2), l.getDiagnosticTests(c2));
            Assert.assertEquals(Set.of(v3), l.getDiagnosticTests(c1));

            diagTestss.clear();
            diagTestss.add(v2);
            l.negative(c2, diagTestss);

            expected = Set.of(v3);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(c2));
        }
    }

    @Test
    public void test04RemoveFromEmptyTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);

        int count = 0;

        {
            DiagnosticTest v1 = l.createDiagnosticTest(count++);
            DiagnosticTest v2 = l.createDiagnosticTest(count++);

            Set<DiagnosticTest> diagTestss = new HashSet<>();
            diagTestss.add(v1);
            diagTestss.add(v2);

            l.positive(c1, diagTestss);
            l.positive(c2, diagTestss);

            Set<DiagnosticTest> expected = new HashSet<>();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));
        }

        {
            DiagnosticTest v1 = l.createDiagnosticTest(count++);
            DiagnosticTest v2 = l.createDiagnosticTest(count++);

            Set<DiagnosticTest> diagTestss = new HashSet<>();
            diagTestss.add(v1);
            diagTestss.add(v2);

            l.sampleDiagnosticTests(c1, diagTestss);
            l.positive(c1, diagTestss);
            l.positive(c2, diagTestss);

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));

            diagTestss.clear();
            diagTestss.add(v1);
            l.negative(c1, diagTestss);
            l.negative(c2, diagTestss);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));

            diagTestss.clear();
            diagTestss.add(v2);
            l.negative(c1, diagTestss);
            l.positive(c2, diagTestss);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));
        }
    }

    @Test
    public void test04RemoveDiagnosticTestNotInTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> diagTestss = new HashSet<>();
            diagTestss.add(v1);
            l.sampleDiagnosticTests(c1, diagTestss);

            diagTestss.clear();
            diagTestss.add(v2);
            l.positive(c2, diagTestss);

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(c2));
        }
    }

    @Test
    public void test05DiagnosticTestAuditDiagnosticTestsAdded() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        Assert.assertEquals(List.of(new Action(Action.Direction.SAMPLED, c1)), l.audit(v1));
        Assert.assertEquals(List.of(new Action(Action.Direction.SAMPLED, c2)), l.audit(v2));
    }

    @Test
    public void test05DiagnosticTestAuditDiagnosticTestsAddedRemoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(1);
        TestingSite c3 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);
        DiagnosticTest v3 = l.createDiagnosticTest(3);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v3);
            l.sampleDiagnosticTests(c3, toAdd);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v1);
            l.positive(c1, toRemove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v2);
            l.negative(c2, toRemove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v3);
            l.invalid(c3, toRemove);
        }

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,  c1),
                        new Action(Action.Direction.POSITIVE, c1)),
                l.audit(v1));

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED, c2),
                        new Action(Action.Direction.NEGATIVE, c2)),
                l.audit(v2));

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED, c3),
                        new Action(Action.Direction.INVALID, c3)),
                l.audit(v3));
    }

    @Test
    public void test05DiagnosticTestAuditDiagnosticTestsAddedMoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v1);
            l.moveDiagnosticTests(c1, c2, toMove);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v2);
            l.moveDiagnosticTests(c2, c1, toMove);
        }

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   c1),
                        new Action(Action.Direction.MOVED_OUT, c1),
                        new Action(Action.Direction.MOVED_IN,  c2)
                ),
                l.audit(v1));

        Assert.assertEquals(
                Arrays.asList(
                        new Action(Action.Direction.SAMPLED,   c2),
                        new Action(Action.Direction.MOVED_OUT, c2),
                        new Action(Action.Direction.MOVED_IN,  c1)
                ),
                l.audit(v2));
    }

    @Test
    public void test05DiagnosticTestAuditDiagnosticTestsAddedMovedRemoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v1);
            l.moveDiagnosticTests(c1, c2, toMove);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v2);
            l.moveDiagnosticTests(c2, c1, toMove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v1);
            l.positive(c2, toRemove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v2);
            l.negative(c1, toRemove);
        }

        Assert.assertEquals(
                Arrays.asList(
                        new Action(Action.Direction.SAMPLED,      c1),
                        new Action(Action.Direction.MOVED_OUT,    c1),
                        new Action(Action.Direction.MOVED_IN,     c2),
                        new Action(Action.Direction.POSITIVE, c2)
                ),
                l.audit(v1));

        Assert.assertEquals(
                Arrays.asList(
                        new Action(Action.Direction.SAMPLED,   c2),
                        new Action(Action.Direction.MOVED_OUT, c2),
                        new Action(Action.Direction.MOVED_IN,  c1),
                        new Action(Action.Direction.NEGATIVE, c1)
                ),
                l.audit(v2));
    }

    @Test
    public void test05TestingSitesAuditDiagnosticTestsAdded() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        Assert.assertEquals(List.of(new Action(Action.Direction.SAMPLED, v1)), l.audit(c1));
        Assert.assertEquals(List.of(new Action(Action.Direction.SAMPLED, v2)), l.audit(c2));
    }

    @Test
    public void test05ShelvesAuditDiagnosticTestsAddedRemoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v1);
            l.positive(c1, toRemove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v2);
            l.negative(c2, toRemove);
        }

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,      v1),
                        new Action(Action.Direction.POSITIVE, v1)),
                l.audit(c1));

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   v2),
                        new Action(Action.Direction.NEGATIVE, v2)),
                l.audit(c2));
    }

    @Test
    public void test05ShelvesAuditDiagnosticTestsAddedMoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(10);
        TestingSite c2 = l.createTestingSite(10);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v1);
            l.moveDiagnosticTests(c1, c2, toMove);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v2);
            l.moveDiagnosticTests(c2, c1, toMove);
        }

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   v1),
                        new Action(Action.Direction.MOVED_OUT, v1),
                        new Action(Action.Direction.MOVED_IN,  v2)
                ),
                l.audit(c1));

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   v2),
                        new Action(Action.Direction.MOVED_IN,  v1),
                        new Action(Action.Direction.MOVED_OUT, v2)
                ),
                l.audit(c2));
    }


    @Test
    public void test05ShelvesAuditDiagnosticTestsAddedMovedNotEnoughRoom() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v1);
            l.moveDiagnosticTests(c1, c2, toMove);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v2);
            l.moveDiagnosticTests(c2, c1, toMove);
        }

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   v1),
                        new Action(Action.Direction.MOVED_IN,  v2)
                ),
                l.audit(c1));

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   v2),
                        new Action(Action.Direction.MOVED_OUT, v2)
                ),
                l.audit(c2));
    }

    @Test
    public void test05ShelvesAuditDiagnosticTestsAddedMovedRemoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v1);
            l.moveDiagnosticTests(c1, c2, toMove);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v2);
            l.moveDiagnosticTests(c2, c1, toMove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v1);
            l.positive(c2, toRemove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v2);
            l.negative(c1, toRemove);
        }

        Assert.assertEquals(
                Arrays.asList(
                        new Action(Action.Direction.SAMPLED,   v1),
                        new Action(Action.Direction.MOVED_OUT, v1),
                        new Action(Action.Direction.MOVED_IN,  v2),
                        new Action(Action.Direction.NEGATIVE, v2)
                ),
                l.audit(c1));

        Assert.assertEquals(
                Arrays.asList(
                        new Action(Action.Direction.SAMPLED,      v2),
                        new Action(Action.Direction.MOVED_IN,     v1),
                        new Action(Action.Direction.MOVED_OUT,    v2),
                        new Action(Action.Direction.POSITIVE, v1)
                ),
                l.audit(c2));
    }


    @Test
    public void test06Add() {
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
    public void test06Remove() {
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

        runAllThreads(threads);

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

    @Test
    public void test07Move() {
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

        runAllThreads(threads);

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
