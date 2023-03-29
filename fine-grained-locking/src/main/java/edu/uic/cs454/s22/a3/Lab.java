package edu.uic.cs454.s22.a3;

import edu.uic.cs454.s22.a3.solution.SolutionLab;

import java.util.List;
import java.util.Set;

public abstract class Lab<TS extends TestingSite, DT extends DiagnosticTest> {
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

    public abstract Set<DT> getDiagnosticTests(List<TS> testingSite);
}