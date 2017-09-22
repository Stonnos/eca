package eca.ensemble;

/**
 * Ensemble algorithms utility class.
 *
 * @author Roman Batygin
 */
public class EnsembleUtils {

    /**
     * Calculates classifier weight.
     *
     * @param error classifier error value
     * @return classifier weight
     */
    public static double getClassifierWeight(double error) {
        return 0.5 * Math.log((1.0 - error) / error);
    }
}
