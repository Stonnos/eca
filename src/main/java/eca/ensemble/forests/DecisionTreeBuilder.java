package eca.ensemble.forests;

import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import eca.trees.ID3;

/**
 * Class for creation decision tree models.
 * @author Roman Batygin
 */
public class DecisionTreeBuilder implements DecisionTreeTypeVisitor<DecisionTreeClassifier> {

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
