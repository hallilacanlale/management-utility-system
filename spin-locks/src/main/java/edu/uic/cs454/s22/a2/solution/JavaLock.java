package edu.uic.cs454.s22.a2.solution;

import edu.uic.cs454.s22.a2.CS454Lock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class JavaLock extends CS454Lock {
    AtomicBoolean state = new AtomicBoolean(false);
    AtomicReference<Thread> atomicThread = new AtomicReference<>();
    AtomicInteger atomicValue = new AtomicInteger();

    @Override
    public void lock() {
        Thread curr = Thread.currentThread();
        if (atomicThread.get() == curr && atomicValue.get() > 0) {
            atomicValue.getAndIncrement();
            return;
        }

        while (true) {
            while (state.get()) {};
            if ((!state.getAndSet(true))) {
                atomicThread.set(curr);
                atomicValue.getAndIncrement();
                return;
            }
            int timeToSleep = 1000;
            long start = System.nanoTime();
            while (System.nanoTime() - start < timeToSleep);
        }


    }

    @Override
    public boolean tryLock() {
        Thread curr = Thread.currentThread();

        if (state.get() && !curr.equals(atomicThread.get())){
            return false;
        }

        state.set(true);
        atomicThread.set(curr);
        atomicValue.getAndIncrement();
        return true;
    }

    @Override
    public void unlock() {
        Thread curr = Thread.currentThread();

        if (atomicThread.get() == null) {
            throw new IllegalMonitorStateException("It is empty");
        } else if (!atomicThread.get().equals(curr)) {
            throw new IllegalMonitorStateException("Not the curr thread");
        } else if (state.get() == false && atomicThread.get().equals(curr)) {
            throw new IllegalMonitorStateException("Cannot unlock an unlocked thread");
        }

        if (atomicValue.decrementAndGet() == 0) {
            atomicThread.set(null);
            state.set(false);
        }


    }





}
