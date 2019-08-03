package eca.metrics.distances;

import eca.core.DescriptiveEnum;

/**
 * Distance function type.
 *
 * @author Roman Batygin
 */
public enum DistanceType implements DescriptiveEnum {

    /**
     * Euclid distance
     */
    EUCLID(DistanceDictionary.EUCLID_DESCRIPTION) {
        @Override
        public <T> T handle(DistanceTypeVisitor<T> distanceTypeVisitor) {
            return distanceTypeVisitor.caseEuclid();
        }
    },

    /**
     * Square euclid distance
     */
    SQUARE_EUCLID(DistanceDictionary.SQUARE_EUCLID_DESCRIPTION) {
        @Override
        public <T> T handle(DistanceTypeVisitor<T> distanceTypeVisitor) {
            return distanceTypeVisitor.caseSquareEuclid();
        }
    },

    /**
     * Manhattan distance
     */
    MANHATTAN(DistanceDictionary.MANHATTAN_DISTANCE) {
        @Override
        public <T> T handle(DistanceTypeVisitor<T> distanceTypeVisitor) {
            return distanceTypeVisitor.caseManhattan();
        }
    },

    /**
     * Chebyshev distance
     */
    CHEBYSHEV(DistanceDictionary.CHEBYSHEV_DISTANCE) {
        @Override
        public <T> T handle(DistanceTypeVisitor<T> distanceTypeVisitor) {
            return distanceTypeVisitor.caseChebyshev();
        }
    };

    private String description;

    DistanceType(String description) {
        this.description = description;
    }

    /**
     * Returns distance function description.
     *
     * @return distance function description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Visitor pattern common method
     *
     * @param distanceTypeVisitor visitor class
     * @param <T>                 generic class
     * @return generic class
     */
    public abstract <T> T handle(DistanceTypeVisitor<T> distanceTypeVisitor);
}
