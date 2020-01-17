package eca.neural.functions;

import lombok.experimental.UtilityClass;

/**
 * Activation function formulas dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ActivationFunctionFormulasDictionary {

    public static final String LOGISTIC_FORMULA = "f(S)=1/(1+exp(-S))";
    public static final String HYPERBOLIC_TANGENT_FORMULA =
            "f(S)=(exp(S)-exp(-S))/(exp(S)+exp(-S))";
    public static final String SINUSOID_FORMULA = "f(S)=sin(S)";
    public static final String EXPONENTIAL_FORMULA = "f(S)=exp(-S^2)";
    public static final String SOFT_SIGN_FORMULA = "f(S)=S/(1+|S|)";
    public static final String ISRU_FORMULA = "f(S)=S/sqrt(1+S^2)";

    public static final String LOGISTIC_FORMULA_FORMAT = "f(S)=1/(1+exp(-%s*S))";
    public static final String HYPERBOLIC_TANGENT_FORMULA_FORMAT =
            "f(S)=(exp(%1$s*S)-exp(-%1$s*S))/(exp(%1$s*S)+exp(-%1$s*S))";
    public static final String SINUSOID_FORMULA_FORMAT = "f(S)=sin(%s*S)";
    public static final String EXPONENTIAL_FORMULA_FORMAT = "f(S)=exp(-S^2/%s^2)";
    public static final String SOFT_SIGN_FORMULA_FORMAT = "f(S)=%s*S/(1+|S|)";
    public static final String ISRU_FORMULA_FORMAT = "f(S)=S/sqrt(1+%s*S^2)";
}
