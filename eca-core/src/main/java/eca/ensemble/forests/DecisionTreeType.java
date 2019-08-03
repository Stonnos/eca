package eca.ensemble.forests;

import eca.core.DescriptiveEnum;
import eca.dictionary.ClassifiersNamesDictionary;

/**
 * Decision tree algorithm enum.
 *
 * @author Roman Batygin
 */
public enum DecisionTreeType implements DescriptiveEnum {

    /**
     * CART algorithm.
     */
    CART(ClassifiersNamesDictionary.CART) {
        @Override
        public <T> T handle(DecisionTreeTypeVisitor<T> decisionTreeTypeVisitor) {
            return decisionTreeTypeVisitor.handleCartTree();
        }
    },

    /**
     * Id3 algorithm.
     */
    ID3(ClassifiersNamesDictionary.ID3) {
        @Override
        public <T> T handle(DecisionTreeTypeVisitor<T> decisionTreeTypeVisitor) {
            return decisionTreeTypeVisitor.handleId3Tree();
        }
    },

    /**
     * C4.5 algorithm.
     */
    C45(ClassifiersNamesDictionary.C45) {
        @Override
        public <T> T handle(DecisionTreeTypeVisitor<T> decisionTreeTypeVisitor) {
            return decisionTreeTypeVisitor.handleC45Tree();
        }
    },

    /**
     * CHAID algorithm.
     */
    CHAID(ClassifiersNamesDictionary.CHAID) {
        @Override
        public <T> T handle(DecisionTreeTypeVisitor<T> decisionTreeTypeVisitor) {
            return decisionTreeTypeVisitor.handleChaidTree();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param decisionTreeTypeVisitor visitor class
     * @param <T>                     generic class
     * @return generic class
     */
    public abstract <T> T handle(DecisionTreeTypeVisitor<T> decisionTreeTypeVisitor);

    private String description;

    DecisionTreeType(String description) {
        this.description = description;
    }

    /**
     * Returns algorithm description.
     *
     * @return algorithm description
     */
    @Override
    public String getDescription() {
        return description;
    }
}
