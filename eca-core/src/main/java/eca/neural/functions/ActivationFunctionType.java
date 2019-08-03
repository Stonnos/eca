package eca.neural.functions;

import eca.core.DescriptiveEnum;

/**
 * Activation function type.
 *
 * @author Roman Batygin
 */

public enum ActivationFunctionType implements DescriptiveEnum {

    /**
     * Logistic function.
     */
    LOGISTIC(ActivationFunctionsDictionary.LOGISTIC_TEXT,
            ActivationFunctionFormulasDictionary.LOGISTIC_FORMULA,
            ActivationFunctionFormulasDictionary.LOGISTIC_FORMULA_FORMAT) {
        @Override
        public <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor) {
            return activationFunctionTypeVisitor.caseLogistic();
        }
    },

    /**
     * Hyperbolic tangent function.
     */
    HYPERBOLIC_TANGENT(ActivationFunctionsDictionary.HYPERBOLIC_TANGENT_TEXT,
            ActivationFunctionFormulasDictionary.HYPERBOLIC_TANGENT_FORMULA,
            ActivationFunctionFormulasDictionary.HYPERBOLIC_TANGENT_FORMULA_FORMAT) {
        @Override
        public <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor) {
            return activationFunctionTypeVisitor.caseHyperbolicTangent();
        }
    },

    /**
     * Sinusoid function.
     */
    SINUSOID(ActivationFunctionsDictionary.SINUSOID_TEXT,
            ActivationFunctionFormulasDictionary.SINUSOID_FORMULA,
            ActivationFunctionFormulasDictionary.SINUSOID_FORMULA_FORMAT) {
        @Override
        public <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor) {
            return activationFunctionTypeVisitor.caseSinusoid();
        }
    },

    /**
     * Exponential function.
     */
    EXPONENTIAL(ActivationFunctionsDictionary.EXPONENTIAL_TEXT,
            ActivationFunctionFormulasDictionary.EXPONENTIAL_FORMULA,
            ActivationFunctionFormulasDictionary.EXPONENTIAL_FORMULA_FORMAT) {
        @Override
        public <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor) {
            return activationFunctionTypeVisitor.caseExponential();
        }
    },

    /**
     * Soft sign function.
     */
    SOFT_SIGN(ActivationFunctionsDictionary.SOFT_SIGN_TEXT,
            ActivationFunctionFormulasDictionary.SOFT_SIGN_FORMULA,
            ActivationFunctionFormulasDictionary.SOFT_SIGN_FORMULA_FORMAT) {
        @Override
        public <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor) {
            return activationFunctionTypeVisitor.caseSoftSign();
        }
    },

    /**
     * Inverse square root unit.
     */
    INVERSE_SQUARE_ROOT_UNIT(ActivationFunctionsDictionary.INVERSE_SQUARE_ROOT_UNIT_TEXT,
            ActivationFunctionFormulasDictionary.ISRU_FORMULA,
            ActivationFunctionFormulasDictionary.ISRU_FORMULA_FORMAT) {
        @Override
        public <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor) {
            return activationFunctionTypeVisitor.caseInverseSquareRootUnit();
        }
    };

    private String description;
    private String formula;
    private String formulaFormat;

    /**
     * Constructor with activation function type params.
     *
     * @param description   - description
     * @param formula       - formula text
     * @param formulaFormat - formula format text
     */
    ActivationFunctionType(String description, String formula, String formulaFormat) {
        this.description = description;
        this.formula = formula;
        this.formulaFormat = formulaFormat;
    }

    /**
     * Returns activation function description.
     *
     * @return activation function description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Returns activation function formula.
     *
     * @return activation function formula
     */
    public String getFormula() {
        return formula;
    }

    /**
     * Returns activation function formula format.
     *
     * @return activation function formula format
     */
    public String getFormulaFormat() {
        return formulaFormat;
    }

    /**
     * Visitor pattern common method
     *
     * @param activationFunctionTypeVisitor visitor class
     * @param <T>                           generic class
     * @return generic class
     */
    public abstract <T> T handle(ActivationFunctionTypeVisitor<T> activationFunctionTypeVisitor);
}
