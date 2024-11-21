package edu.yu.parallel;

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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
