package edu.uic.cs454.s22.a4.solution;

import edu.uic.cs454.s22.a4.Action;
import edu.uic.cs454.s22.a4.DiagnosticTest;
import edu.uic.cs454.s22.a4.Result;
import edu.uic.cs454.s22.a4.TestingSite;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SolutionTestingSite extends TestingSite<SolutionDiagTest> {
//    /*default*/ HashSet<SolutionDiagTest> contents = new HashSet<>();
    /*default*/ final int capacity;

    public SolutionTestingSite(int capacity) {
        this.capacity = capacity;
    }

    Queue<Action> workToBeDone = new LinkedList<>();

    @Override
    public void submitAction(Action a) {
        synchronized (workToBeDone) { // submit work to be done workToBeDone.enq(...);
            workToBeDone.add(a);
            if (!workToBeDone.isEmpty())
                workToBeDone.notifyAll();
        }
    }

    @Override
    protected Action getAction() {
        while (true) {
            synchronized (workToBeDone) {
                if (workToBeDone.isEmpty()) {
                    try {
                        workToBeDone.wait();
                    } catch (InterruptedException e) {
                    }
                    continue;
                }
                return workToBeDone.remove();
            }
        }
    }

    @Override
    protected void sample(Set<SolutionDiagTest> tests, Result<Boolean> result) {
        if (tests.size() + this.getSampledTests().size() > this.capacity) {
            result.setResult(false);
            return;
        }
        for (SolutionDiagTest i : tests) {
            if (i.getStatus() != DiagnosticTest.Status.READY) {
                result.setResult(false);
                return;
            }
        }
        this.addDiagnosticTests(tests);
        for (SolutionDiagTest i : tests) {
            i.status = (DiagnosticTest.Status.SAMPLED);
        }
        result.setResult(true);
        return;
    }

    @Override
    protected void positive(Set<SolutionDiagTest> tests, Result<Boolean> result) {
        if (!this.getSampledTests().containsAll(tests)) {
            result.setResult(false);
            return;
        }

//        testingSite.contents.removeAll(tests);
        this.removeDiagnosticTests(tests);

        for (SolutionDiagTest i : tests) {
            i.status = (DiagnosticTest.Status.POSITIVE);
        }
        result.setResult(true);
        return;
    }

    @Override
    protected void negative(Set<SolutionDiagTest> tests, Result<Boolean> result) {
        if (!this.getSampledTests().containsAll(tests)) {
            result.setResult(false);
            return;
        }

//        testingSite.contents.removeAll(tests);
        this.removeDiagnosticTests(tests);

        for (SolutionDiagTest i : tests) {
            i.status = (DiagnosticTest.Status.NEGATIVE);
        }
        result.setResult(true);
        return;
    }

    @Override
    protected void invalid(Set<SolutionDiagTest> tests, Result<Boolean> result) {
        if (!this.getSampledTests().containsAll(tests)) {
            result.setResult(false);
            return;
        }

//        testingSite.contents.removeAll(tests);
        this.removeDiagnosticTests(tests);

        for (SolutionDiagTest i : tests) {
            i.status = (DiagnosticTest.Status.INVALID);
        }
        result.setResult(true);
        return;
    }

    @Override
    protected void contents(Result<Set<SolutionDiagTest>> result) {
        result.setResult(this.getSampledTests());
    }

    @Override
    protected void add(Set<SolutionDiagTest> tests, Result<Boolean> result) {
        if (tests.size() + this.getSampledTests().size() <= this.capacity) {
            this.addDiagnosticTests(tests);
            result.setResult(true);
            return;
        }
        result.setResult(false);
        return;

    }

    @Override
    protected void remove(Set<SolutionDiagTest> tests, Result<Boolean> result) {
        if (this.getSampledTests().containsAll(tests)) {
            this.removeDiagnosticTests(tests);
            result.setResult(true);
        }
        result.setResult(false);
        return;


    }
}
