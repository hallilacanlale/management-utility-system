package edu.uic.cs454.s22.a5;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test01_Add {
    @Test
    public void testDiagnosticTestIdentity() {
        Lab l = Lab.createLab();

        int size = 10;
        DiagnosticTest[] vaccineDoses = new DiagnosticTest[size];

        Set<DiagnosticTest> toAdd = new HashSet<>();
        for (int i = 0 ; i < size ; i++) {
            vaccineDoses[i] = l.createDiagnosticTest(i);
            toAdd.add(vaccineDoses[i]);
        }

        Assert.assertEquals(size, toAdd.size());
        Assert.assertEquals(toAdd, Set.of(vaccineDoses));
    }

    @Test
    public void testMaxCapacity() {
        Lab l = Lab.createLab();
        int size = 10;
        int test = 20;
        TestingSite c = l.createTestingSite(size);

        for (int i = 0 ; i < test ; i++) {
            DiagnosticTest v = l.createDiagnosticTest(i);

            if (i < size)
                Assert.assertTrue(l.sampleDiagnosticTests(c, Set.of(v)));
            else
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(v)));
        }
    }

    @Test
    public void testMaxCapacityTestingSites() {
        Lab l = Lab.createLab();
        int test = 20;
        TestingSite[] tss = new TestingSite[10];

        for (int i = 0 ; i < tss.length ; i++)
            tss[i] = l.createTestingSite(i+1);

        int count = 0;

        for (int i = 0 ; i < test ; i++) {
            for (int j = 0 ; i < tss.length ; i++) {
                TestingSite s = tss[j];
                DiagnosticTest vaccineDose = l.createDiagnosticTest(count++);
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(vaccineDose);

                if (i <= j)
                    Assert.assertTrue(l.sampleDiagnosticTests(s, toAdd));
                else
                    Assert.assertFalse(l.sampleDiagnosticTests(s, toAdd));
            }
        }
    }

    @Test
    public void testDiagnosticTestAlreadyInTestingSite() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        testDiagnosticTestAlreadyInTestingSite(sequentialIndexes);
        testDiagnosticTestAlreadyInTestingSite(shuffledIndexesList);
    }

    private void testDiagnosticTestAlreadyInTestingSite(List<Integer> indexes) {
        int size = indexes.size();
        Lab l = Lab.createLab();
        TestingSite c = l.createTestingSite(size);

        DiagnosticTest[] vaccineDoses = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            vaccineDoses[i] = l.createDiagnosticTest(i);
        }

        for (int i : indexes) {
            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(vaccineDoses[i]);
                Assert.assertTrue(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(vaccineDoses[i])));
            }

            if (i > 0) {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(vaccineDoses[i]);
                toAdd.add(vaccineDoses[i-1]);
                Assert.assertFalse(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(vaccineDoses[i], vaccineDoses[i-1])));
            }

            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(vaccineDoses[i]);
                DiagnosticTest anotherDiagnosticTest = l.createDiagnosticTest(size+i);
                toAdd.add(anotherDiagnosticTest);
                Assert.assertFalse(l.sampleDiagnosticTests(c, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(c, Set.of(vaccineDoses[i], anotherDiagnosticTest)));
            }
        }
    }

    @Test
    public void testDiagnosticTestAlreadyInLab() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList1);
        Collections.shuffle(shuffledIndexesList2);

        testDiagnosticTestAlreadyInLab(sequentialIndexes, sequentialIndexes);
        testDiagnosticTestAlreadyInLab(shuffledIndexesList1, shuffledIndexesList2);
    }

    private void testDiagnosticTestAlreadyInLab(List<Integer> vaccineDoseIndexes, List<Integer> shelfIndexes) {
        int size = vaccineDoseIndexes.size();
        Lab l = Lab.createLab();
        TestingSite[] tss = new TestingSite[size];

        for (int i = 0 ; i < size ; i++)
            tss[i] = l.createTestingSite(size);

        int count = 0;

        DiagnosticTest[] vaccineDoses = new DiagnosticTest[size];
        for (int i = 0 ; i < size ; i++) {
            vaccineDoses[i] = l.createDiagnosticTest(count++);
        }


        for (int i : vaccineDoseIndexes) {
            TestingSite s  = tss[shelfIndexes.get(i)];
            TestingSite ss = tss[shelfIndexes.get((i+1)%size)];

            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(vaccineDoses[i]);
                Assert.assertTrue(l.sampleDiagnosticTests(s, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(s, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(s, Set.of(vaccineDoses[i])));

                Assert.assertFalse(l.sampleDiagnosticTests(ss, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(ss, Set.of(vaccineDoses[i])));
            }

            if (i > 0) {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(vaccineDoses[i]);
                toAdd.add(vaccineDoses[i-1]);
                Assert.assertFalse(l.sampleDiagnosticTests(s, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(s, Set.of(vaccineDoses[i], vaccineDoses[i-1])));
            }


            {
                Set<DiagnosticTest> toAdd = new HashSet<>();
                toAdd.add(vaccineDoses[i]);
                DiagnosticTest anotherDiagnosticTest = l.createDiagnosticTest(count++);
                toAdd.add(anotherDiagnosticTest);
                Assert.assertFalse(l.sampleDiagnosticTests(s, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(s, Set.of(vaccineDoses[i], anotherDiagnosticTest)));

                Assert.assertFalse(l.sampleDiagnosticTests(ss, toAdd));
                Assert.assertFalse(l.sampleDiagnosticTests(ss, Set.of(vaccineDoses[i])));
            }
        }
    }


}