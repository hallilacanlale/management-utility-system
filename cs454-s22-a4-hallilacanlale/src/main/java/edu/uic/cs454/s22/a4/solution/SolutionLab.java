package edu.uic.cs454.s22.a4.solution;

import edu.uic.cs454.s22.a4.Action;
import edu.uic.cs454.s22.a4.DiagnosticTest;
import edu.uic.cs454.s22.a4.Lab;
import edu.uic.cs454.s22.a4.Result;

import java.util.HashSet;
import java.util.Set;

public class SolutionLab extends Lab<SolutionTestingSite, SolutionDiagTest> {
    Set<SolutionTestingSite> allSites = new HashSet<>();

    @Override
    public SolutionTestingSite createTestingSite(int capacity) {
        SolutionTestingSite ret = new SolutionTestingSite(capacity);
        allSites.add(ret);
        return ret;
//        return new SolutionTestingSite(capacity);
    }

    @Override
    public SolutionDiagTest createDiagnosticTest(int id) {
        return new SolutionDiagTest();
    }

    @Override
    public boolean sampleDiagnosticTests(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        SolutionResult<Boolean> result = new SolutionResult<>();
        testingSite.submitAction(new Action(Action.Direction.SAMPLE, diagnosticTests, result));
        return  result.getResult();
    }

    @Override
    public boolean positive(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        SolutionResult<Boolean> result = new SolutionResult<>();
        testingSite.                      (new Action(Action.Direction.POSITIVE, diagnosticTests, result));
        return  result.getResult();
    }

    @Override
    public boolean negative(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        SolutionResult<Boolean> result = new SolutionResult<>();
        testingSite.submitAction(new Action(Action.Direction.NEGATIVE, diagnosticTests, result));
        return  result.getResult();
    }

    @Override
    public boolean invalid(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        SolutionResult<Boolean> result = new SolutionResult<>();
        testingSite.submitAction(new Action(Action.Direction.INVALID, diagnosticTests, result));
        return  result.getResult();
    }

    @Override
    public boolean moveDiagnosticTests(SolutionTestingSite from, SolutionTestingSite to, Set<SolutionDiagTest> diagnosticTests) {
        SolutionResult<Boolean> from_result = new SolutionResult<>();
        from.submitAction(new Action(Action.Direction.REMOVE, diagnosticTests, from_result));

        SolutionResult<Boolean> to_result = new SolutionResult<>();
        to.submitAction(new Action(Action.Direction.ADD, diagnosticTests, to_result));

        boolean b1 = from_result.getResult();
        boolean b2 = to_result.getResult();

        if (!b1) {
            from.submitAction(new Action(Action.Direction.ADD, diagnosticTests, to_result));
        }

        return from_result.getResult() && to_result.getResult();
    }

    @Override
    public Set<SolutionDiagTest> getDiagnosticTests() {
        Set<SolutionDiagTest> res = new HashSet<>();

        for (SolutionTestingSite i: allSites) {
            Result<Set<SolutionDiagTest>> curr = new SolutionResult<>();
            i.submitAction(new Action(Action.Direction.CONTENTS, i, curr));

            for (SolutionDiagTest j : curr.getResult()) {
                res.add(j);
            }
        }

        return res;
    }

    @Override
    public Set<SolutionDiagTest> getDiagnosticTests(SolutionTestingSite testingSite) {
        SolutionResult<Set<SolutionDiagTest>> result = new SolutionResult<>();
        testingSite.submitAction(new Action(Action.Direction.CONTENTS, testingSite, result));
        return result.getResult();
    }

    @Override
    public Result<Boolean> sampleDiagnosticTestsAsync(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        SolutionResult<Boolean> result = new SolutionResult<>();
        testingSite.submitAction(new Action(Action.Direction.SAMPLE, diagnosticTests, result));
        return result;
    }

    @Override
    public Result<Boolean> positiveAsync(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        SolutionResult<Boolean> result = new SolutionResult<>();
        testingSite.submitAction(new Action(Action.Direction.POSITIVE, diagnosticTests, result));
        return result;
    }

    @Override
    public Result<Boolean> negativeAsync(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        SolutionResult<Boolean> result = new SolutionResult<>();
        testingSite.submitAction(new Action(Action.Direction.NEGATIVE, diagnosticTests, result));
        return result;
    }

    @Override
    public Result<Boolean> invalidAsync(SolutionTestingSite testingSite, Set<SolutionDiagTest> diagnosticTests) {
        SolutionResult<Boolean> result = new SolutionResult<>();
        testingSite.submitAction(new Action(Action.Direction.INVALID, diagnosticTests, result));
        return result;
    }

    @Override
    public Result<Boolean> moveDiagnosticTestsAsync(SolutionTestingSite from, SolutionTestingSite to, Set<SolutionDiagTest> diagnosticTests) {
        SolutionResult<Boolean> from_result = new SolutionResult<>();
        SolutionResult<Boolean> to_result = new SolutionResult<>();


        from.submitAction(new Action(Action.Direction.REMOVE, diagnosticTests, from_result));
        to.submitAction(new Action(Action.Direction.ADD, diagnosticTests, to_result));

        Result<Boolean> result = new Result<Boolean>() {
            @Override
            public void setResult(Boolean result) {
                throw new Error ("This method cannot be called.");
            }

            @Override
            public Boolean getResult() {
                boolean b1 = from_result.getResult();
                boolean b2 = to_result.getResult();

                if (!b1) {
                    from.submitAction(new Action(Action.Direction.ADD, diagnosticTests, to_result));
                }

                return from_result.getResult() && to_result.getResult();
            }
        };
        return result;

    }

    @Override
    public Result<Set<SolutionDiagTest>> getDiagnosticTestsAsync() {
//        throw new Error("Not yet implemented");

//        Result<Set<SolutionDiagTest>> res = new SolutionResult<>();
        Result<Set<SolutionDiagTest>> SolutionResults = new SolutionResult<>();

        for (SolutionTestingSite i: allSites) {
            Result<Set<SolutionDiagTest>> curr = new SolutionResult<>();
            i.submitAction(new Action(Action.Direction.CONTENTS, i, curr));
            i.submitAction(new Action(Action.Direction.ADD, curr, SolutionResults));
        }

        Result<Set<SolutionDiagTest>> result = new Result<Set<SolutionDiagTest>>() {
            @Override
            public void setResult(Set<SolutionDiagTest> result) {
                throw new Error ("This method cannot be called.");
            }

            @Override
            public Set<SolutionDiagTest> getResult() {
                Set<SolutionDiagTest> ret = new HashSet<>();
                for (SolutionDiagTest i : SolutionResults.getResult()){
                    ret.add(i);
                }
                return ret;
            }
        };
        return result;



    }

    @Override
    public Result<Set<SolutionDiagTest>> getDiagnosticTestsAsync(SolutionTestingSite testingSite) {
        SolutionResult<Set<SolutionDiagTest>> result = new SolutionResult<>();
        testingSite.submitAction(new Action(Action.Direction.CONTENTS, testingSite, result));
        return result;
    }
}
