package eca.ensemble.forests;

/**
 * Random forests type enum.
 *
 * @author Roman Batygin
 */
public enum RandomForestsType {

    /**
     * Random forests basic algorithm
     */
    RANDOM_FORESTS {
        @Override
        public <T> T handle(RandomForestsTypeVisitor<T> randomForestsTypeVisitor) {
            return randomForestsTypeVisitor.caseRandomForests();
        }
    },

    /**
     * Extra trees algorithm
     */
    EXTRA_TREES {
        @Override
        public <T> T handle(RandomForestsTypeVisitor<T> randomForestsTypeVisitor) {
            return randomForestsTypeVisitor.caseExtraTrees();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param randomForestsTypeVisitor visitor class
     * @param <T>                      generic class
     * @return generic class
     */
    public abstract <T> T handle(RandomForestsTypeVisitor<T> randomForestsTypeVisitor);
    }
