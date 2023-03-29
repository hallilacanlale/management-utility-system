package edu.uic.cs454.s22.a2.solution;

import edu.uic.cs454.s22.a2.Action;
import edu.uic.cs454.s22.a2.CS454Lock;
import edu.uic.cs454.s22.a2.DiagnosticTest;

import java.util.ArrayList;
import java.util.List;

public class SolutionDiagTest implements DiagnosticTest {
    /*default*/ Status status = Status.READY;
    final int id;
    List<Action<SolutionTestingSite>> diagTestAction = new ArrayList<>();
    CS454Lock lock;

    public SolutionDiagTest(int id, CS454Lock lock) {
        this.lock = lock;
        this.id = id;
    }

    @Override
    public Status getStatus() {
        lock.lock();
        try {
            return status;
        } finally {
            lock.unlock();
        }
    }

    public void setStatus(Status status) {
        lock.lock();
        try {
            this.status = status;
        } finally {
            lock.unlock();
        }
    }
}