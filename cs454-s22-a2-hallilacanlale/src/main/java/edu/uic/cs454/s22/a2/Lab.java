package edu.uic.cs454.s22.a2;

import edu.uic.cs454.s22.a2.solution.SolutionDiagTest;
import edu.uic.cs454.s22.a2.solution.SolutionLab;
import edu.uic.cs454.s22.a2.solution.JavaLock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Lab<TS extends TestingSite, DT extends DiagnosticTest> {
    public final HashSet<SolutionDiagTest> tests = new HashSet<>();

    public static CS454Lock createLock() {
        return new JavaLock();
    }

    public static Lab<?, ?> createLab() {
        return new SolutionLab();
    }

    public abstract TS createTestingSite(int capacity);

    public abstract DT createDiagnosticTest(int id);

    public abstract boolean sampleDiagnosticTests(TS testingSite, Set<DT> diagnosticTests);

    public abstract boolean positive(TS testingSite, Set<DT> diagnosticTests);

    public abstract boolean negative(TS testingSite, Set<DT> diagnosticTests);

    public abstract boolean invalid(TS testingSite, Set<DT> diagnosticTests);

    public abstract boolean moveDiagnosticTests(TS from, TS to, Set<DT> diagnosticTests);

    public abstract Set<DT> getDiagnosticTests();

    public abstract Set<DT> getDiagnosticTests(TS testingSite);

    public abstract List<Action<DT>> audit(TS testingSite);

    public abstract List<Action<TS>> audit(DT diagnosticTest);
}
