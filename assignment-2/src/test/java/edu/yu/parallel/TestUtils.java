package edu.yu.parallel;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TestUtils {
    /**
     * Sleep a random amount of time
     * Ignores the exception
     * <p>
     *
     * @param maxMilliseconds Maximum number of milliseconds to sleep.
     * @return None.
     */
    public static void SleepRandomTime(long maxMilliseconds) {
        try {
            Thread.sleep(new Random().nextLong(0L, maxMilliseconds));
        } catch (InterruptedException e) {
        }
    }

    /**
     * Atomically ensures that the specified AtomicInteger holds
     * max(its current value, specified value), using possibly multiple CASes,
     * and returns this max value.
     * <p>
     * If the specified AtomicInteger is initially found to hold
     * a value superior or equal to the specified value, this method has
     * volatile read semantics, else, it has volatile read and write semantics.
     *
     * @param atomic An AtomicInteger.
     * @param value  A value.
     * @return max(atomic, value).
     */
    public static int ensureMaxAndGet(AtomicInteger atomic, int value) {
        int tmpLastReturned;
        do {
            tmpLastReturned = atomic.get();
            if (tmpLastReturned >= value) {
                return tmpLastReturned;
            }
            // Here, value > tmpLastReturned,
            // so we will try to set it as new value.
        } while (!atomic.compareAndSet(tmpLastReturned, value));
        return value;
    }

    /**
     * Atomically ensures that the specified AtomicLong holds
     * max(its current value, specified value), using possibly multiple CASes,
     * and returns this max value.
     * <p>
     * If the specified AtomicLong is initially found to hold
     * a value superior or equal to the specified value, this method has
     * volatile read semantics, else, it has volatile read and write semantics.
     *
     * @param atomic An AtomicLong.
     * @param value  A value.
     * @return max(atomic, value).
     */
    public static long ensureMaxAndGet(AtomicLong atomic, long value) {
        long tmpLastReturned;
        do {
            tmpLastReturned = atomic.get();
            if (tmpLastReturned >= value) {
                return tmpLastReturned;
            }
            // Here, value > tmpLastReturned,
            // so we will try to set it as new value.
        } while (!atomic.compareAndSet(tmpLastReturned, value));
        return value;
    }
}
