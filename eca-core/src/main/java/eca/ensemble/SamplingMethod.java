package eca.ensemble;

/**
 * Sampling method type.
 * @author Roman Batygin
 */
public enum SamplingMethod {

    /**
     * Initial sample type
     **/
    INITIAL(SamplingDictionary.INITIAL_TEXT) {
        @Override
        public <T> T handle(SamplingMethodTypeVisitor<T> samplingMethodTypeVisitor) {
            return samplingMethodTypeVisitor.caseInitial();
        }
    },

    /**
     * Bootstrap sample type
     **/
    BAGGING(SamplingDictionary.BAGGING_TEXT) {
        @Override
        public <T> T handle(SamplingMethodTypeVisitor<T> samplingMethodTypeVisitor) {
            return samplingMethodTypeVisitor.caseBagging();
        }
    },

    /**
     * Random sub sample type
     **/
    RANDOM(SamplingDictionary.RANDOM_TEXT) {
        @Override
        public <T> T handle(SamplingMethodTypeVisitor<T> samplingMethodTypeVisitor) {
            return samplingMethodTypeVisitor.caseRandom();
        }
    },

    /**
     * Random bootstrap sub sample type
     **/
    RANDOM_BAGGING(SamplingDictionary.RANDOM_BAGGING_TEXT) {
        @Override
        public <T> T handle(SamplingMethodTypeVisitor<T> samplingMethodTypeVisitor) {
            return samplingMethodTypeVisitor.caseRandomBagging();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param samplingMethodTypeVisitor visitor class
     * @param <T>                     generic class
     * @return generic class
     */
    public abstract <T> T handle(SamplingMethodTypeVisitor<T> samplingMethodTypeVisitor);

    private String description;

    SamplingMethod(String description) {
        this.description = description;
    }

    /**
     * Returns sampling method description.
     *
     * @return sampling method description
     */
    public String getDescription() {
        return description;
    }
}
