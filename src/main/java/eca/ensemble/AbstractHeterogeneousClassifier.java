/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

/**
 *
 * @author Рома
 */
public abstract class AbstractHeterogeneousClassifier extends IterativeEnsembleClassifier {

    public static final double MIN_ERROR_THRESHOLD = 0.0;
    public static final double MAX_ERROR_THRESHOLD = 0.5;

    protected ClassifiersSet set;
    protected double max_error = MAX_ERROR_THRESHOLD;
    protected double min_error = MIN_ERROR_THRESHOLD;

    protected AbstractHeterogeneousClassifier(ClassifiersSet set) {
        this.set = set;
    }

    public final ClassifiersSet getClassifiersSet() {
        return set;
    }

    public void setClassifiersSet(ClassifiersSet set) {
        this.set = set;
    }

    public final void setMaxError(double max_error) {
        if (max_error <= min_error || max_error > MAX_ERROR_THRESHOLD) {
            throw new IllegalArgumentException("Значение допустимой ошибки"
                    + " классификатора должно лежать в интервале: [" + String.valueOf(MIN_ERROR_THRESHOLD)
                    + "," + String.valueOf(MAX_ERROR_THRESHOLD) + "]!");
        }
        this.max_error = max_error;
    }

    public final double getMaxError() {
        return max_error;
    }

    public final double getMinError() {
        return min_error;
    }

    public void setMinError(double min_error) {
        if (min_error < MIN_ERROR_THRESHOLD || min_error >= max_error) {
            throw new IllegalArgumentException("Значение допустимой ошибки"
                    + " классификатора должно лежать в интервале: [" + String.valueOf(MIN_ERROR_THRESHOLD)
                    + "," + String.valueOf(MAX_ERROR_THRESHOLD) + "]!");
        }
        this.min_error = min_error;
    }
}
