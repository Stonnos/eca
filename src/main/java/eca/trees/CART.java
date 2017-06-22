/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

import java.util.Enumeration;
import weka.core.Attribute;

/**
 * Class for generating CART decision tree model.
 * @author Рома
 */
public class CART extends DecisionTreeClassifier {

    public CART() {
        splitAlgorithm = new CartSplitAlgorithm();
    }

    @Override
    protected final SplitDescriptor createOptSplit(TreeNode x) {
        SplitDescriptor split = new SplitDescriptor();
        currentMeasure = Double.MAX_VALUE;

        for (Enumeration<Attribute> e = attributes(); e.hasMoreElements();) {
            Attribute a = e.nextElement();
            if (a.isNumeric()) {
                processNumericSplit(a, x, splitAlgorithm, split);
            } else {
                processBinarySplit(a, x, splitAlgorithm, split);
            }
        }

        return split;
    }


    private class CartSplitAlgorithm implements SplitAlgorithm {

        @Override
        public boolean isBetterSplit(double measure) {
            return measure < currentMeasure;
        }

        @Override
        public double getMeasure(TreeNode x) {
            return giniSplit(x);
        }

        double giniIndex(TreeNode x) {
            double giniIndex = 0.0;
            probabilities(x);
            for (int k = 0; k < probabilities.length; k++) {
                giniIndex += probabilities[k] * probabilities[k];
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
