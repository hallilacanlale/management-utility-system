package edu.uic.cs454.s22.a4;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test06_ManyTestingSites {

    @Test
    public void testCapacityAdd() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        c.startThread();

        {
            l.sampleDiagnosticTests(c, Set.of(v1, v2));

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
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
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(v1), c.getSampledTests());
    }

    @Test
    public void testCapacityAddAsync() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        Result<Set<DiagnosticTest>> r1, r2;

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v1, v2));

            r1 = l.getDiagnosticTestsAsync();
        }

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v1));
            l.sampleDiagnosticTestsAsync(c, Set.of(v2));

            r2 = l.getDiagnosticTestsAsync();
        }

        c.startThread();

        Assert.assertEquals(Set.of(),   r1.getResult());
        Assert.assertEquals(Set.of(v1), r2.getResult());

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(v1), c.getSampledTests());
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

    public void testCapacitySameDescription(List<Integer> indexes) {
        int size = indexes.size();
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(size);
        DiagnosticTest[] tests = new DiagnosticTest[size];
        Set<DiagnosticTest> toAdd = new HashSet<>();
        Set<DiagnosticTest> expected = new HashSet<>();

        c.startThread();

        for (int i : indexes) {
            tests[i] = l.createDiagnosticTest(i);
            toAdd.add(tests[i]);
            expected.add(tests[i]);
        }

        l.sampleDiagnosticTests(c, toAdd);

        Assert.assertEquals(size, l.getDiagnosticTests().size());
        Assert.assertEquals(expected, l.getDiagnosticTests());
        l.getDiagnosticTests(c).clear();
        Assert.assertEquals(size, l.getDiagnosticTests().size());
        Assert.assertEquals(expected, l.getDiagnosticTests());

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(expected, c.getSampledTests());
    }


    @Test
    public void testCapacitySameDescriptionAsync() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        testCapacitySameDescriptionAsync(sequentialIndexes);
        testCapacitySameDescriptionAsync(shuffledIndexesList);
    }

    public void testCapacitySameDescriptionAsync(List<Integer> indexes) {
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

        l.sampleDiagnosticTestsAsync(c, toAdd);

        c.startThread();

        Result<Set<DiagnosticTest>> r1 = l.getDiagnosticTestsAsync();

        Assert.assertEquals(size, r1.getResult().size());
        Assert.assertEquals(expected, r1.getResult());
        l.getDiagnosticTests(c).clear();
        Assert.assertEquals(size, l.getDiagnosticTests().size());
        Assert.assertEquals(expected, l.getDiagnosticTests());

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(expected, c.getSampledTests());
    }

    @Test
    public void testRemoveFromTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);

        c.startThread();

        {
            DiagnosticTest v1 = l.createDiagnosticTest(0);
            DiagnosticTest v2 = l.createDiagnosticTest(1);

            Set<DiagnosticTest> items = Set.of(v1, v2);
            l.sampleDiagnosticTests(c, items);
            l.positive(c, items);

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
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
            l.negative(c, items);

            Set<DiagnosticTest> expected = Set.of(v2);
            Assert.assertEquals(expected, l.getDiagnosticTests());

            items.clear();
            items.add(v2);
            l.invalid(c, items);

            expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(), c.getSampledTests());
    }

    @Test
    public void testRemoveFromTestingSiteAsync() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);

        Map<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> expected = new HashMap<>();

        {
            DiagnosticTest v1 = l.createDiagnosticTest(0);
            DiagnosticTest v2 = l.createDiagnosticTest(1);

            Set<DiagnosticTest> items = Set.of(v1, v2);
            l.sampleDiagnosticTestsAsync(c, items);
            l.negativeAsync(c, items);

            expected.put(l.getDiagnosticTestsAsync(), Set.of());
        }

        {
            DiagnosticTest v1 = l.createDiagnosticTest(2);
            DiagnosticTest v2 = l.createDiagnosticTest(3);

            l.sampleDiagnosticTestsAsync(c, Set.of(v1));
            l.sampleDiagnosticTestsAsync(c, Set.of(v2));


            l.positiveAsync(c, Set.of(v1));
            expected.put(l.getDiagnosticTestsAsync(), Set.of(v2));

            l.invalidAsync(c, Set.of(v2));

            expected.put(l.getDiagnosticTestsAsync(), Set.of());
        }

        c.startThread();

        for (Map.Entry<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(), c.getSampledTests());
    }


    @Test
    public void testRemoveFromEmptyTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        c.startThread();

        {
            Set<DiagnosticTest> items = Set.of(v1, v2);

            l.positive(c, items);

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());
        }

        {
            Set<DiagnosticTest> items = new HashSet<>();
            items.add(v1);
            items.add(v2);

            l.sampleDiagnosticTests(c, items);

            Set<DiagnosticTest> expected = Set.of(v1, v2);
            Assert.assertEquals(expected, l.getDiagnosticTests());

            l.positive(c, items);
            l.negative(c, items);

            expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests());

            items.clear();
            items.add(v1);
            l.positive(c, items);
            Assert.assertEquals(expected, l.getDiagnosticTests());

            items.clear();
            items.add(v2);
            l.invalid(c, items);
            Assert.assertEquals(expected, l.getDiagnosticTests());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(), c.getSampledTests());
    }

    @Test
    public void testRemoveFromEmptyTestingSiteAsync() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        Map<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> expected = new HashMap<>();

        {
            Set<DiagnosticTest> items = Set.of(v1, v2);

            l.invalidAsync(c, items);

            expected.put(l.getDiagnosticTestsAsync(c), Set.of());
        }

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v1, v2));

            expected.put(l.getDiagnosticTestsAsync(), Set.of(v1, v2));

            l.positiveAsync(c, Set.of(v1, v2));
            l.negativeAsync(c, Set.of(v1, v2));

