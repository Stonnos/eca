/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

/**
 * Class for generating C4.5 decision tree model.
 *
 * @author Roman Batygin
 */
public class C45 extends ID3 {

    public C45() {
        splitAlgorithm = new C45SplitAlgorithm();
    }

    private class C45SplitAlgorithm extends Id3SplitAlgorithm {

        @Override
        public double getMeasure(TreeNode x) {
            if (x.rule.attribute().isNumeric() ||
                    x.rule.attribute().numValues() < 0.3 * (double) x.objectsNum()) {
                return super.getMeasure(x);
            } else {
                double measure = super.getMeasure(x);
                double splitInfo = splitInfo(x);
                return splitInfo != 0.0 ? measure / splitInfo : splitInfo;
            }
        }

        double splitInfo(TreeNode x) {
            double splitInfo = 0.0;
            for (int i = 0; i < childrenSizes.length; i++) {
                splitInfo += log((double) childrenSizes[i] / x.objectsNum());
            }
            return -splitInfo;
        }

    }

}
