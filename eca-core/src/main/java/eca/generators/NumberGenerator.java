package eca.generators;

import java.util.Random;

/**
 * Class for generating random numbers.
 *
 * @author Roman Batygin
 */
public class NumberGenerator {

    private static final double NOISE_LOWER_BOUND = -0.5;
    private static final double NOISE_UPPER_BOUND = 0.5;

    /**
     * Returns the number uniformly distributed in defined interval {@code [a, b)}.
     *
     * @param lowerBound lower bound of interval
     * @param upperBound upper bound of interval
     * @return the number uniformly distributed in defined interval {@code [a, b)}
     */
    public static double random(double lowerBound, double upperBound) {
        return transform(Math.random(), lowerBound, upperBound);
    }

    /**
     * Returns the number uniformly distributed in defined interval {@code [a, b]}.
     *
     * @param lowerBound lower bound of interval
     * @param upperBound upper bound of interval
     * @param random     random object
     * @return the number uniformly distributed in defined interval {@code [a, b]}
     */
    public static int randomInt(Random random, int lowerBound, int upperBound) {
        return random.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }

    /**
     * Returns the number uniformly distributed in defined interval {@code [a, b)}.
     *
     * @param lowerBound lower bound of interval
     * @param upperBound upper bound of interval
     * @param random     random object
     * @return the number uniformly distributed in defined interval {@code [a, b)}
     */
    public static double random(Random random, double lowerBound, double upperBound) {
        return transform(random.nextDouble(), lowerBound, upperBound);
    }

    /**
     * Returns the number with normal distribution and defined mean and standard deviation.
     *
     * @param random   <tt>Random</tt> object.
     * @param mean     mean of the normal distribution
     * @param variance standard deviation of the normal distribution
     * @return the number normally distributed with defined mean and standard deviation.
     */
    public static double nextGaussian(Random random, double mean, double variance) {
        return mean + variance * random.nextGaussian();
    }

    /**
     * Returns the number with normal distribution and additive noise uniformly
     * distributed in interval {@code [-0.5, 0.5)}.
     *
     * @param random   <tt>Random</tt> object.
     * @param mean     mean of the normal distribution
     * @param variance standard deviation of the normal distribution
     * @return the number normally distributed with defined mean and standard deviation.
     */
    public static double nextGaussianWithNoise(Random random, double mean, double variance) {
        double noise = random(NOISE_LOWER_BOUND, NOISE_UPPER_BOUND);
        return nextGaussian(random, mean, variance) + noise;
    }

    /**
     * Fills array by random values.
     *
     * @param values - specified array
     * @param random - random object
     */
    public static void fillRandom(int[] values, Random random) {
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt();
        }
    }

    private static double transform(double value, double lowerBound, double upperBound) {
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("Upper bound must be greater than lower bound!");
        }
        return value * (upperBound - lowerBound) + lowerBound;
    }
}
