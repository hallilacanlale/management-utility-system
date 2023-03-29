package edu.uic.cs454.s22.a4;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test03_AsyncProgress {

    @Test
    public void testMaxCapacityAsync() {
        Lab l = Lab.createLab();
        int size = 100;
        int test = 200;
        TestingSite c = l.createTestingSite(size);


        Result<Boolean>[] rs = new Result[test];
        Set<DiagnosticTest> expected = new HashSet<>();

        for (int i = 0 ; i < test ; i++) {
            DiagnosticTest v = l.createDiagnosticTest(i);
            rs[i] = l.sampleDiagnosticTestsAsync(c, Set.of(v));
            if (i < size)
                expected.add(v);

        }

        c.startThread();

        for (int i = 0 ; i < test ; i++) {
            if (i < size)
                Assert.assertTrue(rs[i].getResult());
            else
                Assert.assertFalse(rs[i].getResult());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(expected, c.getSampledTests());
    }

    @Test
    public void testDiagnosticTestAlreadyInTestingSiteAsync() {
        int size = 1_000;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        testDiagnosticTestAlreadyInTestingSiteAsync(sequentialIndexes);
        testDiagnosticTestAlreadyInTestingSiteAsync(shuffledIndexesList);
    }

    private void testDiagnosticTestAlreadyInTestingSiteAsync(List<Integer> indexes) {
        int size = indexes.size();
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(size);

        DiagnosticTest[] tests = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            tests[i] = l.createDiagnosticTest(i);
        }

        List<Result<Boolean>> trueResults  = new LinkedList<>();
        List<Result<Boolean>> falseResults = new LinkedList<>();

        for (int i : indexes) {
            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                trueResults.add(l.sampleDiagnosticTestsAsync(c, toAdd));
                falseResults.add(l.sampleDiagnosticTestsAsync(c, toAdd));
                falseResults.add(l.sampleDiagnosticTestsAsync(c, Set.of(tests[i])));
            }

            if (i > 0) {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                toAdd.add(tests[i-1]);
                falseResults.add(l.sampleDiagnosticTestsAsync(c, toAdd));
                falseResults.add(l.sampleDiagnosticTestsAsync(c, Set.of(tests[i], tests[i-1])));
            }

            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                DiagnosticTest anotherDiagnosticTest = l.createDiagnosticTest(size+i);
                toAdd.add(anotherDiagnosticTest);
                falseResults.add(l.sampleDiagnosticTestsAsync(c, toAdd));
                falseResults.add(l.sampleDiagnosticTestsAsync(c, Set.of(tests[i], anotherDiagnosticTest)));
            }

        }

        c.startThread();

        for (Result<Boolean> r : trueResults)
            Assert.assertTrue(r.getResult());

        for (Result<Boolean> r : falseResults)
            Assert.assertFalse(r.getResult());

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(tests), c.getSampledTests());
    }

    @Test
    public void testCapacityAddAsync() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

        Map<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> expected = new HashMap<>();

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v1, v2));

            expected.put(l.getDiagnosticTestsAsync(c), Set.of());
        }

        {
            l.sampleDiagnosticTestsAsync(c, Set.of(v1));
            l.sampleDiagnosticTestsAsync(c, Set.of(v2));

            expected.put(l.getDiagnosticTestsAsync(c), Set.of(v1));
        }

        c.startThread();

        for (Map.Entry<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(v1), c.getSampledTests());
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

        Result<Set<DiagnosticTest>> r1 = l.getDiagnosticTestsAsync(c);

        Assert.assertEquals(size, r1.getResult().size());
        Assert.assertEquals(expected, r1.getResult());
        l.getDiagnosticTests(c).clear();
        Assert.assertEquals(size, l.getDiagnosticTests(c).size());
        Assert.assertEquals(expected, l.getDiagnosticTests(c));

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(expected, c.getSampledTests());
    }



}
