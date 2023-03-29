package edu.uic.cs454.s22.a4;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test04_PositiveNegativeSingleTestingSite {

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
            l.negative(c, items);

            Set<DiagnosticTest> expected = Set.of();
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
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            items.clear();
            items.add(v2);
            l.invalid(c, items);

            expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
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

            expected.put(l.getDiagnosticTestsAsync(c), Set.of());
        }

        {
            DiagnosticTest v1 = l.createDiagnosticTest(2);
            DiagnosticTest v2 = l.createDiagnosticTest(3);

            l.sampleDiagnosticTestsAsync(c, Set.of(v1));
            l.sampleDiagnosticTestsAsync(c, Set.of(v2));


            l.negativeAsync(c, Set.of(v1));
            expected.put(l.getDiagnosticTestsAsync(c), Set.of(v2));

            l.positiveAsync(c, Set.of(v2));

            expected.put(l.getDiagnosticTestsAsync(c), Set.of());
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

            l.negative(c, items);

            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
        }

        {
            Set<DiagnosticTest> items = new HashSet<>();
            items.add(v1);
            items.add(v2);

            l.sampleDiagnosticTests(c, items);

            Set<DiagnosticTest> expected = Set.of(v1, v2);
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            l.sampleDiagnosticTests(c, items);
            l.negative(c, items);

            expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            items.clear();
            items.add(v1);
            l.negative(c, items);
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            items.clear();
            items.add(v2);
            l.sampleDiagnosticTests(c, items);
            Assert.assertEquals(expected, l.getDiagnosticTests(c));
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

            l.negativeAsync(c, items);

            expected.put(l.getDiagnosticTestsAsync(c), Set.of());
        }

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v1, v2));

            expected.put(l.getDiagnosticTestsAsync(c), Set.of(v1, v2));

            l.sampleDiagnosticTestsAsync(c, Set.of(v1, v2));
            l.negativeAsync(c, Set.of(v1, v2));

//            expected = Set.of();
            expected.put(l.getDiagnosticTestsAsync(c), Set.of());

            l.negativeAsync(c, Set.of(v1));
            expected.put(l.getDiagnosticTestsAsync(c), Set.of());

            l.sampleDiagnosticTestsAsync(c, Set.of(v2));
            expected.put(l.getDiagnosticTestsAsync(c), Set.of());
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
            l.sampleDiagnosticTests(c, items);

            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests(c));

            l.getDiagnosticTests(c).clear();

            Assert.assertEquals(expected, l.getDiagnosticTests(c));
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
            l.sampleDiagnosticTests(c, items);

            expected.put(l.getDiagnosticTestsAsync(c), Set.of(v1));

            ((Set<DiagnosticTest>)l.getDiagnosticTestsAsync(c).getResult()).clear();

            expected.put(l.getDiagnosticTestsAsync(c), Set.of(v1));
        }

        for (Map.Entry<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(v1), c.getSampledTests());
    }

    @Test
    public void testDiagnosticTestChangesCorrectly() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        c.startThread();

        {
            l.sampleDiagnosticTests(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v2.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v2.getStatus());
        }

        {
            l.negative(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.SAMPLED, v2.getStatus());
        }

        {
            l.invalid(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v2.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v2.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v2.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v2.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v1, v2));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.INVALID, v2.getStatus());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(), c.getSampledTests());


    }

    @Test
    public void testDiagnosticTestChangesCorrectlyAsync() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        Result<Boolean> last;

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.READY, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
        }

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.READY, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
        }

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.READY, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
        }

        {
            l.negativeAsync(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.READY, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
        }

        {
            l.positiveAsync(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.READY, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
        }

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.READY, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
        }

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.READY, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
        }

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.READY, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
        }

        {
            last = l.sampleDiagnosticTestsAsync(c, Set.of(v1, v2));

            Assert.assertEquals(DiagnosticTest.Status.READY, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.READY, v2.getStatus());
        }

        c.startThread();

        // Once we get the last result, we know that all the previous async operations have completed
        last.getResult();

        c.stopThread();

        Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
        Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v2.getStatus());

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(), c.getSampledTests());


    }

    @Test
    public void testCannotReuseDiagnosticTests() {
        int size = 1_000;
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

        c.startThread();

        DiagnosticTest[] tests = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            tests[i] = l.createDiagnosticTest(i);
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(tests[i]);
            Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
        }

        for (int i : indexes) {
            Set<DiagnosticTest> vs = Set.of(tests[i]);
            if (i%2 == 0) {
                Assert.assertTrue(l.negative(c, vs));
            } else {
                Assert.assertTrue(l.positive(c, vs));
            }

            Assert.assertFalse(l.sampleDiagnosticTests(c, vs));
            Assert.assertFalse(l.negative(c, vs));
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(), c.getSampledTests());
    }

    @Test
    public void testCannotReuseDiagnosticTestsAsync() {
        int size = 1_000;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        testCannotReuseDiagnosticTestsAsync(sequentialIndexes);
        testCannotReuseDiagnosticTestsAsync(shuffledIndexesList);
    }

    public void testCannotReuseDiagnosticTestsAsync(List<Integer> indexes) {
        Lab l = Lab.createLab();
        int size = indexes.size();
        TestingSite c = l.createTestingSite(size);


        Map<Result<Boolean>, Boolean> expected = new HashMap<>();

        DiagnosticTest[] tests = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            tests[i] = l.createDiagnosticTest(i);
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(tests[i]);
            expected.put(l.sampleDiagnosticTestsAsync(c, toAdd), true);
        }

        for (int i : indexes) {
            Set<DiagnosticTest> vs = Set.of(tests[i]);
            if (i%2 == 0) {
                expected.put(l.negativeAsync(c, vs), true);
            } else {
                expected.put(l.positiveAsync(c, vs), true);
            }

            expected.put(l.positiveAsync(c, vs), false);
            expected.put(l.negativeAsync(c, vs), false);
        }

        c.startThread();

        for (Map.Entry<Result<Boolean>, Boolean> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(), c.getSampledTests());
    }

}
