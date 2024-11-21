package edu.yu.parallel;

public class ParallelStreamNorm extends AbstractArrayNorm {

    public static double compute(int[] array) {
        ParallelStreamNorm norm = new ParallelStreamNorm(array);
        return norm.compute();
    }

    public ParallelStreamNorm(int[] array) {
        super(array);
    }

    @Override
    protected double computeNorm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
