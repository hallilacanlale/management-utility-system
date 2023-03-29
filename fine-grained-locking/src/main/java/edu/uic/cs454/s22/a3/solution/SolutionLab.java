package edu.uic.cs454.s22.a3.solution;

//import edu.uic.cs454.s22.a3.Action;
import edu.uic.cs454.s22.a3.DiagnosticTest;
import edu.uic.cs454.s22.a3.Lab;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SolutionLab extends Lab<SolutionTestingSite, SolutionDiagTest> {
    Set<SolutionTestingSite> allSites = new HashSet<>();

    @Override
    public SolutionTestingSite createTestingSite(int capacity) {
        SolutionTestingSite ret = new SolutionTestingSite(capacity);
        allSites.add(ret);
        return ret;
    }

    @Override
    public SolutionDiagTest createDiagnosticTest(int id) {
        return new SolutionDiagTest(this);
    }
//    1. Which operations can result in deadlocks, and what steps did you take to avoid them?

    @Override
    public boolean sampleDiagnosticTests(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        List<SolutionDiagTest> diagList = new ArrayList<>(diagnosticTests);
        Collections.sort(diagList, (SolutionDiagTest d1, SolutionDiagTest d2) -> {
            if (d1.id.get() < d2.id.get()) return -1;
            else if (d1.id.get() > d2.id.get()) return 1;
            else return 1;
        });

        for (SolutionDiagTest i: diagList) {
            i.lock.lock();
        }
        testingSite.rwLock.writeLock().lock();

        try {
            if (diagnosticTests.size() + testingSite.contents.size() > testingSite.capacity)
                return false;
            for (SolutionDiagTest i : diagnosticTests) {
                if (i.getStatus() != DiagnosticTest.Status.READY) {
                    return false;
                }
            }
            testingSite.contents.addAll(diagnosticTests);


            for (SolutionDiagTest i : diagnosticTests) {
                i.setStatus(DiagnosticTest.Status.SAMPLED);
            }
            return true;
        } finally {
            testingSite.rwLock.writeLock().unlock();
            for (SolutionDiagTest i: diagList) {
                i.lock.unlock();
            }
        }
    }
    private boolean setResult(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests, DiagnosticTest.Status s) {
       if (!getDiagnosticTests(testingSite).containsAll(diagnosticTests))
            return false;

        testingSite.contents.removeAll(diagnosticTests);

        for (SolutionDiagTest i : diagnosticTests) {
            i.status = s;
        }
        return true;
    }


    @Override
    public boolean positive(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        List<SolutionDiagTest> diagList = new ArrayList<>(diagnosticTests);
        Collections.sort(diagList, (SolutionDiagTest d1, SolutionDiagTest d2) -> {
            if (d1.id.get() < d2.id.get()) return -1;
            else if (d1.id.get() > d2.id.get()) return 1;
            else return 1;
        });

        for (SolutionDiagTest i: diagnosticTests) {
            i.lock.lock();
        }
        testingSite.rwLock.writeLock().lock();

        try {
            return setResult(testingSite, diagnosticTests, DiagnosticTest.Status.POSITIVE);
        } finally {
            testingSite.rwLock.writeLock().unlock();
            for (SolutionDiagTest i: diagnosticTests) {
                i.lock.unlock();
            }
        }
    }

    @Override
    public boolean negative(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        List<SolutionDiagTest> diagList = new ArrayList<>(diagnosticTests);
        Collections.sort(diagList, (SolutionDiagTest d1, SolutionDiagTest d2) -> {
            if (d1.id.get() < d2.id.get()) return -1;
            else if (d1.id.get() > d2.id.get()) return 1;
            else return 1;
        });

        for (SolutionDiagTest i: diagnosticTests) {
            i.lock.lock();
        }
        testingSite.rwLock.writeLock().lock();

        try {
            return setResult(testingSite, diagnosticTests, DiagnosticTest.Status.NEGATIVE);
        } finally {
            testingSite.rwLock.writeLock().unlock();
            for (SolutionDiagTest i: diagnosticTests) {
                i.lock.unlock();
            }
        }
    }

    @Override
    public boolean invalid(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        List<SolutionDiagTest> diagList = new ArrayList<>(diagnosticTests);
        Collections.sort(diagList, (SolutionDiagTest d1, SolutionDiagTest d2) -> {
            if (d1.id.get() < d2.id.get()) return -1;
            else if (d1.id.get() > d2.id.get()) return 1;
            else return 1;
        });

        for (SolutionDiagTest i: diagnosticTests) {
            i.lock.lock();
        }
        testingSite.rwLock.writeLock().lock();

        try {
            return setResult(testingSite, diagnosticTests, DiagnosticTest.Status.INVALID);
        } finally {
            testingSite.rwLock.writeLock().unlock();
            for (SolutionDiagTest i: diagnosticTests) {
                i.lock.unlock();
        }
    }
    }



    @Override
    public boolean moveDiagnosticTests(SolutionTestingSite from, SolutionTestingSite to, Set<SolutionDiagTest> diagnosticTests) {
        AtomicInteger fromID = from.id, toID = to.id;
//        from.rwLock.readLock().lock();
//        to.rwLock.readLock().lock();

        if (fromID.get() > toID.get()) {
            to.rwLock.readLock().lock();
            from.rwLock.readLock().lock();
        } else {
            from.rwLock.readLock().lock();
            to.rwLock.readLock().lock();
        }
        try {
            if ((diagnosticTests.size() + to.contents.size() > to.capacity) || (!from.contents.containsAll(diagnosticTests))) {
                return false;
            }
        } finally {
            to.rwLock.readLock().unlock();
            from.rwLock.readLock().unlock();
        }
//        from.rwLock.writeLock().lock();
//        to.rwLock.writeLock().lock();
        if (fromID.get() > toID.get()) {
            to.rwLock.writeLock().lock();
            from.rwLock.writeLock().lock();
        } else {
            from.rwLock.writeLock().lock();
            to.rwLock.writeLock().lock();
        }
        try {
            from.contents.removeAll(diagnosticTests);
            to.contents.addAll(diagnosticTests);
        } finally {
            to.rwLock.writeLock().unlock();
            from.rwLock.writeLock().unlock();
        }
        return true;


    }

    @Override
    public Set<SolutionDiagTest> getDiagnosticTests() {
        Set<SolutionDiagTest> ret = new HashSet<>();
        List<SolutionTestingSite> testlist = new ArrayList<>(allSites);
        Collections.sort(testlist, (SolutionTestingSite t1, SolutionTestingSite t2) -> {
            if (t1.id.get() < t2.id.get()) return -1;
            else if (t1.id.get() > t2.id.get()) return 1;
            else return 1;
        });



        for (SolutionTestingSite i : testlist) {
            i.rwLock.readLock().lock();
        }

        try {
            for (SolutionTestingSite i : testlist) {
                ret.addAll(getDiagnosticTests(i));

            }
            return ret;
        } finally {
            for (SolutionTestingSite i : testlist) {
                i.rwLock.readLock().unlock();
            }
        }
    }

    @Override
    public Set<SolutionDiagTest> getDiagnosticTests(SolutionTestingSite testingSite) {

        testingSite.rwLock.readLock().lock();
        try {
            return new HashSet<>(testingSite.contents);
        } finally {
            testingSite.rwLock.readLock().unlock();
        }

    }

    @Override
    public Set<SolutionDiagTest> getDiagnosticTests(List<SolutionTestingSite> testingSite) {
//        throw new Error("not implemented");
        Set<SolutionDiagTest> ret = new HashSet<>();
        List<SolutionTestingSite> testlist = new ArrayList<>(allSites);
        Collections.sort(testlist, (SolutionTestingSite t1, SolutionTestingSite t2) -> {
            if (t1.id.get() < t2.id.get()) return -1;
            else if (t1.id.get() > t2.id.get()) return 1;
            else return 1;
        });

        for (SolutionTestingSite i : testlist) {
            i.rwLock.readLock().lock();
        }

        try {
            for (SolutionTestingSite i : testlist) {
                ret.addAll(getDiagnosticTests(i));

            }
            return ret;
        } finally {
            for (SolutionTestingSite i : testlist) {
                i.rwLock.readLock().unlock();
            }
        }

//        for (SolutionTestingSite i: testingSite) {
//            i.rwLock.readLock().lock();
//            try {
//                ret.addAll(getDiagnosticTests(i));
//            } finally {
//                i.rwLock.readLock().unlock();
//            }
//        }
    }

//
//    @Override
//    public List<Action<SolutionDiagTest>> audit(SolutionTestingSite testingSite) {
//        return testingSite.testingTestAction;
//    }
//
//    @Override
//    public List<Action<SolutionTestingSite>> audit(SolutionDiagTest diagnosticTest) {
//        return diagnosticTest.diagTestAction;
//    }
}
