package eca.ensemble.forests;

import eca.trees.*;

import java.io.Serializable;

/**
 * Class for creation decision tree models.
 *
 * @author Roman Batygin
 */
public class DecisionTreeBuilder implements DecisionTreeTypeVisitor<DecisionTreeClassifier>, Serializable {

    @Override
    public DecisionTreeClassifier handleCartTree() {
        return new CART();
    }

    @Override
    public DecisionTreeClassifier handleId3Tree() {
        return new ID3();
    }

    @Override
    public DecisionTreeClassifier handleC45Tree() {
        return new C45();
    }

    @Override
    public DecisionTreeClassifier handleChaidTree() {
        return new CHAID();
    }
}
