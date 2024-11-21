package edu.yu.parallel;

public class SequentialNorm extends AbstractArrayNorm {

    static double compute(int[] array) {
        SequentialNorm norm = new SequentialNorm(array);
        return norm.compute();
    }

    public SequentialNorm(int[] array) {
        super(array);
    }

    @Override
    protected double computeNorm() {
        double sum = 0D;
        for (int value : array) {
            sum += value * value; // Square each element and sum
        }
        return Math.sqrt(sum); // Take square root of the sum
    }

}
