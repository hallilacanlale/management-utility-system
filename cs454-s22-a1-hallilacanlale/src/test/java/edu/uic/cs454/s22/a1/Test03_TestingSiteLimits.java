package edu.uic.cs454.s22.a1;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test03_TestingSiteLimits {

    @Test
    public void testMaxCapacity() {
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
    public void testNoRoomInDestination() {
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
    public void testRemoveFromLab() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList1);
        Collections.shuffle(shuffledIndexesList2);

        testRemoveFromTestingSite(sequentialIndexes, sequentialIndexes);
        testRemoveFromTestingSite(shuffledIndexesList1, shuffledIndexesList2);
    }

    public void testRemoveFromTestingSite(List<Integer> diagTestsIndexes, List<Integer> shelfIndexes) {

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
    public void testRemoveFromEmptyTestingSite() {
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
}