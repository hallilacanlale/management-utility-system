package edu.uic.cs454.s22.a5;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class Test04_MoveGetStatus {
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
        Assert.assertFalse(l.sampleDiagnosticTests(c1, toMove));

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
    public void testDiagnosticTestChangesCorrectly() {
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(0);
        DiagnosticTest v2 = l.createDiagnosticTest(1);

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
            l.positive(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v2.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v2.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v1));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v2.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v2));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v2.getStatus());
        }

        {
            l.sampleDiagnosticTests(c, Set.of(v1, v2));

            Assert.assertEquals(DiagnosticTest.Status.NEGATIVE, v1.getStatus());
            Assert.assertEquals(DiagnosticTest.Status.POSITIVE, v2.getStatus());
        }


    }


}
