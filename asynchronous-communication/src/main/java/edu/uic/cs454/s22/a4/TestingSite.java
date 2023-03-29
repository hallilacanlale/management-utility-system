package edu.uic.cs454.s22.a4;

import java.util.HashSet;
import java.util.Set;

public abstract class TestingSite<DT extends DiagnosticTest> implements Runnable {

    private final HashSet<DT> sampledTests = new HashSet<>();
    protected final Thread allowedThread;
    private boolean exception = false;

    private final Action STOP = new Action(Action.Direction.CONTENTS, null, null);

    public TestingSite() {
        this.allowedThread = new Thread(this);
        this.allowedThread.setDaemon(true);
        this.allowedThread.setUncaughtExceptionHandler( (Thread thread, Throwable throwable) -> {
            System.err.println(throwable.getMessage());
            throwable.printStackTrace();
            exception = true;
        });
    }

    public void startThread() {
        this.allowedThread.start();
    }

    public final void run() {
        while (true) {
            Action a = getAction();

            if (a == STOP)
                return;

            switch (a.getDirection()) {
                case SAMPLE:
                    sample((Set<DT>) a.getTarget(), a.getResult());
                    break;
                case POSITIVE:
                    positive((Set<DT>) a.getTarget(), a.getResult());
                    break;
                case NEGATIVE:
                    negative((Set<DT>) a.getTarget(), a.getResult());
                    break;
                case INVALID:
                    invalid((Set<DT>) a.getTarget(), a.getResult());
                    break;
                case CONTENTS:
                    contents(a.getResult());
                    break;
                case ADD:
                    add((Set<DT>) a.getTarget(), a.getResult());
                    break;
                case REMOVE:
                    remove((Set<DT>) a.getTarget(), a.getResult());
                    break;
                default:
                    throw new Error("Unknown operation");
            }
        }
    }

    public void addDiagnosticTests(Set<DT> tests) {
        if (this.allowedThread.isAlive() && Thread.currentThread() != this.allowedThread)
            throw new Error("Wrong thread!");

        this.sampledTests.addAll(tests);
    }

    public void removeDiagnosticTests(Set<DT> tests) {
        if (this.allowedThread.isAlive() && Thread.currentThread() != this.allowedThread)
            throw new Error("Wrong thread!");

        this.sampledTests.removeAll(tests);
    }

    public final Set<DT> getSampledTests() {
        if (this.allowedThread.isAlive() && Thread.currentThread() != this.allowedThread)
            throw new Error("Wrong thread!");

        return new HashSet<>(this.sampledTests);
    }

    public final void stopThread() {
        if (!this.allowedThread.isAlive())
            throw new Error("Thread already stopped, maybe due to an exception?");

        this.submitAction(STOP);

        while (this.allowedThread.isAlive()) {
            try {
                this.allowedThread.join();
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

    public boolean didThrowException() {
        return this.exception;
    }

    public abstract void submitAction(Action a);

    protected abstract Action getAction();

    protected abstract void sample(Set<DT> tests, Result<Boolean> result);

    protected abstract void positive(Set<DT> tests, Result<Boolean> result);

    protected abstract void negative(Set<DT> tests, Result<Boolean> result);

    protected abstract void invalid(Set<DT> tests, Result<Boolean> result);

    protected abstract void contents(Result<Set<DT>> result);

    protected abstract void add(Set<DT> tests, Result<Boolean> result);

    protected abstract void remove(Set<DT> tests, Result<Boolean> result);
}
