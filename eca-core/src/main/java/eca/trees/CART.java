package eca.trees;

import java.util.Arrays;

/**
 * Class for generating CART decision tree model.
 *
 * @author Roman Batygin
 */
public class CART extends DecisionTreeClassifier {

    public CART() {
        splitAlgorithm = new CartSplitAlgorithm();
    }

    @Override
    public boolean getUseBinarySplits() {
        return true;
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

        double giniIndex(double[] p) {
            double giniIndex = 0.0;
            double[] pCopy = Arrays.copyOf(p, p.length);
            eca.util.Utils.normalize(pCopy);
            for (double val : pCopy) {
                giniIndex += val * val;
            }
            return 1.0 - giniIndex;
        }

        double giniSplit(TreeNode x) {
            double infoS = 0.0;
            for (int i = 0; i < childrenSizes.length; i++) {
                infoS += childrenSizes[i] * giniIndex(probabilitiesMatrix[i]) / x.objectsNum();
            }
            return infoS;
        }
    }

}
