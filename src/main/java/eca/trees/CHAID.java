/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

import eca.statistics.Statistics;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.List;

/**
 * Class for generating CHAID decision tree model.
 *
 * @author Roman Batygin
 */
public class CHAID extends DecisionTreeClassifier {

    /**
     * Contingency table
     **/
    private double[][] contingency_table;

    /**
     * Significance level for hi square test
     **/
    private double alpha = 0.05;

    public CHAID() {
        splitAlgorithm = new ChaidSplitAlgorithm();
    }

    /**
     * Returns the value of significance level for hi square test.
     *
     * @return the value of significance level for hi square test
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Sets the value of significance level for hi square test.
     *
     * @param alpha the value of significance level for hi square test
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public List<String> getListOptions() {
        List<String> options = getListOptions();
        options.add(DecisionTreeDictionary.SIGNIFICANT_LEVEL);
        options.add(String.valueOf(alpha));
        return options;
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        contingency_table = getUseBinarySplits() ? new double[data.numClasses() + 1][3]
                : new double[data.numClasses() + 1][];
        super.buildClassifier(data);
    }

    @Override
    protected boolean isSplit(SplitDescriptor splitDescriptor) {
        TreeNode x = splitDescriptor.getNode();
        Attribute attribute = x.getRule().attribute();
        int df;
        if (getUseBinarySplits() || attribute.isNumeric()) {
            df = getData().numClasses() - 1;
        } else {
            df = (getData().numClasses() - 1) * (attribute.numValues() - 1);
        }
        return splitDescriptor.getCurrentMeasure() > Statistics.chiSquaredCriticalValue(alpha, df)
                && x.isSplit(getMinObj());
    }


    private class ChaidSplitAlgorithm implements SplitAlgorithm {

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
            computeContingencyTable(x);
            return hiSquaredTest();
        }

        double hiSquaredTest() {
            double h = 0.0;
            for (int i = 0; i < contingency_table.length - 1; i++) {
                for (int j = 0; j < contingency_table[i].length - 1; j++) {
                    double theoryFreq = theoryFrequency(i, j);
                    if (theoryFreq != 0.0) {
                        h += (contingency_table[i][j] - theoryFreq)
                                * (contingency_table[i][j] - theoryFreq) / theoryFreq;
                    }
                }
            }
            return h;
        }

        void computeContingencyTable(TreeNode x) {
            for (int i = 0; i < contingency_table.length; i++) {
                if (getUseBinarySplits()) {
                    for (int j = 0; j < contingency_table[i].length; j++) {
                        contingency_table[i][j] = 0;
                    }
                } else {
                    contingency_table[i] = new double[x.getRule().attribute().isNumeric()
                            ? 3 : x.getRule().attribute().numValues() + 1];
                }
            }

            for (int i = 0; i < contingency_table.length - 1; i++) {
                for (int j = 0; j < contingency_table[i].length - 1; j++) {
                    contingency_table[i][j] = frequency(x.getChild(j), i);
                    contingency_table[i][getTableSize() - 1] += contingency_table[i][j];
                    contingency_table[contingency_table.length - 1][j] += contingency_table[i][j];
                }
                contingency_table[contingency_table.length - 1][getTableSize() - 1]
                        += contingency_table[i][getTableSize() - 1];
            }
        }

        int getTableSize() {
            return contingency_table[0].length;
        }

        double theoryFrequency(int i, int j) {
            return (contingency_table[contingency_table.length - 1][j]
                    * contingency_table[i][getTableSize() - 1])
                    / contingency_table[contingency_table.length - 1][getTableSize() - 1];
        }

        int frequency(TreeNode x, double classValue) {
            int freq = 0;
            for (int i = 0; i < x.objects().numInstances(); i++) {
                if (x.objects().instance(i).classValue() == classValue) {
                    freq++;
                }
            }
            return freq;
        }

    }

}
