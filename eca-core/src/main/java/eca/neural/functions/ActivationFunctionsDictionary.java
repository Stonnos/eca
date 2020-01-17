package eca.neural.functions;

import lombok.experimental.UtilityClass;

/**
 * Activation functions dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ActivationFunctionsDictionary {

    public static final String LOGISTIC_TEXT = "Логистическая";
    public static final String HYPERBOLIC_TANGENT_TEXT = "Гиперболический тангенс";
    public static final String SINUSOID_TEXT = "Тригонометрический синус";
    public static final String EXPONENTIAL_TEXT = "Экспоненциальная";
    public static final String SOFT_SIGN_TEXT = "Функция SoftSign";
    public static final String INVERSE_SQUARE_ROOT_UNIT_TEXT = "Функция ISRU";

    public static final double DEFAULT_COEFFICIENT = 1.0;
}
