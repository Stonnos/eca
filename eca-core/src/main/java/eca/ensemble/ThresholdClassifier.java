package eca.ensemble;

import eca.text.NumericFormatFactory;

import java.text.DecimalFormat;

/**
 * Abstract class for generating error threshold classifier model.
 *
 * @author Roman Batygin
 */
public abstract class ThresholdClassifier extends IterativeEnsembleClassifier {

    public static final DecimalFormat COMMON_DECIMAL_FORMAT = NumericFormatFactory.getInstance(Integer.MAX_VALUE);

    public static final double MIN_ERROR_THRESHOLD = 0.0;

    public static final double MAX_ERROR_THRESHOLD = 0.5;

    /**
     * Minimum error threshold
     **/
    private double maxError = MAX_ERROR_THRESHOLD;

    /**
     * Maximum error threshold
     **/
    private double minError = MIN_ERROR_THRESHOLD;

    /**
     * Sets the value of maximum error threshold for including classifier in ensemble.
     *
     * @param maxError the value of maximum error threshold for including classifier in ensemble
     * @throws IllegalArgumentException if the value of maximum error is less or equal to
     *                                  minimum error threshold or greater than {@value MAX_ERROR_THRESHOLD}
     */
    public void setMaxError(double maxError) {
        if (maxError <= minError || maxError > MAX_ERROR_THRESHOLD) {
            throw new IllegalArgumentException(
                    String.format(EnsembleDictionary.INVALID_ERROR_THRESHOLD_TEXT,
                            MIN_ERROR_THRESHOLD, MAX_ERROR_THRESHOLD));
        }
        this.maxError = maxError;
    }

    /**
     * Returns the value of maximum error threshold for including classifier in ensemble.
     *
     * @return the value of maximum error threshold for including classifier in ensemble
     */
    public double getMaxError() {
        return maxError;
    }

    /**
     * Returns the value of minimum error threshold for including classifier in ensemble.
     *
     * @return the value of minimum error threshold for including classifier in ensemble
     */
    public double getMinError() {
        return minError;
    }

    /**
     * Sets the value of minimum error threshold for including classifier in ensemble.
     *
     * @param minError the value of minimum error threshold for including classifier in ensemble
     * @throws IllegalArgumentException if the value of minimum error is less than
     *                                  {@value MIN_ERROR_THRESHOLD} or greater or equal to maximum error threshold
     */
    public void setMinError(double minError) {
        if (minError < MIN_ERROR_THRESHOLD || minError >= maxError) {
            throw new IllegalArgumentException(
                    String.format(EnsembleDictionary.INVALID_ERROR_THRESHOLD_TEXT,
                            MIN_ERROR_THRESHOLD, MAX_ERROR_THRESHOLD));
        }
        this.minError = minError;
    }

}

