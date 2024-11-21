package edu.yu.parallel;

import java.util.Random;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class AbstractArrayNorm {
    private static final Logger logger = LogManager.getLogger(AbstractArrayNorm.class);
    protected final int[] array;

    public static int[] generateSeqArray(int size) {
        return IntStream.range(0, size).toArray();
    }

    public static int[] generateRandomArray(int size, int bound) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i=0; i < size; i++) {
            array[i] = random.nextInt(bound);
        }
        return array;
    }

    public AbstractArrayNorm(int[] array) {
        this.array = array;
    }

    public double compute() {
        long start = System.currentTimeMillis();
        double result = computeNorm();
        long end = System.currentTimeMillis();
        logger.info("Result: " + result);
        logger.info("Time: " + (end - start) + " ms");
        return result;
    }

    protected abstract double computeNorm();

}
