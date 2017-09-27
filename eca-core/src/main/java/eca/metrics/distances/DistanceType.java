package eca.metrics.distances;

/**
 * Distance function type.
 * @author Roman Batygin
 */

public enum DistanceType {

    EUCLID(DistanceDictionary.EUCLID_DESCRIPTION) {
        @Override
        public <T> T handle(DistanceTypeVisitor<T> distanceTypeVisitor) {
            return distanceTypeVisitor.caseEuclid();
        }
    },

    SQUARE_EUCLID(DistanceDictionary.SQUARE_EUCLID_DESCRIPTION) {
        @Override
        public <T> T handle(DistanceTypeVisitor<T> distanceTypeVisitor) {
            return distanceTypeVisitor.caseSquareEuclid();
        }
    },

    MANHATTAN(DistanceDictionary.MANHATTAN_DISTANCE) {
        @Override
        public <T> T handle(DistanceTypeVisitor<T> distanceTypeVisitor) {
            return distanceTypeVisitor.caseManhattan();
        }
    },

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
    public String getDescription() {
        return description;
    }

    /**
     * Returns distance functions description.
     * @return distance functions description
     */
    public static String[] getDescriptions() {
        DistanceType[] values = values();
        String[] descriptions = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            descriptions[i] = values[i].getDescription();
        }
        return descriptions;
    }

    /**
     * Finds distance function type by description
     *
     * @param description description string.
     * @return {@link DistanceType} object
     */
    public static DistanceType findByDescription(String description) {
        for (DistanceType distanceType : values()) {
            if (distanceType.getDescription().equals(description)) {
                return distanceType;
            }
        }
        return null;
    }

    /**
     * Visitor pattern common method
     *
     * @param distanceTypeVisitor visitor class
     * @param <T> generic class
     * @return generic class
     */
    public abstract <T> T handle(DistanceTypeVisitor<T> distanceTypeVisitor);
}
