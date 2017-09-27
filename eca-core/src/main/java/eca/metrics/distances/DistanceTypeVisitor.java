package eca.metrics.distances;

/**
 * Distance type visitor pattern.
 * @param <T> - generic type
 *
 * @author Roman Batygin
 */

public interface DistanceTypeVisitor<T> {

    T caseEuclid();

    T caseSquareEuclid();

    T caseManhattan();

    T caseChebyshev();
}
