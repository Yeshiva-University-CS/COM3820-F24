package edu.yu.parallel;

public class SequentialStreamNorm extends AbstractArrayNorm {

    public static double compute(int[] array) {
        SequentialStreamNorm norm = new SequentialStreamNorm(array);
        return norm.compute();
    }

    public SequentialStreamNorm(int[] array) {
        super(array);
    }

    @Override
    protected double computeNorm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
