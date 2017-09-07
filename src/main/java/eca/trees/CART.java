/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

/**
 * Class for generating CART decision tree model.
 *
 * @author Рома
 */
public class CART extends DecisionTreeClassifier {

    public CART() {
        splitAlgorithm = new CartSplitAlgorithm();
    }

    @Override
    public boolean getUseBinarySplits() {
        return true;
    }

    @Override
    public void setUseBinarySplits(boolean flag) {
    }

    private class CartSplitAlgorithm implements SplitAlgorithm {

        @Override
        public boolean isBetterSplit(double currentMeasure, double measure) {
            return measure < currentMeasure;
        }

        @Override
        public double getMaxMeasure() {
            return Double.MAX_VALUE;
        }

        @Override
        public double getMeasure(TreeNode x) {
            return giniSplit(x);
        }

        double giniIndex(TreeNode x) {
            double giniIndex = 0.0;
            probabilities(x);
            for (double probability : probabilities) {
                giniIndex += probability * probability;
            }
            return 1.0 - giniIndex;
        }

        double giniSplit(TreeNode x) {
            double infoS = 0.0;
            for (TreeNode child : x.children()) {
                infoS += child.objectsNum() * giniIndex(child) / x.objectsNum();
            }
            return infoS;
        }
    }

}
