package edu.uic.cs454.s22.a5.solution;

import edu.uic.cs454.s22.a5.Action;
import edu.uic.cs454.s22.a5.DiagnosticTest;
import edu.uic.cs454.s22.a5.Lab;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class SolutionLab extends Lab<SolutionTestingSite, SolutionDiagnosticTest> {
    private final List dummy = new List(new Action<>(Action.Direction.INVALID, new SolutionDiagnosticTest(this)),
            new Action<>(Action.Direction.INVALID, new SolutionTestingSite(0)), new AtomicReference<>(null));

    /*default*/ final List head = dummy;



    @Override
    public SolutionTestingSite createTestingSite(int capacity) {
        return new SolutionTestingSite(capacity);
    }

    @Override
    public SolutionDiagnosticTest createDiagnosticTest(int id) {
        return new SolutionDiagnosticTest(this);
    }

    @Override
    public boolean sampleDiagnosticTests(SolutionTestingSite testingSite, Set<SolutionDiagnosticTest> diagnosticTests) {
        while (true) {

            Set<SolutionDiagnosticTest> contents = new HashSet<>();
            List curr = head;
            List lastNodeInTheList = head;

            while (curr != null) {
                if (diagnosticTests.contains(curr.testAction.get())) {
                    return false;
                }
                if (curr.siteAction.get() == testingSite) {
                    switch (curr.siteAction.getDirection()) {
                        case SAMPLED:
                        case MOVED_IN:
                            contents.add(curr.testAction.get());
                            break;
                        case POSITIVE:
                        case INVALID:
                        case NEGATIVE:
                        case MOVED_OUT:
                            contents.remove(curr.testAction.get());
                            break;
                        default:
                            throw new Error("dead code");
                    }
                }

                lastNodeInTheList = curr;
                curr = curr.next.get();
            }
            //to do: perform more validations
            if (contents.size() + diagnosticTests.size() > testingSite.capacity) {
                return false;
            }

            // add a change to the lab
            List head = null;
            List current = null;

            for (SolutionDiagnosticTest t : diagnosticTests) {
                List node = new List(new Action<>(Action.Direction.SAMPLED, t),
                        new Action<>(Action.Direction.SAMPLED, testingSite));
                if (head == null) {
                    head = node;
//                current = node;
                } else {
                    current.next.set(node);
                }
                current = node;

//            lastNodeInTheList.next = new List(new Action<>(Action.Direction.SAMPLED, t),
//                    new Action<>(Action.Direction.SAMPLED, testingSite));
//            lastNodeInTheList = lastNodeInTheList.next;

            }
            if (lastNodeInTheList.next.compareAndSet(null, head)){
                return true;
            }
////
//            return (lastNodeInTheList.next.compareAndSet(null, head));
//        return false;
        }
    }

    @Override
    public boolean positive(SolutionTestingSite testingSite, Set<SolutionDiagnosticTest> diagnosticTests) {
        while (true) {

            Set<SolutionDiagnosticTest> contents = new HashSet<>();
            List curr = head;
            List lastNodeInTheList = head;
            while (curr != null) {
                if (curr.siteAction.get() == testingSite) {
                    switch (curr.siteAction.getDirection()) {
                        case SAMPLED:
                        case MOVED_IN:
                            contents.add(curr.testAction.get());
                            break;
                        case POSITIVE:
                        case INVALID:
                        case NEGATIVE:
                        case MOVED_OUT:
                            contents.remove(curr.testAction.get());
                            break;
                        default:
                            throw new Error("dead code");
                    }
                }

                lastNodeInTheList = curr;
                curr = curr.next.get();
            }
//        //to do: perform more validations
            if (!contents.containsAll(diagnosticTests)) {
                return false;
            }

            // add a change to the lab
            List head = null;
            List current = null;
            for (SolutionDiagnosticTest t : diagnosticTests) {
                List node = new List(new Action<>(Action.Direction.POSITIVE, t),
                        new Action<>(Action.Direction.POSITIVE, testingSite));
                if (head == null) {
                    head = node;
                } else {
                    current.next.set(node);
                }
                current = node;
//            lastNodeInTheList.next = new List(new Action<>(Action.Direction.SAMPLED, t),
//                    new Action<>(Action.Direction.SAMPLED, testingSite));
//            lastNodeInTheList = lastNodeInTheList.next;

            }
            if (lastNodeInTheList.next.compareAndSet(null, head)) {
                return true;
            }
        }
    }

    @Override
    public boolean negative(SolutionTestingSite testingSite, Set<SolutionDiagnosticTest> diagnosticTests) {
        while (true) {
            Set<SolutionDiagnosticTest> contents = new HashSet<>();
            List curr = head;
            List lastNodeInTheList = head;

            while (curr != null) {
                if (curr.siteAction.get() == testingSite) {
                    switch (curr.siteAction.getDirection()) {
                        case SAMPLED:
                        case MOVED_IN:
                            contents.add(curr.testAction.get());
                            break;
                        case POSITIVE:
                        case INVALID:
                        case NEGATIVE:
                        case MOVED_OUT:
                            contents.remove(curr.testAction.get());
                            break;
                        default:
                            throw new Error("dead code");
                    }
                }

                lastNodeInTheList = curr;
                curr = curr.next.get();
            }
//        //to do: perform more validations
            if (!contents.containsAll(diagnosticTests)) {
                return false;
            }

//        List node = null;
            List head = null;
            List current = null;
            for (SolutionDiagnosticTest t : diagnosticTests) {
                List node = new List(new Action<>(Action.Direction.NEGATIVE, t),
                        new Action<>(Action.Direction.NEGATIVE, testingSite));
                if (head == null) {
                    head = node;
                } else {
                    current.next.set(node);
                }
                current = node;

            }
            if (lastNodeInTheList.next.compareAndSet(null, head)) {
                return true;
            }
        }
//        return false;

//        return (lastNodeInTheList.next.compareAndSet(null, head));
    }

    @Override
    public boolean invalid(SolutionTestingSite testingSite, Set<SolutionDiagnosticTest> diagnosticTests) {

        while (true) {

            Set<SolutionDiagnosticTest> contents = new HashSet<>();
            List curr = head;
            List lastNodeInTheList = head;
            while (curr != null) {
                if (curr.siteAction.get() == testingSite) {
                    switch (curr.siteAction.getDirection()) {
                        case SAMPLED:
                        case MOVED_IN:
                            contents.add(curr.testAction.get());
                            break;
                        case POSITIVE:
                        case INVALID:
                        case NEGATIVE:
                        case MOVED_OUT:
                            contents.remove(curr.testAction.get());
                            break;
                        default:
                            throw new Error("dead code");
                    }
                }

                lastNodeInTheList = curr;
                curr = curr.next.get();
            }
//        //to do: perform more validations
            if (!contents.containsAll(diagnosticTests)) {
                return false;
            }

            // add a change to the lab
            List head = null;
            List current = null;
            for (SolutionDiagnosticTest t : diagnosticTests) {
                List node = new List(new Action<>(Action.Direction.INVALID, t),
                        new Action<>(Action.Direction.INVALID, testingSite));
                if (head == null) {
                    head = node;
                } else {
                    current.next.set(node);
                }
                current = node;
//            lastNodeInTheList.next = new List(new Action<>(Action.Direction.SAMPLED, t),
//                    new Action<>(Action.Direction.SAMPLED, testingSite));
//            lastNodeInTheList = lastNodeInTheList.next;

            }
            if (lastNodeInTheList.next.compareAndSet(null, head)) {
                return true;
            }
        }
    }


    @Override
    public boolean moveDiagnosticTests(SolutionTestingSite from, SolutionTestingSite to, Set<SolutionDiagnosticTest> diagnosticTests) {
//        throw new Error ("Not yet implemented");
        while (true) {
            Set<SolutionDiagnosticTest> toContents = new HashSet<>();

            Set<SolutionDiagnosticTest> fromContents = new HashSet<>();

            List curr = head;
            List lastNodeInTheList = head;

            while (curr != null) {
                switch (curr.siteAction.getDirection()) {
                    case SAMPLED:
                    case MOVED_IN:
                        if (curr.siteAction.get() == to) {
                            toContents.add(curr.testAction.get());
                        } else if (curr.siteAction.get() == from) {
                            fromContents.add(curr.testAction.get());
                        }
                        break;
                    case POSITIVE:
                    case INVALID:
                    case NEGATIVE:
                    case MOVED_OUT:
                        if (curr.siteAction.get() == to) {
                            toContents.remove(curr.testAction.get());
                        } else if (curr.siteAction.get() == from) {
                            fromContents.remove(curr.testAction.get());
                        }
                        break;
                    default:
                        throw new Error("dead code");
                }


                lastNodeInTheList = curr;
                curr = curr.next.get();
            }

            if (toContents.size() + diagnosticTests.size() > to.capacity ||
                    !fromContents.containsAll(diagnosticTests)) {
                return false;
            }


            List head = null;
            List current = null;
            //keeping track of two nodes at the same time
            //set predecessor to node 2 and
            //increment predecessors next
            //out->in->next

            for (SolutionDiagnosticTest t : diagnosticTests) {
                List in = new List(new Action<>(Action.Direction.MOVED_IN, t), new Action<>(Action.Direction.MOVED_IN, to));
                List out = new List(new Action<>(Action.Direction.MOVED_OUT, t), new Action<>(Action.Direction.MOVED_OUT, from),
                        new AtomicReference(in));

                if (head == null) {
                    head = out;
                    out.next.set(in);
                } else {
                    out.next.set(in);
                    current.next.set(out);
                }
                current = in;
//            lastNodeInTheList.next = new List(new Action<>(Action.Direction.MOVED_OUT, t),
//                    new Action<>(Action.Direction.MOVED_OUT, from));
//            lastNodeInTheList = lastNodeInTheList.next;
//            lastNodeInTheList.next = new List(new Action<>(Action.Direction.MOVED_IN, t),
//                    new Action<>(Action.Direction.MOVED_IN, to));
//            lastNodeInTheList = lastNodeInTheList.next;
            }
            if (lastNodeInTheList.next.compareAndSet(null, head)) {
                return true;
            }
        }
//        return (lastNodeInTheList.next.compareAndSet(null, head));
    }

    @Override
    public Set<SolutionDiagnosticTest> getDiagnosticTests() {
        Set<SolutionDiagnosticTest> contents = new HashSet<>();
        List curr = head;

//
        while (curr != null) {
            switch (curr.siteAction.getDirection()) {
                case SAMPLED:
                case MOVED_IN:
                    contents.add(curr.testAction.get());
                    break;

                case POSITIVE:
                case INVALID:
                case NEGATIVE:
                case MOVED_OUT:
                    contents.remove(curr.testAction.get());
                    break;
                default:
                    throw new Error ("dead code");
            }
            curr = curr.next.get();
        }
        return contents;
    }

    @Override
    public Set<SolutionDiagnosticTest> getDiagnosticTests(SolutionTestingSite testingSite) {
        Set<SolutionDiagnosticTest> contents = new HashSet<>();
        List curr = head;

        while (curr != null) {
            switch (curr.siteAction.getDirection()) {
                case SAMPLED:
                case MOVED_IN:
                    if (curr.siteAction.get() == testingSite) {
                        contents.add(curr.testAction.get());
                    }
                    break;
                case POSITIVE:
                case INVALID:
                case NEGATIVE:
                case MOVED_OUT:
                    if (curr.siteAction.get() == testingSite) {
                        contents.remove(curr.testAction.get());
                    }
                    break;
                default:
                    throw new Error ("dead code");
            }
            curr = curr.next.get();
        }
        return contents;
//        throw new Error ("not yet implemented");
    }

    /*default*/ static class List {
        final Action<SolutionDiagnosticTest> testAction;
        final Action<SolutionTestingSite> siteAction;

        AtomicReference<List> next;

        public List(Action<SolutionDiagnosticTest> testAction,  Action<SolutionTestingSite> siteAction) {
            this.testAction = testAction;
            this.siteAction = siteAction;
            this.next = new AtomicReference<>(null);
        }

        public List(Action<SolutionDiagnosticTest> testAction,  Action<SolutionTestingSite> siteAction, AtomicReference<List> next) {
            this.testAction = testAction;
            this.siteAction = siteAction;
            this.next = next;
        }


    }
}
