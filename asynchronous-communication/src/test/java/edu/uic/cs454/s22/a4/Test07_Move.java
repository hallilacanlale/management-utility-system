package edu.uic.cs454.s22.a4;

import org.junit.Assert;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Test07_Move {

    @Test
    public void moveDiagnosticTestToEmptyTestingSite() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);
        DiagnosticTest v3 = l.createDiagnosticTest(3);
        DiagnosticTest v4 = l.createDiagnosticTest(4);

        c1.startThread();
        c2.startThread();

        Set<DiagnosticTest> toAdd = Set.of(v1, v2);
        Assert.assertTrue(l.sampleDiagnosticTests(c1, toAdd));

        Set<DiagnosticTest> toMove = Set.of(v1, v2);
        Assert.assertTrue(l.moveDiagnosticTests(c1, c2, toMove));
        Assert.assertFalse(l.positive(c1, toMove));
        Assert.assertFalse(l.negative(c1, toMove));

        Set<DiagnosticTest> toAddMore = Set.of(v3, v4);
        Assert.assertTrue(l.sampleDiagnosticTests(c1, toAddMore));
        Assert.assertTrue(l.invalid(c2, toMove));

        c1.stopThread();
        c2.stopThread();

        Assert.assertFalse(c1.didThrowException());
        Assert.assertFalse(c2.didThrowException());

        Assert.assertEquals(Set.of(v3, v4), c1.getSampledTests());
        Assert.assertEquals(Set.of(), c2.getSampledTests());
    }

    @Test
    public void moveDiagnosticTestToEmptyTestingSiteAsync() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);
        DiagnosticTest v3 = l.createDiagnosticTest(3);
        DiagnosticTest v4 = l.createDiagnosticTest(4);

        LinkedList<Map.Entry<Result<Boolean>, Boolean>> expected = new LinkedList<>();

        Set<DiagnosticTest> toAdd = Set.of(v1, v2);
        expected.addLast(new AbstractMap.SimpleEntry<>(l.sampleDiagnosticTestsAsync(c1, toAdd), true));

        Set<DiagnosticTest> toMove = Set.of(v1, v2);
        expected.addLast(new AbstractMap.SimpleEntry<>(l.moveDiagnosticTestsAsync(c1, c2, toMove), true));
        expected.addLast(new AbstractMap.SimpleEntry<>(l.positiveAsync(c1, toMove), false));
        expected.addLast(new AbstractMap.SimpleEntry<>(l.negativeAsync(c1, toMove), false));

        Set<DiagnosticTest> toAddMore = Set.of(v3, v4);
        expected.addLast(new AbstractMap.SimpleEntry<>(l.sampleDiagnosticTestsAsync(c1, toAddMore), true));

        c1.startThread();
        c2.startThread();

        for (Map.Entry<Result<Boolean>, Boolean> entry : expected) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        c1.stopThread();

        Assert.assertEquals(l.invalidAsync(c2, toMove).getResult(), true);

        c2.stopThread();

        Assert.assertFalse(c1.didThrowException());
        Assert.assertFalse(c2.didThrowException());

        Assert.assertEquals(Set.of(v3, v4), c1.getSampledTests());
        Assert.assertEquals(Set.of(), c2.getSampledTests());
    }

    @Test
    public void moveToFullTestingSite() {
        Lab l = Lab.createLab();
        TestingSite from = l.createTestingSite(1);
        TestingSite to = l.createTestingSite(1);

        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        from.startThread();
        to.startThread();

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

        from.stopThread();
        to.stopThread();

        Assert.assertFalse(from.didThrowException());
        Assert.assertFalse(to.didThrowException());

        Assert.assertEquals(Set.of(v1), from.getSampledTests());
        Assert.assertEquals(Set.of(v2), to.getSampledTests());
    }

    @Test
    public void moveToFullTestingSiteAsync() {
        Lab l = Lab.createLab();
        TestingSite from = l.createTestingSite(1);
        TestingSite to = l.createTestingSite(1);

        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        Map<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> expected = new HashMap<>();
        Result<Boolean> moveResult;

        l.sampleDiagnosticTestsAsync(from, Set.of(v1));
        l.sampleDiagnosticTestsAsync(to, Set.of(v2));

        expected.put(l.getDiagnosticTestsAsync(from), Set.of(v1));
        expected.put(l.getDiagnosticTestsAsync(to), Set.of(v2));

        moveResult = l.moveDiagnosticTestsAsync(from, to, Set.of(v1));

        from.startThread();
        to.startThread();

        Assert.assertFalse(moveResult.getResult());

        expected.put(l.getDiagnosticTestsAsync(from), Set.of(v1));
        expected.put(l.getDiagnosticTestsAsync(to), Set.of(v2));
        expected.put(l.getDiagnosticTestsAsync(), Set.of(v1, v2));

        for (Map.Entry<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        from.stopThread();
        to.stopThread();

        Assert.assertFalse(from.didThrowException());
        Assert.assertFalse(to.didThrowException());

        Assert.assertEquals(Set.of(v1), from.getSampledTests());
        Assert.assertEquals(Set.of(v2), to.getSampledTests());
    }


    @Test
    public void moveInvalidDose() {
        Lab l = Lab.createLab();
        TestingSite from = l.createTestingSite(1);
        TestingSite to = l.createTestingSite(1);

        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        from.startThread();
        to.startThread();

        l.sampleDiagnosticTests(from, Set.of(v1));

        {
            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests(from));
        }

        {
            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests(to));
        }

        Assert.assertFalse(l.moveDiagnosticTests(from, to, Set.of(v2)));

        {
            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests(from));
        }

        {
            Set<DiagnosticTest> expected = Set.of();
            Assert.assertEquals(expected, l.getDiagnosticTests(to));
        }

        from.stopThread();
        to.stopThread();

        Assert.assertFalse(from.didThrowException());
        Assert.assertFalse(to.didThrowException());

        Assert.assertEquals(Set.of(v1), from.getSampledTests());
        Assert.assertEquals(Set.of(), to.getSampledTests());
    }

    @Test
    public void moveInvalidDoseAsync() {
        Lab l = Lab.createLab();
        TestingSite from = l.createTestingSite(1);
        TestingSite to = l.createTestingSite(1);

        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        Map<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> expected = new HashMap<>();
        Result<Boolean> moveResult;

        l.sampleDiagnosticTestsAsync(from, Set.of(v1));

        expected.put(l.getDiagnosticTestsAsync(from), Set.of(v1));
        expected.put(l.getDiagnosticTestsAsync(to), Set.of());
//        expected.put(l.getDiagnosticTestsAsync(), Set.of(v1));

        moveResult = l.moveDiagnosticTestsAsync(from, to, Set.of(v2));

        from.startThread();
        to.startThread();

        Assert.assertFalse(moveResult.getResult());

        expected.put(l.getDiagnosticTestsAsync(from), Set.of(v1));
        expected.put(l.getDiagnosticTestsAsync(to), Set.of());
        expected.put(l.getDiagnosticTestsAsync(), Set.of(v1));

        for (Map.Entry<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        from.stopThread();
        to.stopThread();

        Assert.assertFalse(from.didThrowException());
        Assert.assertFalse(to.didThrowException());

        Assert.assertEquals(Set.of(v1), from.getSampledTests());
        Assert.assertEquals(Set.of(), to.getSampledTests());
    }

    @Test
    public void moveInvalidDoseToFullTestingSite() {
        Lab l = Lab.createLab();
        TestingSite from = l.createTestingSite(1);
        TestingSite to = l.createTestingSite(1);

        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);
        DiagnosticTest v3 = l.createDiagnosticTest(3);

        from.startThread();
        to.startThread();

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

        Assert.assertFalse(l.moveDiagnosticTests(from, to, Set.of(v3)));

        {
            Set<DiagnosticTest> expected = Set.of(v1);
            Assert.assertEquals(expected, l.getDiagnosticTests(from));
        }

        {
            Set<DiagnosticTest> expected = Set.of(v2);
            Assert.assertEquals(expected, l.getDiagnosticTests(to));
        }

        from.stopThread();
        to.stopThread();

        Assert.assertFalse(from.didThrowException());
        Assert.assertFalse(to.didThrowException());

        Assert.assertEquals(Set.of(v1), from.getSampledTests());
        Assert.assertEquals(Set.of(v2), to.getSampledTests());
    }

    @Test
    public void moveInvalidDoseToFullTestingSiteAsync() {
        Lab l = Lab.createLab();
        TestingSite from = l.createTestingSite(1);
        TestingSite to = l.createTestingSite(1);

        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);
        DiagnosticTest v3 = l.createDiagnosticTest(3);

        Map<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> expected = new HashMap<>();
        Result<Boolean> moveResult;

        l.sampleDiagnosticTestsAsync(from, Set.of(v1));
        l.sampleDiagnosticTestsAsync(to, Set.of(v2));

        expected.put(l.getDiagnosticTestsAsync(from), Set.of(v1));
        expected.put(l.getDiagnosticTestsAsync(to), Set.of(v2));
        expected.put(l.getDiagnosticTestsAsync(), Set.of(v1, v2));

        moveResult = l.moveDiagnosticTestsAsync(from, to, Set.of(v3));

        from.startThread();
        to.startThread();

        Assert.assertFalse(moveResult.getResult());

        expected.put(l.getDiagnosticTestsAsync(from), Set.of(v1));
        expected.put(l.getDiagnosticTestsAsync(to), Set.of(v2));
        expected.put(l.getDiagnosticTestsAsync(), Set.of(v1, v2));

        for (Map.Entry<Result<Set<DiagnosticTest>>, Set<DiagnosticTest>> entry : expected.entrySet()) {
            Assert.assertEquals(entry.getValue(), entry.getKey().getResult());
        }

        from.stopThread();
        to.stopThread();

        Assert.assertFalse(from.didThrowException());
        Assert.assertFalse(to.didThrowException());

        Assert.assertEquals(Set.of(v1), from.getSampledTests());
        Assert.assertEquals(Set.of(v2), to.getSampledTests());
    }
}
