package eca.ensemble.sampling;

/**
 * Sampling method type visitor pattern.
 * @param <T> - generic type
 *
 * @author Roman Batygin
 */
public interface SamplingMethodTypeVisitor<T> {

    /**
     * Method executed in case if sampling method type is INITIAL.
     *
     * @return generic object
     */
    T caseInitial();

    /**
     * Method executed in case if sampling method type is BAGGING.
     *
     * @return generic object
     */
    T caseBagging();

    /**
     * Method executed in case if sampling method type is RANDOM.
     *
     * @return generic object
     */
    T caseRandom();

    /**
     * Method executed in case if sampling method type is RANDOM_BAGGING.
     *
     * @return generic object
     */
    T caseRandomBagging();
}
