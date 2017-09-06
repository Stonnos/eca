/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

import weka.core.Attribute;
import weka.core.Utils;

import java.util.Enumeration;

/**
 * Class for generating Id3 decision tree model.
 *
 * @author Рома
 */
public class ID3 extends DecisionTreeClassifier {

    public ID3() {
        splitAlgorithm = new Id3SplitAlgorithm();
    }

    @Override
    protected SplitDescriptor createOptSplit(TreeNode x) {
        SplitDescriptor split = new SplitDescriptor(x, -Double.MAX_VALUE);

        for (Enumeration<Attribute> e = attributes(); e.hasMoreElements(); ) {
            Attribute a = e.nextElement();
            if (a.isNumeric()) {
                processNumericSplit(a, splitAlgorithm, split);
            } else {
                processNominalSplit(a, splitAlgorithm, split);
            }
        }
        return split;
    }

    protected class Id3SplitAlgorithm implements SplitAlgorithm {

        @Override
        public boolean isBetterSplit(double currentMeasure, double measure) {
            return measure > currentMeasure;
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
