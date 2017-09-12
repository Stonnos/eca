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

        double info(TreeNode x) {
            double info = 0.0;
            probabilities(x);
            for (double probability : probabilities) {
                info += log(probability);
            }
            return -info;
        }

        double infoS(TreeNode x) {
            double infoS = 0.0;
            for (TreeNode child : x.children()) {
                infoS += child.objectsNum() * info(child) / x.objectsNum();
            }
            return infoS;
        }

        double gain(TreeNode x) {
            return info(x) - infoS(x);
        }
    }

}
