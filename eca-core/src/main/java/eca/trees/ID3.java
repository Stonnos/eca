/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

import weka.core.Utils;

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

        double log(double p) {
            return p == 0 ? p : p * Utils.log2(p);
        }

        double info(double[] p) {
            double info = 0.0;
            for (double probability : p) {
                info += log(probability);
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
            calculateProbabilities(x);
            calculateProbabilities(x, true);
            return info(probabilities) - infoS(x);
        }
    }

}
