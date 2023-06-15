package eca.trees;

import weka.core.Utils;

import java.util.Arrays;

/**
 * Class for generating Id3 decision tree model.
 *
 * @author Roman Batygin
 */
public class ID3 extends DecisionTreeClassifier {

    public ID3() {
        splitAlgorithm = new Id3SplitAlgorithm();
    }

    protected class Id3SplitAlgorithm implements SplitAlgorithm {

        private double currentInfoValue;

        @Override
        public boolean isBetterSplit(double currentMeasure, double measure) {
            return measure > currentMeasure;
        }

        @Override
        public double getMaxMeasure() {
            return -Double.MAX_VALUE;
        }

        @Override
        public double getMeasure(TreeNode x) {
            return gain(x);
        }

        @Override
        public void preProcess(TreeNode x) {
            calculateNodeProbabilities(x);
            currentInfoValue = info(probabilities);
        }

        double log(double p) {
            return p == 0 ? p : p * Utils.log2(p);
        }

        double info(double[] p) {
            double info = 0.0;
            double[] pCopy = Arrays.copyOf(p, p.length);
            eca.util.Utils.normalize(pCopy);
            for (double val : pCopy) {
                info += log(val);
            }
            return -info;
        }

        double infoS(TreeNode x) {
            double infoS = 0.0;
            for (int i = 0; i < childrenSizes.length; i++) {
                infoS += childrenSizes[i] * info(probabilitiesMatrix[i]) / x.objectsNum();
            }
            return infoS;
        }

        double gain(TreeNode x) {
            return currentInfoValue - infoS(x);
        }
    }

}
