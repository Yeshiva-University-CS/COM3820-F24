package edu.yu.parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinNorm extends AbstractArrayNorm {
    public static final int DEFAULT_THRESHOLD = 1000;
    private final int threshold;

    public static double compute(int[] array) {
        return compute(array, DEFAULT_THRESHOLD);
    }

    public static double compute(int[] array, int threshold) {
        ForkJoinNorm norm = new ForkJoinNorm(array, threshold);
        return norm.compute();
    }

    public ForkJoinNorm(int[] array) {
        this(array, DEFAULT_THRESHOLD);
    }

    public ForkJoinNorm(int[] array, int threshold) {
        super(array);
        this.threshold = threshold;
    }

    @Override
    protected double computeNorm() {
        ForkJoinPool pool = new ForkJoinPool();
        double sumOfSquares = pool.invoke(new NormTask(0, array.length));
        return Math.sqrt(sumOfSquares);
    }

    private class NormTask extends RecursiveTask<Double> {
        private final int start;
        private final int end;

        public NormTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Double compute() {
            if (end - start < threshold) {
                double sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i] * array[i];
                }
                return sum;
            } else {
                int mid = start + (end - start) / 2;
                NormTask left = new NormTask(start, mid);
                NormTask right = new NormTask(mid, end);
                left.fork();
                right.fork();
                return left.join() + right.join();
            }
        }
    }
}
