package eca.neural.functions;

/**
 * Activation function type.
 * @author Roman Batygin
 */

public enum ActivationFunctionType {

    LOGISTIC(ActivationFunctionsDictionary.LOGISTIC_TEXT,
            ActivationFunctionFormulasDictionary.LOGISTIC_FORMULA) {
        @Override
        public <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor) {
            return activationFunctionTypeVisitor.caseLogistic();
        }
    },

    HYPERBOLIC_TANGENT(ActivationFunctionsDictionary.HYPERBOLIC_TANGENT_TEXT,
            ActivationFunctionFormulasDictionary.HYPERBOLIC_TANGENT_FORMULA) {
        @Override
        public <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor) {
            return activationFunctionTypeVisitor.caseHyperbolicTangent();
        }
    },

    SINUSOID(ActivationFunctionsDictionary.SINUSOID_TEXT,
            ActivationFunctionFormulasDictionary.SINUSOID_FORMULA) {
        @Override
        public <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor) {
            return activationFunctionTypeVisitor.caseSine();
        }
    },

    EXPONENTIAL(ActivationFunctionsDictionary.EXPONENTIAL_TEXT,
            ActivationFunctionFormulasDictionary.EXPONENTIAL_FORMULA) {
        @Override
        public <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor) {
            return activationFunctionTypeVisitor.caseExponential();
        }
    };

    private String description;
    private String formula;

    ActivationFunctionType(String description, String formula) {
        this.description = description;
        this.formula = formula;
    }

    /**
     * Returns activation function description.
     *
     * @return activation function description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns activation function formula.
     * @return activation function formula
     */
    public String getFormula() {
        return formula;
    }

    /**
     * Finds activation function type by description
     *
     * @param description description string.
     * @return {@link ActivationFunctionType} object
     */
    public static ActivationFunctionType findByDescription(String description) {
        for (ActivationFunctionType activationFunctionType : values()) {
            if (activationFunctionType.getDescription().equals(description)) {
                return activationFunctionType;
            }
        }
        return null;
    }

    /**
     * Visitor pattern common method
     *
     * @param activationFunctionTypeVisitor visitor class
     * @param <T>                     generic class
     * @return generic class
     */
    public abstract <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor);
}
