package eca.ensemble;

/**
 * @author Roman Batygin
 */
public interface SamplingMethodTypeVisitor<T> {

    T caseInitial();

    T caseBagging();

    T caseRandom();

    T caseRandomBagging();
}
