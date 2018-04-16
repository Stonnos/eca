/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.roc;

import eca.core.evaluation.Evaluation;
import eca.regression.Logistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Implements significant attributes selection based on ROC - analysis.
 *
 * @author Roman Batygin
 */
public class AttributesSelection {

    private static final double MIN_THRESHOLD_VALUE = 0.5;
    private static final double MAX_THRESHOLD_VALUE = 1.0;

    private final Instances data;

    private final double[][] aucAreas;

    private final double[] averageAUC;

    private double aucThresholdValue = 0.6;

    /**
     * Creates <tt>AttributesSelection</tt> object.
     *
     * @param data <tt>Instances</tt> object
     */
    public AttributesSelection(Instances data) {
        this.data = data;
        this.aucAreas = new double[data.numAttributes()][data.numClasses()];
        this.averageAUC = new double[data.numAttributes()];
        for (int k = 0; k < data.numClasses(); k++) {
            this.aucAreas[data.classIndex()][k] = Double.NaN;
        }
        this.averageAUC[data.classIndex()] = Double.NaN;
    }

    /**
     * Returns <tt>Instances</tt> object.
     *
     * @return <tt>Instances</tt> object
     */
    public Instances data() {
        return data;
    }

    /**
     * Calculates all significant attributes.
     *
     * @throws Exception
     */
    public void calculate() throws Exception {
        for (int i = 0; i < data.numAttributes(); i++) {
            if (i != data.classIndex()) {
                Instances set = createInstances(i);
                Logistic model = new Logistic();
                model.buildClassifier(set);
                Evaluation evaluation = new Evaluation(set);
                evaluation.evaluateModel(model, set);
                for (int k = 0; k < data.numClasses(); k++) {
                    aucAreas[i][k] = evaluation.areaUnderROC(k);
                    averageAUC[i] += aucAreas[i][k];
                }
                averageAUC[i] /= data.numClasses();
            }
        }
    }

    /**
     * Returns AUC threshold value.
     *
     * @return AUC threshold value
     */
    public double getAucThresholdValue() {
        return aucThresholdValue;
    }

    /**
     * Sets AUC threshold value.
     *
     * @param aucThresholdValue - AUC threshold value
     */
    public void setAucThresholdValue(double aucThresholdValue) {
        if (aucThresholdValue <= MIN_THRESHOLD_VALUE || aucThresholdValue >= MAX_THRESHOLD_VALUE) {
            throw new IllegalArgumentException(
                    String.format("AUC threshold value must lies in (%.1f, %.1f)!", MIN_THRESHOLD_VALUE,
                            MAX_THRESHOLD_VALUE));
        }
        this.aucThresholdValue = aucThresholdValue;
    }

    /**
     * Returns the array of under ROC areas.
     *
     * @return the array of under ROC areas
     */
    public double[][] underROCValues() {
        return aucAreas;
    }

    /**
     * Returns the array of under ROC average areas.
     *
     * @return the array of under ROC average areas
     */
    public double[] underROCAverageValues() {
        return averageAUC;
    }

    /**
     * Returns <tt>true</tt> if attribute is significant.
     *
     * @param attrIndex attribute index
     * @return <tt>true</tt> if attribute is significant
     */
    public boolean isSignificant(int attrIndex) {
        return averageAUC[attrIndex] > getAucThresholdValue();
    }

    private Instances createInstances(int attrIndex) {
        ArrayList<Attribute> attr = new ArrayList<>(2);
        attr.add(data.attribute(attrIndex).copy(data.attribute(attrIndex).name()));
        attr.add(data.classAttribute().copy(data.classAttribute().name()));
        Instances set = new Instances(data.relationName(), attr, data.numInstances());
        for (int i = 0; i < data.numInstances(); i++) {
            Instance obj = new DenseInstance(set.numAttributes());
            obj.setValue(0, data.get(i).value(attrIndex));
            obj.setValue(1, data.get(i).classValue());
            set.add(obj);
        }
        set.setClassIndex(1);
        return set;
    }
}
