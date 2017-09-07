package eca.ensemble.forests;

import eca.gui.enums.ClassifiersNames;

/**
 * Decision tree algorithm enum.
 * @author Roman Batygin
 */
public enum DecisionTreeType {

    /**
     * CART algorithm.
     */
    CART(ClassifiersNames.CART) {
        @Override
        public <T> T handle(DecisionTreeTypeVisitor<T> decisionTreeTypeVisitor) {
            return decisionTreeTypeVisitor.handleCartTree();
        }
    },

    /**
     * Id3 algorithm.
     */
    ID3(ClassifiersNames.ID3) {
        @Override
        public <T> T handle(DecisionTreeTypeVisitor<T> decisionTreeTypeVisitor) {
            return decisionTreeTypeVisitor.handleId3Tree();
        }
    },

    /**
     * C4.5 algorithm.
     */
    C45(ClassifiersNames.C45) {
        @Override
        public <T> T handle(DecisionTreeTypeVisitor<T> decisionTreeTypeVisitor) {
            return decisionTreeTypeVisitor.handleC45Tree();
        }
    },

    /**
     * CHAID algorithm.
     */
    CHAID(ClassifiersNames.CHAID) {
        @Override
        public <T> T handle(DecisionTreeTypeVisitor<T> decisionTreeTypeVisitor) {
            return decisionTreeTypeVisitor.handleChaidTree();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param decisionTreeTypeVisitor visitor class
     * @param <T>         generic class
     * @return generic class
     */
    public abstract <T> T handle(DecisionTreeTypeVisitor<T> decisionTreeTypeVisitor);

    private String description;

    DecisionTreeType(String description) {
        this.description = description;
    }

    /**
     * Returns algorithm description.
     * @return algorithm description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Finds decision tree type by description
     * @param description description string.
     * @return {@link DecisionTreeType} object
     */
    public static DecisionTreeType findByDescription(String description) {
        for (DecisionTreeType treeType : values()) {
            if (treeType.getDescription().equals(description)) {
                return treeType;
            }
        }
        return null;
    }
}
