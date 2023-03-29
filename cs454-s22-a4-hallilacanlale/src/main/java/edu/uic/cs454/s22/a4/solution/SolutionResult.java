package edu.uic.cs454.s22.a4.solution;

import edu.uic.cs454.s22.a4.Action;
import edu.uic.cs454.s22.a4.Result;

import java.util.LinkedList;
import java.util.Queue;

public class SolutionResult<T> extends Result<T> {

    @Override
    public void setResult(T result) {
        synchronized (this) { // submit work to be done workToBeDone.enq(...);
            super.set(result);
            if (this.isReady())
                this.notifyAll();
        }
    }

    @Override
    public T getResult() {
        while (true) {
            synchronized (this) {
                if (!this.isReady()) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                    continue;
                }
              return this.get();
            }
        }
    }
}
