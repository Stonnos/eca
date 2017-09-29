package eca.neural.functions;

/**
 * Activation function formulas dictionary.
 * @author Roman Batygin
 */
public class ActivationFunctionFormulasDictionary {

    public static final String LOGISTIC_FORMULA = "f(S)=1/(1+exp(-a*S))";
    public static final String HYPERBOLIC_TANGENT_FORMULA = "f(S)=(exp(a*S)-exp(-a*S))/(exp(a*S)+exp(-a*S))";
    public static final String SINUSOID_FORMULA = "f(S)=sin(a*S)";
    public static final String EXPONENTIAL_FORMULA = "f(S)=exp(-S^2/a^2)";
    public static final String SOFT_SIGN_FORMULA = "f(S)=S/(1+|S|)";
}
