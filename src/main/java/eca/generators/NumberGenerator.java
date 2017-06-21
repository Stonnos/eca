package eca.generators;

import java.util.Random;

/**
 * Created by Roman93 on 16.04.2017.
 */
public class NumberGenerator {

    public  static double random(double lowerBound, double upperBound) {
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("upper bound must be greater than lower bound!");
        }
        return Math.random() * (upperBound - lowerBound) + lowerBound;
    }

    public static double nextGaussian(Random random, double mean, double variance) {
        return mean + variance * random.nextGaussian();
    }

    public static double nextGaussianWithNoise(Random random, double mean, double variance) {
        double noise = random(-0.5, 0.5);
        return nextGaussian(random, mean, variance) + noise;
    }
}
