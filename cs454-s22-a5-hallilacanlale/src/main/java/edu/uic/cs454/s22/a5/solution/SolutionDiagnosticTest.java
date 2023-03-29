package edu.uic.cs454.s22.a5.solution;

import edu.uic.cs454.s22.a5.DiagnosticTest;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SolutionDiagnosticTest implements DiagnosticTest {
//    enum Status {SAMPLED , MOVED_OUT, MOVED_IN, POSITIVE , NEGATIVE , INVALID }

    /*default */ AtomicReference<Status> status = new AtomicReference<>(Status.READY) ;
    /*default */ SolutionLab lab;


    static AtomicInteger numIDs = new AtomicInteger();
    final AtomicInteger id;

    public SolutionDiagnosticTest(SolutionLab lab) {
        this.id = new AtomicInteger(numIDs.getAndIncrement());
        this.lab = lab;
    }

    @Override
    public Status getStatus() {
        SolutionLab.List curr = this.lab.head;
        while (curr != null) {
            if (curr.testAction.get() == this) {
                switch (curr.siteAction.getDirection()) {
                    case SAMPLED:
                    case MOVED_IN:
                    case MOVED_OUT:
                        status.set(Status.SAMPLED);
                        break;
                    case POSITIVE:
                        status.set(Status.POSITIVE);
                        break;
                    case INVALID:
                        status.set(Status.INVALID);

                        break;
                    case NEGATIVE:
                        status.set(Status.NEGATIVE);

                        break;
                    default:
                        throw new Error("dead code");
                }
            }
            curr = curr.next.get();
        }
        return status.get();
    }
};
