/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

/**
 * Abstract class for generating heterogeneous classifier model.
 * Valid options are: <p>
 *
 * Set individual classifiers collection  <p>
 *
 * Set minimum error threshold for including classifier in ensemble
 *
 * Set maximum error threshold for including classifier in ensemble
 *
 * @author Рома
 */
public abstract class AbstractHeterogeneousClassifier extends IterativeEnsembleClassifier {

    public static final double MIN_ERROR_THRESHOLD = 0.0;
    public static final double MAX_ERROR_THRESHOLD = 0.5;

    /** Classifiers set **/
    protected ClassifiersSet set;

    /** Minimum error threshold **/
    protected double max_error = MAX_ERROR_THRESHOLD;

    /** Maximum error threshold **/
    protected double min_error = MIN_ERROR_THRESHOLD;

    protected AbstractHeterogeneousClassifier(ClassifiersSet set) {
        this.set = set;
    }

    /**
     * Returns classifiers collection.
     * @return classifiers collection
     */
    public final ClassifiersSet getClassifiersSet() {
        return set;
    }

    /**
     * Sets classifiers collection.
     * @param set <tt>ClassifiersSet</tt> object
     */
    public void setClassifiersSet(ClassifiersSet set) {
        this.set = set;
    }

    /**
     * Sets the value of maximum error threshold for including classifier in ensemble.
     * @param max_error the value of maximum error threshold for including classifier in ensemble
     * @exception IllegalArgumentException if the value of maximum error is less or equal to
     * minimum error threshold or greater than {@value MAX_ERROR_THRESHOLD}
     */
    public final void setMaxError(double max_error) {
        if (max_error <= min_error || max_error > MAX_ERROR_THRESHOLD) {
            throw new IllegalArgumentException("Значение допустимой ошибки"
                    + " классификатора должно лежать в интервале: [" + String.valueOf(MIN_ERROR_THRESHOLD)
                    + "," + String.valueOf(MAX_ERROR_THRESHOLD) + "]!");
        }
        this.max_error = max_error;
    }

    /**
     * Returns the value of maximum error threshold for including classifier in ensemble.
     * @return the value of maximum error threshold for including classifier in ensemble
     */
    public final double getMaxError() {
        return max_error;
    }

    /**
     * Returns the value of minimum error threshold for including classifier in ensemble.
     * @return the value of minimum error threshold for including classifier in ensemble
     */
    public final double getMinError() {
        return min_error;
    }

    /**
     * Sets the value of minimum error threshold for including classifier in ensemble.
     * @param min_error the value of minimum error threshold for including classifier in ensemble
     * @exception IllegalArgumentException if the value of minimum error is less than
     * {@value MIN_ERROR_THRESHOLD} or greater or equal to maximum error threshold
     */
    public void setMinError(double min_error) {
        if (min_error < MIN_ERROR_THRESHOLD || min_error >= max_error) {
            throw new IllegalArgumentException("Значение допустимой ошибки"
                    + " классификатора должно лежать в интервале: [" + String.valueOf(MIN_ERROR_THRESHOLD)
                    + "," + String.valueOf(MAX_ERROR_THRESHOLD) + "]!");
        }
        this.min_error = min_error;
    }
}
