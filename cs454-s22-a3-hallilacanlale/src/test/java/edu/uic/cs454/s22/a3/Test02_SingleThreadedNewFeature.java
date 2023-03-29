package edu.uic.cs454.s22.a3;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test02_SingleThreadedNewFeature {

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
            Set<DiagnosticTest> expected = Set.of(v1,v2);
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(to, from)));
        }

        Assert.assertFalse(l.moveDiagnosticTests(from, to, Set.of(v1)));

        {
            Set<DiagnosticTest> expected = Set.of(v1, v2);
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(from, to)));
        }

    }

    @Test
    public void testCapacityAdd() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(10);
        TestingSite c3 = l.createTestingSite(1);
        TestingSite c4 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);
        DiagnosticTest v3 = l.createDiagnosticTest(3);
        DiagnosticTest v4 = l.createDiagnosticTest(4);
        DiagnosticTest v5 = l.createDiagnosticTest(5);

        {
            l.sampleDiagnosticTests(c3, Set.of(v3));
            l.sampleDiagnosticTests(c4, Set.of(v4));
            l.sampleDiagnosticTests(c4, Set.of(v5));
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v1,v2);
            l.sampleDiagnosticTests(c1, toAdd);

            Set<DiagnosticTest> expected = Set.of(v3, v4, v5);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(c1));
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(c2));
        }

        {
            Set<DiagnosticTest> toAdd = new HashSet<>();
            toAdd.add(v1);
            l.sampleDiagnosticTests(c1, toAdd);
            toAdd.clear();
            toAdd.add(v2);
            l.sampleDiagnosticTests(c1, toAdd);

            Assert.assertEquals(Set.of(v1), l.getDiagnosticTests(c1));
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(c2));
            Assert.assertEquals(Set.of(v1,v3), l.getDiagnosticTests(List.of(c1,c2,c3)));
            Assert.assertEquals(Set.of(v1,v3,v4,v5), l.getDiagnosticTests(List.of(c1,c2,c3,c4)));
            Assert.assertEquals(Set.of(v3,v4,v5), l.getDiagnosticTests(List.of(c2,c3,c4)));
            Assert.assertEquals(Set.of(v1,v3,v4,v5), l.getDiagnosticTests(List.of(c3,c4,c1)));
            Assert.assertEquals(Set.of(v3,v4,v5), l.getDiagnosticTests(List.of(c4,c3)));
            Assert.assertEquals(Set.of(v1,v3,v4,v5), l.getDiagnosticTests());
        }
    }

    @Test
    public void testCapacityDuplicatedDiagnosticTest() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(10);
        TestingSite c2 = l.createTestingSite(10);
        TestingSite c3 = l.createTestingSite(10);
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
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c2, c1)));
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
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c2,c1)));
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c2,c1,c3)));
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
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c1,c2)));
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c3,c1,c2)));
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
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c1,c2)));
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
            l.positive(c1, vaccineDoses);
            l.sampleDiagnosticTests(c2, vaccineDoses);
            l.sampleDiagnosticTests(c1, vaccineDoses);

            Set<DiagnosticTest> expected = Set.of(v2, v3);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(Set.of(v2), l.getDiagnosticTests(c2));
            Assert.assertEquals(Set.of(v3), l.getDiagnosticTests(c1));
            Assert.assertEquals(Set.of(v3,v2), l.getDiagnosticTests(List.of(c2,c1)));

            vaccineDoses.clear();
            vaccineDoses.add(v2);
            l.negative(c2, vaccineDoses);

            expected = Set.of(v3);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(Set.of(), l.getDiagnosticTests(c2));
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c2,c1)));
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

            Set<DiagnosticTest> vaccineDoses = new HashSet<>();
            vaccineDoses.add(v1);
            vaccineDoses.add(v2);

            l.positive(c1, vaccineDoses);
            l.positive(c2, vaccineDoses);

            Set<DiagnosticTest> expected = new HashSet<>();
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c2,c1)));
        }

        {
            DiagnosticTest v1 = l.createDiagnosticTest(count++);
            DiagnosticTest v2 = l.createDiagnosticTest(count++);

            Set<DiagnosticTest> vaccineDoses = new HashSet<>();
            vaccineDoses.add(v1);
            vaccineDoses.add(v2);

            l.sampleDiagnosticTests(c1, vaccineDoses);
            l.positive(c1, vaccineDoses);
            l.positive(c2, vaccineDoses);

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
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c1,c2)));

            vaccineDoses.clear();
            vaccineDoses.add(v2);
            l.negative(c1, vaccineDoses);
            l.positive(c2, vaccineDoses);
            Assert.assertEquals(expected, l.getDiagnosticTests());
            Assert.assertEquals(expected, l.getDiagnosticTests(c1));
            Assert.assertEquals(expected, l.getDiagnosticTests(c2));
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c1,c2)));
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
            Assert.assertEquals(expected, l.getDiagnosticTests(List.of(c2,c1)));
        }
    }
}
