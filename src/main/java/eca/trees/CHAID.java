/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

import java.util.Enumeration;

import weka.core.Attribute;
import weka.core.Instances;
import eca.statistics.Statistics;

/**
 *
 * @author Рома
 */
public class CHAID extends DecisionTreeClassifier {

    private double[][] contingency_table;
    private double alpha = 0.05;
    private boolean use_binary_splits = true;

    public CHAID() {
        splitAlgorithm = new ChaidSplitAlgorithm();
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setUseBinarySplits(boolean flag) {
        this.use_binary_splits = flag;
    }

    public boolean getUseBinarySplits() {
        return use_binary_splits;
    }

    @Override
    public String[] getOptions() {
        String[] options = {"Минимальное число объектов в листе:", String.valueOf(minObj),
            "Максиальная глубина дерева:", String.valueOf(maxDepth),
            "Случайное дерево:", String.valueOf(isRandom),
            "Число случайных атрибутов:", String.valueOf(numRandomAttr),
            "Бинарное дерево:", String.valueOf(use_binary_splits),
            "Уровень значимости alpha:", String.valueOf(alpha)};
        return options;
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        contingency_table = getUseBinarySplits() ? new double[data.numClasses() + 1][3]
                : new double[data.numClasses() + 1][];
        super.buildClassifier(data);
    }

    @Override
    protected final SplitDescriptor createOptSplit(TreeNode x) {
        currentMeasure = -Double.MAX_VALUE;
        SplitDescriptor split = new SplitDescriptor();

        for (Enumeration<Attribute> e = attributes(); e.hasMoreElements();) {
            Attribute a = e.nextElement();
            if (a.isNumeric()) {
                processNumericSplit(a, x, splitAlgorithm, split);
            } else {
                if (getUseBinarySplits()) {
                    processBinarySplit(a, x, splitAlgorithm, split);
                } else {
                    processNominalSplit(a, x, splitAlgorithm, split);
                }
            }
        }
        return split;
    }

    @Override
    protected boolean isSplit(TreeNode x) {
        int df;
        if (getUseBinarySplits() || x.rule.attribute().isNumeric()) {
            df = data.numClasses() - 1;
        }
        else {
            df = (data.numClasses() - 1) * (x.rule.attribute().numValues() - 1);
        }
        return currentMeasure > Statistics.chiSquaredCriticalValue(alpha, df)
                && x.isSplit(minObj);
    }


    private class ChaidSplitAlgorithm implements SplitAlgorithm {

        @Override
        public boolean isBetterSplit(double measure) {
            return measure > currentMeasure;
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

        int frequency(TreeNode x, double c) {
            int freq = 0;
            for (int i = 0; i < x.objects().numInstances(); i++) {
                if (x.objects().instance(i).classValue() == c) {
                    freq++;
                }
            }
            return freq;
        }

    }

}
