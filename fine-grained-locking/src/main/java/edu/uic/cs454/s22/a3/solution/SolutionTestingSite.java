package edu.uic.cs454.s22.a3.solution;

//import edu.uic.cs454.s22.a3.Action;
import edu.uic.cs454.s22.a3.TestingSite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SolutionTestingSite implements TestingSite {
    /*default*/ HashSet<SolutionDiagTest> contents = new HashSet<>();
    /*default*/ final int capacity;
//    List<Action<SolutionDiagTest>> testingTestAction = new ArrayList<>();

    ReadWriteLock rwLock = new ReentrantReadWriteLock();
    static AtomicInteger numIDs = new AtomicInteger();
    final AtomicInteger id;
    public SolutionTestingSite(int capacity) {
        this.capacity = capacity;
        id = new AtomicInteger(numIDs.getAndIncrement());
    }
}
