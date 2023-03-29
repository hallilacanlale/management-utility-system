package edu.uic.cs454.s22.a3.solution;

//import edu.uic.cs454.s22.a3.Action;
import edu.uic.cs454.s22.a3.DiagnosticTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SolutionDiagTest implements DiagnosticTest {
    /*default*/ Status status = Status.READY;
    final SolutionLab theLab;
//    List<Action<SolutionTestingSite>> diagTestAction = new ArrayList<>();
    ReentrantLock lock = new ReentrantLock();
    static AtomicInteger numIDs = new AtomicInteger();
    final AtomicInteger id;

    public SolutionDiagTest(SolutionLab theLab) {
        this.theLab = theLab;
        id = new AtomicInteger(numIDs.getAndIncrement());
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
        this.status = status;
    }
}
