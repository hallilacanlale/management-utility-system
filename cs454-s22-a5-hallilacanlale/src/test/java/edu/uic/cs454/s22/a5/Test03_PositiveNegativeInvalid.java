package edu.uic.cs454.s22.a5;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test03_PositiveNegativeInvalid {

    @Test
    public void testRemoveFromLab() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList1);
        Collections.shuffle(shuffledIndexesList2);

        testRemoveFromLab(sequentialIndexes, sequentialIndexes);
        testRemoveFromLab(shuffledIndexesList1, shuffledIndexesList2);
    }

    public void testRemoveFromLab(List<Integer> vaccineDoseIndexes, List<Integer> shelfIndexes) {

        Lab l = Lab.createLab();
        int size = vaccineDoseIndexes.size();
        TestingSite[] clinics = new TestingSite[shelfIndexes.size()];

        for (int i = 0 ; i < clinics.length ; i++)
            clinics[i] = l.createTestingSite(size);

        DiagnosticTest[] vaccineDoses = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++)
            vaccineDoses[i] = l.createDiagnosticTest(i);

        for (int i : vaccineDoseIndexes) {
            Set<DiagnosticTest> toAdd = new HashSet<>();
            TestingSite s = clinics[shelfIndexes.get(i)];
            TestingSite ss = clinics[shelfIndexes.get((i+1)%shelfIndexes.size())];
            toAdd.add(vaccineDoses[i]);
            Assert.assertFalse(l.negative(s, toAdd));
            Assert.assertFalse(l.positive(ss, toAdd));

            Assert.assertTrue(l.sampleDiagnosticTests(s, toAdd));

            Assert.assertFalse(l.negative(ss, toAdd));
            Assert.assertFalse(l.sampleDiagnosticTests(ss, toAdd));

            if (i%2 == 0)
                Assert.assertTrue(l.positive(s, toAdd));
            else
                Assert.assertTrue(l.negative(s, toAdd));
        }
    }

    @Test
    public void testRemoveFromEmptyTestingSite1() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> vaccineDoses = new HashSet<>();
            vaccineDoses.add(v1);
            vaccineDoses.add(v2);

            Assert.assertFalse(l.negative(c1, vaccineDoses));
            Assert.assertFalse(l.positive(c1, vaccineDoses));
            Assert.assertFalse(l.negative(c2, vaccineDoses));
            Assert.assertFalse(l.positive(c2, vaccineDoses));
        }

        {
            Assert.assertTrue(l.sampleDiagnosticTests(c1, Set.of(v1)));
            Assert.assertTrue(l.sampleDiagnosticTests(c2, Set.of(v2)));
            Assert.assertTrue(l.negative(c1, Set.of(v1)));
            Assert.assertTrue(l.positive(c2, Set.of(v2)));

            Set<DiagnosticTest> vaccineDoses = new HashSet<>();
            vaccineDoses.add(v1);
            vaccineDoses.add(v2);

            Assert.assertFalse(l.negative(c1, vaccineDoses));
            Assert.assertFalse(l.sampleDiagnosticTests(c1, vaccineDoses));
            Assert.assertFalse(l.negative(c2, vaccineDoses));
            Assert.assertFalse(l.sampleDiagnosticTests(c2, vaccineDoses));

            vaccineDoses.clear();
            vaccineDoses.add(v1);
            Assert.assertFalse(l.negative(c1, vaccineDoses));
            Assert.assertFalse(l.sampleDiagnosticTests(c1, vaccineDoses));
            Assert.assertFalse(l.negative(c2, vaccineDoses));
            Assert.assertFalse(l.sampleDiagnosticTests(c2, vaccineDoses));

            vaccineDoses.clear();
            vaccineDoses.add(v2);
            Assert.assertFalse(l.negative(c1, vaccineDoses));
            Assert.assertFalse(l.sampleDiagnosticTests(c1, vaccineDoses));
            Assert.assertFalse(l.negative(c2, vaccineDoses));
            Assert.assertFalse(l.sampleDiagnosticTests(c2, vaccineDoses));
        }
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
//            Assert.assertEquals(expected, l.getDiagnosticTests(c));
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
            l.negative(c, items);

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

            l.sampleDiagnosticTests(c, items);
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
            l.sampleDiagnosticTests(c, items);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }
    }

    @Test
    public void testRemoveDiagnosticTestNotInTestingSiteAndGet() {
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
            l.sampleDiagnosticTests(c, items);

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

        DiagnosticTest[] vaccineDoses = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            vaccineDoses[i] = l.createDiagnosticTest(i);
        }

        for (int i : indexes) {
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(vaccineDoses[i]);
            Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
            if (i%2 == 0) {
                Assert.assertTrue(l.negative(c, toAdd));
            } else {
                Assert.assertTrue(l.positive(c, toAdd));
            }
        }