//            expected = Set.of();
            expected.put(l.getDiagnosticTestsAsync(), Set.of());

            l.positiveAsync(c, Set.of(v1));
            expected.put(l.getDiagnosticTestsAsync(), Set.of());

            l.negativeAsync(c, Set.of(v2));
            expected.put(l.getDiagnosticTestsAsync(), Set.of());
        }

        c.startThread();

        for (Map.Entry<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(), c.getSampledTests());
    }

    @Test
    public void testRemoveDiagnosticTestNotInTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        c.startThread();

        {
            Set<DiagnosticTest> items = new HashSet<>();
            items.add(v1);
            l.sampleDiagnosticTests(c, Set.of(v1));

            items.clear();
            items.add(v2);
            l.positive(c, items);

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests());

            l.getDiagnosticTests(c).clear();

            Assert.assertEquals(expected, l.getDiagnosticTests());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(v1), c.getSampledTests());
    }

    @Test
    public void testRemoveDiagnosticTestNotInTestingSiteAsync() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        c.startThread();

        Map<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> expected = new HashMap<>();

        {
            Set<DiagnosticTest> items = new HashSet<>();
            items.add(v1);
            l.sampleDiagnosticTests(c, Set.of(v1));

            items.clear();
            items.add(v2);
            l.invalid(c, items);

            expected.put(l.getDiagnosticTestsAsync(), Set.of(v1));

            ((Set<DiagnosticTest>)l.getDiagnosticTestsAsync().getResult()).clear();

            expected.put(l.getDiagnosticTestsAsync(), Set.of(v1));
        }

        for (Map.Entry<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(v1), c.getSampledTests());
    }

    @Test
    public void testMaxCapacityTestingSites() {
        Lab l = Lab.createLab();
        int n = 1_000;
        TestingSite[] clinics = new TestingSite[n];
        Set<DiagnosticTest>[] expected = new Set[clinics.length];
        Set<DiagnosticTest> everything = new HashSet<>();

        for (int i = 0 ; i < clinics.length ; i++) {
            clinics[i] = l.createTestingSite(i + 1);
            expected[i] = new HashSet<>();
        }

        for (TestingSite c : clinics)
            c.startThread();

        int count = 0;


        for (int i = 0 ; i < n ; i++) {
            for (int j = 0 ; i < clinics.length ; i++) {
                TestingSite s = clinics[j];
                DiagnosticTest test = l.createDiagnosticTest(count++);
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(test);

                Assert.assertEquals(everything, l.getDiagnosticTests());

                if (i <= j) {
                    Assert.assertTrue(l.sampleDiagnosticTests(s, toAdd));
                    expected[i].add(test);
                    everything.add(test);
                } else {
                    Assert.assertFalse(l.sampleDiagnosticTests(s, toAdd));
                }
            }
        }


        for (TestingSite c : clinics)
            c.stopThread();

        for (int i = 0 ; i < clinics.length ; i++) {
            TestingSite c = clinics[i];
            Assert.assertFalse(c.didThrowException());
            Assert.assertEquals(expected[i], c.getSampledTests());
        }

    }

    @Test
    public void testMaxCapacityTestingSitesAsync() {
        Lab l = Lab.createLab();
        int n = 1_000;
        TestingSite[] clinics = new TestingSite[n];
        Set<DiagnosticTest>[] expected = new Set[clinics.length];
        Set<DiagnosticTest> everything = new HashSet<>();

        for (int i = 0 ; i < clinics.length ; i++) {
            clinics[i] = l.createTestingSite(i + 1);
            expected[i] = new HashSet<>();
        }

        int count = 0;

        Map<Result<Boolean>, Boolean> expectedResults = new HashMap<>();
        Map<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> expectedContents = new HashMap<>();

        for (int i = 0 ; i < n ; i++) {
            for (int j = 0 ; i < clinics.length ; i++) {
                TestingSite s = clinics[j];
                DiagnosticTest test = l.createDiagnosticTest(count++);
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(test);

                expectedContents.put(l.getDiagnosticTestsAsync(), new HashSet<>(everything));

                if (i <= j) {
                    expectedResults.put(l.sampleDiagnosticTestsAsync(s, toAdd), true);
                    expected[i].add(test);
                    everything.add(test);
                } else {
                    expectedResults.put(l.sampleDiagnosticTestsAsync(s, toAdd), false);
                }
            }
        }

        for (TestingSite c : clinics)
            c.startThread();

        for (Map.Entry<Result<Boolean>, Boolean> entry : expectedResults.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        for (TestingSite c : clinics)
            c.stopThread();

        for (Map.Entry<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> entry : expectedContents.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        for (int i = 0 ; i < clinics.length ; i++) {
            TestingSite c = clinics[i];
            Assert.assertFalse(c.didThrowException());
            Assert.assertEquals(expected[i], c.getSampledTests());
        }

    }

    @Test
    public void testRemoveFromLab() {
        int size = 1_000;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList1);
        Collections.shuffle(shuffledIndexesList2);

        testRemoveFromTestingSite(sequentialIndexes, sequentialIndexes);
        testRemoveFromTestingSite(shuffledIndexesList1, shuffledIndexesList2);
    }

    public void testRemoveFromTestingSite(List<Integer> testIndexes, List<Integer> shelfIndexes) {

        Lab l = Lab.createLab();
        int size = testIndexes.size();
        TestingSite[] clinics = new TestingSite[shelfIndexes.size()];

        for (int i = 0 ; i < clinics.length ; i++)
            clinics[i] = l.createTestingSite(size);

        DiagnosticTest[] tests = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++)
            tests[i] = l.createDiagnosticTest(i);

        for (TestingSite c : clinics)
            c.startThread();

        for (int i : testIndexes) {
            Set<DiagnosticTest> toAdd = new HashSet<>();
            TestingSite s = clinics[shelfIndexes.get(i)];
            TestingSite ss = clinics[shelfIndexes.get((i+1)%shelfIndexes.size())];
            toAdd.add(tests[i]);
            Assert.assertFalse(l.invalid(s, toAdd));
            Assert.assertFalse(l.positive(ss, toAdd));

            Assert.assertTrue(l.sampleDiagnosticTests(s, toAdd));

            Assert.assertFalse(l.negative(ss, toAdd));
            Assert.assertFalse(l.positive(ss, toAdd));

            if (i%2 == 0)
                Assert.assertTrue(l.positive(s, toAdd));
            else
                Assert.assertTrue(l.negative(s, toAdd));
        }

        for (TestingSite c : clinics) {
            c.stopThread();

            Assert.assertFalse(c.didThrowException());
            Assert.assertEquals(Set.of(), c.getSampledTests());
        }
    }

    @Test
    public void testRemoveFromLabAsync() {
        int size = 1_000;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList1);
        Collections.shuffle(shuffledIndexesList2);

        testRemoveFromTestingSiteAsync(sequentialIndexes, sequentialIndexes);
        testRemoveFromTestingSiteAsync(shuffledIndexesList1, shuffledIndexesList2);
    }

    public void testRemoveFromTestingSiteAsync(List<Integer> testIndexes, List<Integer> shelfIndexes) {

        Lab l = Lab.createLab();
        int size = testIndexes.size();
        TestingSite[] clinics = new TestingSite[shelfIndexes.size()];

        for (int i = 0 ; i < clinics.length ; i++)
            clinics[i] = l.createTestingSite(size);

        DiagnosticTest[] tests = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++)
            tests[i] = l.createDiagnosticTest(i);

        Map<Result<Boolean>, Boolean> expected = new HashMap<>();

        for (int i : testIndexes) {
            Set<DiagnosticTest> toAdd = new HashSet<>();
            TestingSite s = clinics[shelfIndexes.get(i)];
            TestingSite ss = clinics[shelfIndexes.get((i+1)%shelfIndexes.size())];
            toAdd.add(tests[i]);
            expected.put(l.positiveAsync(s, toAdd), false);
            expected.put(l.negativeAsync(ss, toAdd), false);

            expected.put(l.sampleDiagnosticTestsAsync(s, toAdd), true);

            expected.put(l.invalidAsync(ss, toAdd), false);
            expected.put(l.positiveAsync(ss, toAdd), false);

            if (i%2 == 0)
                expected.put(l.negativeAsync(s, toAdd), true);
            else
                expected.put(l.positiveAsync(s, toAdd), true);
        }

        for (TestingSite c : clinics)
            c.startThread();

        for (Map.Entry<Result<Boolean>, Boolean> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        for (TestingSite c : clinics) {
            c.stopThread();

            Assert.assertFalse(c.didThrowException());
            Assert.assertEquals(Set.of(), c.getSampledTests());
        }
    }

    @Test
    public void testRemoveFromEmptyTestingSites() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        c1.startThread();
        c2.startThread();

        {
            Set<DiagnosticTest> tests = new HashSet<>();
            tests.add(v1);
            tests.add(v2);

            Assert.assertFalse(l.positive(c1, tests));
            Assert.assertFalse(l.negative(c1, tests));
            Assert.assertFalse(l.positive(c2, tests));
            Assert.assertFalse(l.negative(c2, tests));
        }

        {
            Assert.assertTrue(l.sampleDiagnosticTests(c1, Set.of(v1)));
            Assert.assertTrue(l.sampleDiagnosticTests(c2, Set.of(v2)));
            Assert.assertTrue(l.positive(c1, Set.of(v1)));
            Assert.assertTrue(l.negative(c2, Set.of(v2)));

            Set<DiagnosticTest> tests = new HashSet<>();
            tests.add(v1);
            tests.add(v2);

            Assert.assertFalse(l.positive(c1, tests));
            Assert.assertFalse(l.negative(c1, tests));
            Assert.assertFalse(l.positive(c2, tests));
            Assert.assertFalse(l.negative(c2, tests));

            tests.clear();
            tests.add(v1);
            Assert.assertFalse(l.positive(c1, tests));
            Assert.assertFalse(l.negative(c1, tests));
            Assert.assertFalse(l.positive(c2, tests));
            Assert.assertFalse(l.negative(c2, tests));

            tests.clear();
            tests.add(v2);
            Assert.assertFalse(l.positive(c1, tests));
            Assert.assertFalse(l.negative(c1, tests));
            Assert.assertFalse(l.positive(c2, tests));
            Assert.assertFalse(l.negative(c2, tests));
        }

        c1.stopThread();
        c2.stopThread();

        Assert.assertFalse(c1.didThrowException());
        Assert.assertEquals(Set.of(), c1.getSampledTests());

        Assert.assertFalse(c2.didThrowException());
        Assert.assertEquals(Set.of(), c2.getSampledTests());
    }

    @Test
    public void testRemoveFromEmptyTestingSitesAsync() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        Map<Result<Boolean>, Boolean> expected = new HashMap<>();

        {
            Set<DiagnosticTest> tests = new HashSet<>();
            tests.add(v1);
            tests.add(v2);

            expected.put(l.positiveAsync(c1, tests), false);
            expected.put(l.negativeAsync(c1, tests), false);
            expected.put(l.positiveAsync(c2, tests), false);
            expected.put(l.negativeAsync(c2, tests), false);
        }

        {
            expected.put(l.sampleDiagnosticTestsAsync(c1, Set.of(v1)), true);
            expected.put(l.sampleDiagnosticTestsAsync(c2, Set.of(v2)), true);
            expected.put(l.positiveAsync(c1, Set.of(v1)), true);
            expected.put(l.negativeAsync(c2, Set.of(v2)), true);

            Set<DiagnosticTest> tests = new HashSet<>();
            tests.add(v1);
            tests.add(v2);

            expected.put(l.positiveAsync(c1, tests), false);
            expected.put(l.negativeAsync(c1, tests), false);
            expected.put(l.positiveAsync(c2, tests), false);
            expected.put(l.negativeAsync(c2, tests), false);
        }

        {
            Set<DiagnosticTest> tests = new HashSet<>();
            tests.add(v1);
            expected.put(l.positiveAsync(c1, tests), false);
            expected.put(l.negativeAsync(c1, tests), false);
            expected.put(l.positiveAsync(c2, tests), false);
            expected.put(l.negativeAsync(c2, tests), false);
        }
        {

            Set<DiagnosticTest> tests = new HashSet<>();
            tests.add(v2);
            expected.put(l.positiveAsync(c1, tests), false);
            expected.put(l.negativeAsync(c1, tests), false);
            expected.put(l.positiveAsync(c2, tests), false);
            expected.put(l.negativeAsync(c2, tests), false);
        }

        c1.startThread();
        c2.startThread();

        for (Map.Entry<Result<Boolean>, Boolean> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        c1.stopThread();
        c2.stopThread();

        Assert.assertFalse(c1.didThrowException());
        Assert.assertEquals(Set.of(), c1.getSampledTests());

        Assert.assertFalse(c2.didThrowException());
        Assert.assertEquals(Set.of(), c2.getSampledTests());
    }



}
