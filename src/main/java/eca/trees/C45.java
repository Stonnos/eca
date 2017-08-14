/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

/**
 * Class for generating C4.5 decision tree model.
 *
 * @author Рома
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
                double splitInfo = splitInfo(x);
                return splitInfo != 0.0 ? super.getMeasure(x) / splitInfo : splitInfo;
            }
        }

        double splitInfo(TreeNode x) {
            double splitInfo = 0.0;
            for (TreeNode child : x.children()) {
                splitInfo += log((double) child.objectsNum() / x.objectsNum());
            }
            return -splitInfo;
        }

    }

}
