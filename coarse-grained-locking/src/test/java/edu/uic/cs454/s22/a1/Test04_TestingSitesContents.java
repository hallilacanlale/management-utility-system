package edu.uic.cs454.s22.a1;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test04_TestingSitesContents {
    @Test
    public void moveDiagnosticTestFromEmptyTestingSite() {
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
    public void testDiagnosticTestAlreadyInTestingSite() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        testDiagnosticTestAlreadyInTestingSite(sequentialIndexes);
        testDiagnosticTestAlreadyInTestingSite(shuffledIndexesList);
    }

    private void testDiagnosticTestAlreadyInTestingSite(List<Integer> indexes) {
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
    public void testDiagnosticTestAlreadyInLab() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList1);
        Collections.shuffle(shuffledIndexesList2);

        testDiagnosticTestAlreadyInLab(sequentialIndexes, sequentialIndexes);
        testDiagnosticTestAlreadyInLab(shuffledIndexesList1, shuffledIndexesList2);
    }

    private void testDiagnosticTestAlreadyInLab(List<Integer> diagTestsIndexes, List<Integer> shelfIndexes) {
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
    public void testCapacityAdd() {
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
    public void testRemoveFromTestingSite() {
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
    public void testRemoveFromEmptyTestingSite() {
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
    public void testRemoveDiagnosticTestNotInTestingSite() {
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