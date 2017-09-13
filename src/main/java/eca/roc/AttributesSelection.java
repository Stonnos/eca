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

    private static final double THRESHOLD_VALUE = 0.6;

    private final Instances data;

    private final double[][] auc;

    private final double[] avgAUC;

    /**
     * Creates <tt>AttributesSelection</tt> object.
     *
     * @param data <tt>Instances</tt> object
     */
    public AttributesSelection(Instances data) {
        this.data = data;
        this.auc = new double[data.numAttributes()][data.numClasses()];
        this.avgAUC = new double[data.numAttributes()];

        for (int k = 0; k < data.numClasses(); k++) {
            this.auc[data.classIndex()][k] = Double.NaN;
        }

        this.avgAUC[data.classIndex()] = Double.NaN;
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
                Instances set = createInstance(i);
                Logistic model = new Logistic();
                model.buildClassifier(set);
                Evaluation evaluation = new Evaluation(set);
                evaluation.evaluateModel(model, set);
                for (int k = 0; k < data.numClasses(); k++) {
                    auc[i][k] = evaluation.areaUnderROC(k);
                    avgAUC[i] += auc[i][k];
                }
                avgAUC[i] /= data.numClasses();
            }
        }
    }

    /**
     * Returns the array of under ROC areas.
     *
     * @return the array of under ROC areas
     */
    public double[][] underROCValues() {
        return auc;
    }

    /**
     * Returns the array of under ROC average areas.
     *
     * @return the array of under ROC average areas
     */
    public double[] underROCAverageValues() {
        return avgAUC;
    }

    /**
     * Returns <tt>true</tt> if attribute is significant.
     *
     * @param attrIndex attribute index
     * @return <tt>true</tt> if attribute is significant
     */
    public boolean isSignificant(int attrIndex) {
        return avgAUC[attrIndex] > THRESHOLD_VALUE;
    }

    private Instances createInstance(int attrIndex) {
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
