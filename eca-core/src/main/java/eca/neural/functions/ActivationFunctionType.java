package eca.neural.functions;

/**
 * Activation function type.
 * @author Roman Batygin
 */

public enum ActivationFunctionType {

    LOGISTIC(ActivationFunctionsDictionary.LOGISTIC_TEXT,
            ActivationFunctionFormulasDictionary.LOGISTIC_FORMULA),

    HYPERBOLIC_TANGENT(ActivationFunctionsDictionary.HYPERBOLIC_TANGENT_TEXT,
            ActivationFunctionFormulasDictionary.HYPERBOLIC_TANGENT_FORMULA),

    SINE(ActivationFunctionsDictionary.SINE_TEXT,
            ActivationFunctionFormulasDictionary.SINE_FORMULA),

    EXPONENTIAL(ActivationFunctionsDictionary.EXPONENTIAL_TEXT,
            ActivationFunctionFormulasDictionary.EXPONENTIAL_FORMULA);

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
}
