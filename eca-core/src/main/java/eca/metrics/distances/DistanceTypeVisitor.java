package eca.metrics.distances;

/**
 * Distance type visitor pattern.
 * @param <T> - generic type
 *
 * @author Roman Batygin
 */

public interface DistanceTypeVisitor<T> {

    /**
     * Method executed in case if distance type is EUCLID.
     *
     * @return generic object
     */
    T caseEuclid();

    /**
     * Method executed in case if distance type is SQUARE_EUCLID.
     *
     * @return generic object
     */
    T caseSquareEuclid();

    /**
     * Method executed in case if distance type is MANHATTAN.
     *
     * @return generic object
     */
    T caseManhattan();

    /**
     * Method executed in case if distance type is CHEBYSHEV.
     *
     * @return generic object
     */
    T caseChebyshev();
}
