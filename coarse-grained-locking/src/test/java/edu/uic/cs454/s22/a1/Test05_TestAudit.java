package edu.uic.cs454.s22.a1;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Test05_TestAudit {

    // The audit order for multiple items in the same operation is undefined

    @Test
    public void testDiagnosticTestAuditDiagnosticTestsAdded() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        Assert.assertEquals(List.of(new Action(Action.Direction.SAMPLED, c1)), l.audit(v1));
        Assert.assertEquals(List.of(new Action(Action.Direction.SAMPLED, c2)), l.audit(v2));
    }

    @Test
    public void testDiagnosticTestAuditDiagnosticTestsAddedRemoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(1);
        TestingSite c3 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);
        DiagnosticTest v3 = l.createDiagnosticTest(3);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v3);
            l.sampleDiagnosticTests(c3, toAdd);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v1);
            l.positive(c1, toRemove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v2);
            l.negative(c2, toRemove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v3);
            l.invalid(c3, toRemove);
        }

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,  c1),
                        new Action(Action.Direction.POSITIVE, c1)),
                l.audit(v1));

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED, c2),
                        new Action(Action.Direction.NEGATIVE, c2)),
                l.audit(v2));

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED, c3),
                        new Action(Action.Direction.INVALID, c3)),
                l.audit(v3));
    }

    @Test
    public void testDiagnosticTestAuditDiagnosticTestsAddedMoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v1);
            l.moveDiagnosticTests(c1, c2, toMove);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v2);
            l.moveDiagnosticTests(c2, c1, toMove);
        }

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   c1),
                        new Action(Action.Direction.MOVED_OUT, c1),
                        new Action(Action.Direction.MOVED_IN,  c2)
                        ),
                l.audit(v1));

        Assert.assertEquals(
                Arrays.asList(
                        new Action(Action.Direction.SAMPLED,   c2),
                        new Action(Action.Direction.MOVED_OUT, c2),
                        new Action(Action.Direction.MOVED_IN,  c1)
                ),
                l.audit(v2));
    }

    @Test
    public void testDiagnosticTestAuditDiagnosticTestsAddedMovedRemoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v1);
            l.moveDiagnosticTests(c1, c2, toMove);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v2);
            l.moveDiagnosticTests(c2, c1, toMove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v1);
            l.positive(c2, toRemove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v2);
            l.negative(c1, toRemove);
        }

        Assert.assertEquals(
                Arrays.asList(
                        new Action(Action.Direction.SAMPLED,      c1),
                        new Action(Action.Direction.MOVED_OUT,    c1),
                        new Action(Action.Direction.MOVED_IN,     c2),
                        new Action(Action.Direction.POSITIVE, c2)
                ),
                l.audit(v1));

        Assert.assertEquals(
                Arrays.asList(
                        new Action(Action.Direction.SAMPLED,   c2),
                        new Action(Action.Direction.MOVED_OUT, c2),
                        new Action(Action.Direction.MOVED_IN,  c1),
                        new Action(Action.Direction.NEGATIVE, c1)
                ),
                l.audit(v2));
    }

    @Test
    public void testTestingSitesAuditDiagnosticTestsAdded() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        Assert.assertEquals(List.of(new Action(Action.Direction.SAMPLED, v1)), l.audit(c1));
        Assert.assertEquals(List.of(new Action(Action.Direction.SAMPLED, v2)), l.audit(c2));
    }

    @Test
    public void testShelvesAuditDiagnosticTestsAddedRemoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(1);
        TestingSite c2 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v1);
            l.positive(c1, toRemove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v2);
            l.negative(c2, toRemove);
        }

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,      v1),
                        new Action(Action.Direction.POSITIVE, v1)),
                l.audit(c1));

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   v2),
                        new Action(Action.Direction.NEGATIVE, v2)),
                l.audit(c2));
    }

    @Test
    public void testShelvesAuditDiagnosticTestsAddedMoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(10);
        TestingSite c2 = l.createTestingSite(10);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v1);
            l.moveDiagnosticTests(c1, c2, toMove);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v2);
            l.moveDiagnosticTests(c2, c1, toMove);
        }

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   v1),
                        new Action(Action.Direction.MOVED_OUT, v1),
                        new Action(Action.Direction.MOVED_IN,  v2)
                ),
                l.audit(c1));

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   v2),
                        new Action(Action.Direction.MOVED_IN,  v1),
                        new Action(Action.Direction.MOVED_OUT, v2)
                ),
                l.audit(c2));
    }


    @Test
    public void testShelvesAuditDiagnosticTestsAddedMovedNotEnoughRoom() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(1);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v1);
            l.moveDiagnosticTests(c1, c2, toMove);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v2);
            l.moveDiagnosticTests(c2, c1, toMove);
        }

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   v1),
                        new Action(Action.Direction.MOVED_IN,  v2)
                ),
                l.audit(c1));

        Assert.assertEquals(
                List.of(
                        new Action(Action.Direction.SAMPLED,   v2),
                        new Action(Action.Direction.MOVED_OUT, v2)
                ),
                l.audit(c2));
    }

    @Test
    public void testShelvesAuditDiagnosticTestsAddedMovedRemoved() {
        Lab l = Lab.createLab();
        TestingSite c1 = l.createTestingSite(2);
        TestingSite c2 = l.createTestingSite(2);
        DiagnosticTest v1 = l.createDiagnosticTest(1);
        DiagnosticTest v2 = l.createDiagnosticTest(2);

        {
            Set<DiagnosticTest> toAdd = Set.of(v1);
            l.sampleDiagnosticTests(c1, toAdd);
        }

        {
            Set<DiagnosticTest> toAdd = Set.of(v2);
            l.sampleDiagnosticTests(c2, toAdd);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v1);
            l.moveDiagnosticTests(c1, c2, toMove);
        }

        {
            Set<DiagnosticTest> toMove = Set.of(v2);
            l.moveDiagnosticTests(c2, c1, toMove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v1);
            l.positive(c2, toRemove);
        }

        {
            Set<DiagnosticTest> toRemove = Set.of(v2);
            l.negative(c1, toRemove);
        }

        Assert.assertEquals(
                Arrays.asList(
                        new Action(Action.Direction.SAMPLED,   v1),
                        new Action(Action.Direction.MOVED_OUT, v1),
                        new Action(Action.Direction.MOVED_IN,  v2),
                        new Action(Action.Direction.NEGATIVE, v2)
                ),
                l.audit(c1));

        Assert.assertEquals(
                Arrays.asList(
                        new Action(Action.Direction.SAMPLED,      v2),
                        new Action(Action.Direction.MOVED_IN,     v1),
                        new Action(Action.Direction.MOVED_OUT,    v2),
                        new Action(Action.Direction.POSITIVE, v1)
                ),
                l.audit(c2));
    }
}