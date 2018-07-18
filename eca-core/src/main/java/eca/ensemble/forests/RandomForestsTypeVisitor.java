package eca.ensemble.forests;

/**
 * Random forests type visitor.
 *
 * @author Roman Batygin
 */
public interface RandomForestsTypeVisitor<T> {

    /**
     * Method executed in case if algorithm type is Random forests.
     *
     * @return generic object
     */
    T caseRandomForests();

    /**
     * Method executed in case if algorithm type is Extra trees.
     *
     * @return generic object
     */
    T caseExtraTrees();
}