//        {
//            Set<DiagnosticTest> toAdd = new HashSet<>();
//            for (int i : indexes) {
//                toAdd.add(vaccineDoses[i]);
//            }
//
//            Assert.assertFalse(l.sampleDiagnosticTests(c, toAdd));
//            Assert.assertFalse(l.negative(c, toAdd));
//        }

        {
            for (int i = 0 ; i < size ; i++) {
                vaccineDoses[i] = l.createDiagnosticTest(size+i);
            }

            Set<DiagnosticTest> toAdd = new HashSet<>();
            for (int i : indexes) {
                toAdd.add(vaccineDoses[i]);
            }

            Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
            Assert.assertTrue(l.negative(c, toAdd));
        }

        {
            for (int i = 0 ; i < size ; i++) {
                vaccineDoses[i] = l.createDiagnosticTest((size*2)+i);
            }

            Set<DiagnosticTest> toAdd = new HashSet<>();
            for (int i : indexes) {
                toAdd.add(vaccineDoses[i]);
            }

            Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
            Assert.assertTrue(l.invalid(c, toAdd));
        }
    }


    @Test
    public void testSetIdentity() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        Set<DiagnosticTest> vaccineDoses = new HashSet<>();
        vaccineDoses.add(v1);
        vaccineDoses.add(v2);

        {
            Assert.assertTrue(l.sampleDiagnosticTests(c, vaccineDoses));
            Assert.assertTrue(l.negative(c, vaccineDoses));
            Assert.assertFalse(l.negative(c, vaccineDoses));
            Assert.assertFalse(l.sampleDiagnosticTests(c, vaccineDoses));
        }

        {
            vaccineDoses.clear();
            vaccineDoses.add(v1);
            Assert.assertFalse(l.sampleDiagnosticTests(c, vaccineDoses));
            Assert.assertFalse(l.negative(c, vaccineDoses));
        }

        {
            vaccineDoses.clear();
            vaccineDoses.add(v2);
            Assert.assertFalse(l.sampleDiagnosticTests(c, vaccineDoses));
            Assert.assertFalse(l.negative(c, vaccineDoses));
        }
    }

    @Test
    public void testRemoveDiagnosticTestNotInTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        {
            Set<DiagnosticTest> vaccineDoses = new HashSet<>();
            vaccineDoses.add(v1);
            Assert.assertTrue(l.sampleDiagnosticTests(c, vaccineDoses));

            vaccineDoses.clear();
            vaccineDoses.add(v2);
            Assert.assertFalse(l.sampleDiagnosticTests(c, vaccineDoses));
            Assert.assertFalse(l.negative(c, vaccineDoses));
        }
    }

    @Test
    public void testRemoveDiagnosticTestNotInTestingSites() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> vaccineDoses = new HashSet<>();
            vaccineDoses.add(v1);
            l.sampleDiagnosticTests(c1, vaccineDoses);

            vaccineDoses.clear();
            vaccineDoses.add(v2);
            l.positive(c2, vaccineDoses);

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(c2));
        }
    }

    @Test
    public void testCannotReuseDiagnosticTests() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        testCannotReuseDiagnosticTests(sequentialIndexes);
        testCannotReuseDiagnosticTests(shuffledIndexesList);
    }

    public void testCannotReuseDiagnosticTests(List<Integer> indexes) {
        Lab l = Lab.createLab();
        int size = indexes.size();
        TestingSite c = l.createTestingSite(size);

        DiagnosticTest[] vaccineDoses = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            vaccineDoses[i] = l.createDiagnosticTest(i);
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(vaccineDoses[i]);
            Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
        }

        for (int i : indexes) {
            Set<DiagnosticTest> vs = Set.of(vaccineDoses[i]);
            if (i%2 == 0) {
                Assert.assertTrue(l.negative(c, vs));
            } else {
                Assert.assertTrue(l.invalid(c, vs));
            }

            Assert.assertFalse(l.sampleDiagnosticTests(c, vs));
            Assert.assertFalse(l.positive(c, vs));
        }
    }

    @Test
    public void testRemoveFromTestingSites() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);

        int count = 0;

        {
            DiagnosticTest v1 = l.createDiagnosticTest(count++);
            DiagnosticTest v2 = l.createDiagnosticTest(count++);

            Set<DiagnosticTest> vaccineDoses = new HashSet<>();
            vaccineDoses.add(v1);
            vaccineDoses.add(v2);
            l.sampleDiagnosticTests(c1, vaccineDoses);
            l.positive(c2, vaccineDoses);
            l.negative(c1, vaccineDoses);

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));
        }

        {
            DiagnosticTest v1 = l.createDiagnosticTest(count++);
            DiagnosticTest v2 = l.createDiagnosticTest(count++);
            DiagnosticTest v3 = l.createDiagnosticTest(count++);

            Set<DiagnosticTest> vaccineDoses = new HashSet<>();
            vaccineDoses.add(v1);
            vaccineDoses.add(v3);
            l.sampleDiagnosticTests(c1, vaccineDoses);
            vaccineDoses.clear();
            vaccineDoses.add(v2);
            l.sampleDiagnosticTests(c2, vaccineDoses);


            vaccineDoses.clear();
            vaccineDoses.add(v1);
            l.invalid(c1, vaccineDoses);
            l.sampleDiagnosticTests(c2, vaccineDoses);
            l.sampleDiagnosticTests(c1, vaccineDoses);

            Set<DiagnosticTest> expected = Set.of(v2, v3);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(Set.of(v2), l.getDiagnosticTests(c2));
            Assert.assertEquals(Set.of(v3), l.getDiagnosticTests(c1));

            vaccineDoses.clear();
            vaccineDoses.add(v2);
            l.negative(c2, vaccineDoses);

            expected = Set.of(v3);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(c2));
        }
    }

    @Test
    public void testRemoveFromEmptyTestingSite2() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);

        int count = 0;

        {
            DiagnosticTest v1 = l.createDiagnosticTest(count++);
            DiagnosticTest v2 = l.createDiagnosticTest(count++);

            Set<DiagnosticTest> vaccineDoses = new HashSet<>();
            vaccineDoses.add(v1);
            vaccineDoses.add(v2);

            l.positive(c1, vaccineDoses);
            l.negative(c2, vaccineDoses);

            Set<DiagnosticTest> expected = new HashSet<>();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));
        }

        {
            DiagnosticTest v1 = l.createDiagnosticTest(count++);
            DiagnosticTest v2 = l.createDiagnosticTest(count++);

            Set<DiagnosticTest> vaccineDoses = new HashSet<>();
            vaccineDoses.add(v1);
            vaccineDoses.add(v2);

            l.sampleDiagnosticTests(c1, vaccineDoses);
            l.positive(c1, vaccineDoses);
            l.negative(c2, vaccineDoses);

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));

            vaccineDoses.clear();
            vaccineDoses.add(v1);
            l.negative(c1, vaccineDoses);
            l.negative(c2, vaccineDoses);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));

            vaccineDoses.clear();
            vaccineDoses.add(v2);
            l.negative(c1, vaccineDoses);
            l.sampleDiagnosticTests(c2, vaccineDoses);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));
        }
    }



}
