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
    private double[][] contingencyTable;

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
        List<String> options = super.getListOptions();
        options.add(DecisionTreeDictionary.SIGNIFICANT_LEVEL);
        options.add(String.valueOf(alpha));
        return options;
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        contingencyTable = getUseBinarySplits() || isUseRandomSplits() ? new double[data.numClasses() + 1][3]
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
            calculateProbabilities(x, false);
            computeContingencyTable(x);
            return hiSquaredTest();
        }

        double hiSquaredTest() {
            double hiSquaredVal = 0.0;
            for (int i = 0; i < contingencyTable.length - 1; i++) {
                for (int j = 0; j < contingencyTable[i].length - 1; j++) {
                    double theoryFreq = theoryFrequency(i, j);
                    if (theoryFreq != 0.0) {
                        hiSquaredVal += (contingencyTable[i][j] - theoryFreq)
                                * (contingencyTable[i][j] - theoryFreq) / theoryFreq;
                    }
                }
            }
            return hiSquaredVal;
        }

        void computeContingencyTable(TreeNode x) {
            for (int i = 0; i < contingencyTable.length; i++) {
                if (getUseBinarySplits()) {
                    for (int j = 0; j < contingencyTable[i].length; j++) {
                        contingencyTable[i][j] = 0;
                    }
                } else {
                    contingencyTable[i] = new double[x.getRule().attribute().isNumeric()
                            ? 3 : x.getRule().attribute().numValues() + 1];
                }
            }
            for (int i = 0; i < contingencyTable.length - 1; i++) {
                for (int j = 0; j < contingencyTable[i].length - 1; j++) {
                    contingencyTable[i][j] = probabilitiesMatrix[j][i];
                    contingencyTable[i][getTableSize() - 1] += contingencyTable[i][j];
                    contingencyTable[contingencyTable.length - 1][j] += contingencyTable[i][j];
                }
                contingencyTable[contingencyTable.length - 1][getTableSize() - 1]
                        += contingencyTable[i][getTableSize() - 1];
            }
        }

        int getTableSize() {
            return contingencyTable[0].length;
        }

        double theoryFrequency(int i, int j) {
            return (contingencyTable[contingencyTable.length - 1][j]
                    * contingencyTable[i][getTableSize() - 1])
                    / contingencyTable[contingencyTable.length - 1][getTableSize() - 1];
        }

    }

}
