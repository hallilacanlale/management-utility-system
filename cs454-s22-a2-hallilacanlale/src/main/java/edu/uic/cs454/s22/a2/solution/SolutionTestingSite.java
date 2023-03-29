package edu.uic.cs454.s22.a2.solution;

import edu.uic.cs454.s22.a2.Action;
import edu.uic.cs454.s22.a2.TestingSite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SolutionTestingSite implements TestingSite {
    /*default*/ HashSet<SolutionDiagTest> contents = new HashSet<>();
    /*default*/ final int capacity;
    List<Action<SolutionDiagTest>> testingTestAction = new ArrayList<>();

    public SolutionTestingSite(int capacity) {
        this.capacity = capacity;
    }
}