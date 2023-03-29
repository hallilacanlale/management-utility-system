package edu.uic.cs454.s22.a4.solution;

import edu.uic.cs454.s22.a4.DiagnosticTest;

public class SolutionDiagTest implements DiagnosticTest {
    Status status = Status.READY;


    @Override
    public Status getStatus() {
        return this.status;
    }
}
