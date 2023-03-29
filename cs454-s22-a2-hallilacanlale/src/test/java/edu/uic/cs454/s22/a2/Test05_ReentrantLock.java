package edu.uic.cs454.s22.a2;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.locks.Lock;

public class Test05_ReentrantLock {

    @Test
    public void testBasicReentrancy() {
        Lock l = Lab.createLock();

        l.lock();
        Assert.assertTrue(l.tryLock());
        l.unlock();
        l.unlock();
    }

    @Test
    public void threadAcquiresSameLock() {
        Lock l = Lab.createLock();
        int counter = 0;
        int n_increments = 1_000;

        l.lock();

        try {
            for (int i = 0; i < n_increments; i++) {
                l.lock();
                try {
                    counter += 1;
                } finally {
                    l.unlock();
                }
            }
        } finally {
            l.unlock();
        }

        Assert.assertEquals(n_increments, counter);
        Assert.assertTrue(l.tryLock());
    }

    @Test
    public void threadAcquiresSameLockManyTimes() {
        int n_increments = 1_000;
        int counter = 0;

        Lock l1 = Lab.createLock();
        l1.lock();
        try {
            for (int i = 0; i < n_increments; i++) {
                l1.lock();
            }
            for (int i = 0 ; i < n_increments ; i++) {
                counter += 1;
            }
            for (int i = 0; i < n_increments; i++) {
                l1.unlock();
            }
        } finally {
            l1.unlock();
        }

        Assert.assertEquals(n_increments, counter);
        Assert.assertTrue(l1.tryLock());
    }

    @Test
    public void otherThreadCannotUnlock() {
        Lock l = Lab.createLock();

        Thread t1 = new Thread(() -> {
            l.lock();
            l.lock();
            l.unlock(); });

        Test02_TwoThreadsLock.runAllThreads(t1);

        boolean exceptionCaught = false;

        try {
            l.unlock();
            Assert.fail(); // Should never execute
        } catch (IllegalMonitorStateException e) {
            exceptionCaught = true;
        }

        Assert.assertTrue(exceptionCaught);
    }

    @Test
    public void otherThreadCannotUnlockMultiple() {

        for (int i = 0 ; i < 10 ; i++) {
            Lock l = Lab.createLock();

            Random r = new Random();
            int locks = r.nextInt(1_000) + 1_000;
            int unlocks = r.nextInt(1_000);

            Thread t1 = new Thread(() -> {
                for (int j = 0 ; j < locks ; j++)
                    l.lock();

                for (int j = 0 ; j < unlocks ; j++)
                    l.unlock();
            });

            Test02_TwoThreadsLock.runAllThreads(t1);

            boolean exceptionCaught = false;

            try {
                l.unlock();
                Assert.fail(); // Should never execute
            } catch (IllegalMonitorStateException e) {
                exceptionCaught = true;
            }

            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void secondThreadWaitsForFirstToFinish() {

        for (int i = 0 ; i < 1 ; i++) {
            Test02_TwoThreadsLock.C c = new Test02_TwoThreadsLock.C();
            Lock l = Lab.createLock();

            Random r = new Random();
            int locks = r.nextInt(1_000);

            c.s = "";

            Thread t1 = new Thread(() -> {
                for (int j = 0 ; j < locks ; j++)
                    l.lock();

                try {
                    Thread.sleep(1000);
                    c.s += "T1";
                } catch (InterruptedException e) {
                    return;
                } finally {
                    for (int j = 0 ; j < locks ; j++)
                        l.unlock();
                }
            });

            Thread t2 = new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    return;
                }
                l.lock();
                try {
                    c.s += "T2";
                } finally {
                    l.unlock();
                }
            });

            Test02_TwoThreadsLock.runAllThreads(t1, t2);

            Assert.assertEquals("T1T2", c.s);
        }
    }

}
