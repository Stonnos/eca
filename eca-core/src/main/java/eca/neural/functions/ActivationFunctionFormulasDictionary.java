package eca.neural.functions;

/**
 * Activation function formulas dictionary.
 * @author Roman Batygin
 */
public class ActivationFunctionFormulasDictionary {

    public static final String LOGISTIC_FORMULA = "f(S)=1/(1+exp(-%s*S))";
    public static final String HYPERBOLIC_TANGENT_FORMULA =
            "f(S)=(exp(%1$s*S)-exp(-%1$s*S))/(exp(%1$s*S)+exp(-%1$s*S))";
    public static final String SINUSOID_FORMULA = "f(S)=sin(%s*S)";
    public static final String EXPONENTIAL_FORMULA = "f(S)=exp(-S^2/%s^2)";
    public static final String SOFT_SIGN_FORMULA = "f(S)=%s*S/(1+|S|)";
}
