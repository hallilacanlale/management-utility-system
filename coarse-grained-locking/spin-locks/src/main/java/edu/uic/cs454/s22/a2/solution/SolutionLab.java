package edu.uic.cs454.s22.a2.solution;

import edu.uic.cs454.s22.a2.Action;
import edu.uic.cs454.s22.a2.CS454Lock;
import edu.uic.cs454.s22.a2.DiagnosticTest;
import edu.uic.cs454.s22.a2.Lab;

import java.util.*;

public class SolutionLab extends Lab<SolutionTestingSite, SolutionDiagTest> {
    CS454Lock lock = Lab.createLock();
    @Override
    public SolutionTestingSite createTestingSite(int capacity) {
        return new SolutionTestingSite(capacity);
    }

    @Override
    public SolutionDiagTest createDiagnosticTest(int id) {
        lock.lock();
        try {
            SolutionDiagTest ret = new SolutionDiagTest(id, lock);
            ret.setStatus(DiagnosticTest.Status.READY);
            return ret;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean sampleDiagnosticTests(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        lock.lock();
        try {
            if (diagnosticTests.size() + testingSite.contents.size() > testingSite.capacity)
                return false;
            for (SolutionDiagTest i : diagnosticTests) {
                if (i.getStatus() != DiagnosticTest.Status.READY) {
                    return false;
                }
            }
            for (SolutionDiagTest i : diagnosticTests) {
                i.setStatus(DiagnosticTest.Status.SAMPLED);
                testingSite.testingTestAction.add(new Action(Action.Direction.SAMPLED, i));
                i.diagTestAction.add(new Action(Action.Direction.SAMPLED, testingSite));
            }

            testingSite.contents.addAll(diagnosticTests);
            tests.addAll(diagnosticTests);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean positive(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        lock.lock();
        try {
            if (!testingSite.contents.containsAll(diagnosticTests)) {
                return false;
            }
            for (SolutionDiagTest i : diagnosticTests) {
                if (i.getStatus() == DiagnosticTest.Status.SAMPLED) {
                    i.diagTestAction.add(new Action(Action.Direction.POSITIVE, testingSite));
                    i.setStatus(DiagnosticTest.Status.POSITIVE);

                    testingSite.contents.remove(i);
                    testingSite.testingTestAction.add(new Action(Action.Direction.POSITIVE, i));

                } else {
                    return false;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean negative(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        lock.lock();
        try {
            if (!testingSite.contents.containsAll(diagnosticTests)) {
                return false;
            }
            for (SolutionDiagTest i : diagnosticTests) {
                if (i.getStatus() == DiagnosticTest.Status.SAMPLED) {
                    i.diagTestAction.add(new Action(Action.Direction.NEGATIVE, testingSite));
                    i.setStatus(DiagnosticTest.Status.NEGATIVE);

                    testingSite.contents.remove(i);
                    testingSite.testingTestAction.add(new Action(Action.Direction.NEGATIVE, i));
                } else {
                    return false;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean invalid(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        lock.lock();
        try {
            if (!testingSite.contents.containsAll(diagnosticTests)) {
                return false;
            }
            for (SolutionDiagTest i : diagnosticTests) {
                if (i.getStatus() == DiagnosticTest.Status.SAMPLED) {
                    i.diagTestAction.add(new Action(Action.Direction.INVALID, testingSite));
                    i.setStatus(DiagnosticTest.Status.INVALID);

                    testingSite.contents.remove(i);
                    testingSite.testingTestAction.add(new Action(Action.Direction.INVALID, i));

                } else {
                    return false;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean moveDiagnosticTests(SolutionTestingSite from, SolutionTestingSite to, Set<SolutionDiagTest> diagnosticTests) {
        lock.lock();
        try {
            if ((diagnosticTests.size() + to.contents.size() > to.capacity) || (!from.contents.containsAll(diagnosticTests))) {
                return false;
            }

            for (SolutionDiagTest i : diagnosticTests) {
                if (from.contents.contains(i)) {
                    from.contents.remove(i);
                    i.diagTestAction.add(new Action(Action.Direction.MOVED_OUT, from));
                    from.testingTestAction.add(new Action(Action.Direction.MOVED_OUT, i));

                    to.contents.add(i);
                    i.diagTestAction.add(new Action(Action.Direction.MOVED_IN, to));
                    to.testingTestAction.add(new Action(Action.Direction.MOVED_IN, i));
                }
            }

            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<SolutionDiagTest> getDiagnosticTests() {
        lock.lock();
        try {
            Set<SolutionDiagTest> ret = new HashSet<>();

            for (SolutionDiagTest i : tests) {
                if (i.getStatus() == DiagnosticTest.Status.SAMPLED) {
                    ret.add(i);
                }
            }
            return ret;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<SolutionDiagTest> getDiagnosticTests(SolutionTestingSite testingSite) {
        lock.lock();
        try {
            Set<SolutionDiagTest> ret = new HashSet<>();


            int size = 0;
            for (SolutionDiagTest i : testingSite.contents) {
                if ((testingSite.capacity > size) && (i.getStatus() == DiagnosticTest.Status.SAMPLED)) {
                    ret.add(i);
                    size += 1;
                }
            }
            return ret;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Action<SolutionDiagTest>> audit(SolutionTestingSite testingSite) {
        lock.lock();
        try {
            return testingSite.testingTestAction;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Action<SolutionTestingSite>> audit(SolutionDiagTest diagnosticTest) {
        lock.lock();
        try {
            return diagnosticTest.diagTestAction;
        } finally {
            lock.unlock();
        }
    }
}