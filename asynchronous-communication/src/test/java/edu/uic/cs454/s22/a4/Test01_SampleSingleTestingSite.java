package edu.uic.cs454.s22.a4;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test01_SampleSingleTestingSite {
    @Test
    public void testMaxCapacity() {
        Lab l = Lab.createLab();
        int size = 10;
        int test = 20;
        TestingSite c = l.createTestingSite(size);

        c.startThread();

        Set<DiagnosticTest> expected = new HashSet<>();

        for (int i = 0 ; i < test ; i++) {
            DiagnosticTest v = l.createDiagnosticTest(i);

            if (i < size) {
                expected.add(v);
                Assert.assertTrue(l.sampleDiagnosticTests(c, Set.of(v)));
            } else {
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(v)));
            }
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(expected, c.getSampledTests());
    }

    @Test
    public void testMaxCapacityAsync() {
        Lab l = Lab.createLab();
        int size = 10;
        int test = 20;
        TestingSite c = l.createTestingSite(size);

        c.startThread();

        Result<Boolean>[] rs = new Result[test];
        Set<DiagnosticTest> expected = new HashSet<>();

        for (int i = 0 ; i < test ; i++) {
            DiagnosticTest v = l.createDiagnosticTest(i);
            rs[i] = l.sampleDiagnosticTestsAsync(c, Set.of(v));
            if (i < size)
                expected.add(v);

        }

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

        c.startThread();

        DiagnosticTest[] tests = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            tests[i] = l.createDiagnosticTest(i);
        }

        for (int i : indexes) {
            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(tests[i])));
            }

            if (i > 0) {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                toAdd.add(tests[i-1]);
                Assert.assertFalse(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(tests[i], tests[i-1])));
            }

            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                DiagnosticTest anotherDiagnosticTest = l.createDiagnosticTest(size+i);
                toAdd.add(anotherDiagnosticTest);
                Assert.assertFalse(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(tests[i], anotherDiagnosticTest)));
            }
        }

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(tests), c.getSampledTests());
    }

    @Test
    public void testDiagnosticTestAlreadyInTestingSiteAsync() {
        int size = 10;
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

        c.startThread();

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

        for (Result<Boolean> r : trueResults)
            Assert.assertTrue(r.getResult());

        for (Result<Boolean> r : falseResults)
            Assert.assertFalse(r.getResult());

        c.stopThread();

        Assert.assertFalse(c.didThrowException());
        Assert.assertEquals(Set.of(tests), c.getSampledTests());
    }


}
