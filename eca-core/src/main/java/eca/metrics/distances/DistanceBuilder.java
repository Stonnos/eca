package eca.metrics.distances;

/**
 * Distance function builder.
 * @author Roman Batygin
 */

public class DistanceBuilder implements DistanceTypeVisitor<Distance> {

    @Override
    public Distance caseEuclid() {
        return new EuclidDistance();
    }

    @Override
    public Distance caseSquareEuclid() {
        return new SquareEuclidDistance();
    }

    @Override
    public Distance caseManhattan() {
        return new ManhattanDistance();
    }

    @Override
    public Distance caseChebyshev() {
        return new ChebyshevDistance();
    }
}
