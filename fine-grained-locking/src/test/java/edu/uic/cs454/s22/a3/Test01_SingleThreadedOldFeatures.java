package edu.uic.cs454.s22.a3;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test01_SingleThreadedOldFeatures {
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
/*
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
 */


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

}
