package edu.uic.cs454.s22.a4;

public interface DiagnosticTest {
    enum Status { READY , SAMPLED , POSITIVE , NEGATIVE , INVALID }

    Status getStatus();
}
