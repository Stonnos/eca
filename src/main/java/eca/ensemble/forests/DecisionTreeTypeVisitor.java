package eca.ensemble.forests;

/**
 * Decision tree type visitor.
 *
 * @param <T> - generic type
 * @author Roman Batygin
 */

public interface DecisionTreeTypeVisitor<T> {

    /**
     * Method executed in case if decision tree type is CART.
     *
     * @return generic object
     */
    T handleCartTree();

    /**
     * Method executed in case if decision tree type is Id3.
     *
     * @return generic object
     */
    T handleId3Tree();

    /**
     * Method executed in case if decision tree type is C45.
     *
     * @return generic object
     */
    T handleC45Tree();

    /**
     * Method executed in case if decision tree type is CHAID.
     *
     * @return generic object
     */
    T handleChaidTree();
}
