package eca.ensemble.sampling;

import lombok.experimental.UtilityClass;

/**
 * Sampling method dictionary class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class SamplingDictionary {

    public static final String INITIAL_TEXT = "Использование исходной выборки";
    public static final String BAGGING_TEXT = "Бутстрэп выборки";
    public static final String RANDOM_TEXT = "Случайные подвыборки";
    public static final String RANDOM_BAGGING_TEXT = "Бутстрэп выборки случайного размера";
}
