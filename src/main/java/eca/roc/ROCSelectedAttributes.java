/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.roc;

import weka.core.*;
import eca.regression.Logistic;
import java.util.*;
import eca.core.evaluation.Evaluation;
/**
 *
 * @author Рома
 */
public class ROCSelectedAttributes {
    
    private final Instances data;
    private final double[][] auc;
    private final double[] avgAUC;
    
    public ROCSelectedAttributes(Instances data) {
        this.data = data;
        auc = new double[data.numAttributes()][data.numClasses()];
        avgAUC = new double[data.numAttributes()];
        //-------------------------------------------
        for (int k = 0; k < data.numClasses(); k++) {
            auc[data.classIndex()][k] = Double.NaN;
        }
        avgAUC[data.classIndex()] = Double.NaN;
    }
    
    public Instances data() {
        return data;
    }
    
    public void calculate() throws Exception {
        for (int i = 0; i < data.numAttributes(); i++) {
            if (i != data.classIndex()) {
                Instances set = createInstance(i);
                Logistic model = new Logistic();
                model.buildClassifier(set);
                Evaluation e = new Evaluation(set);
                e.evaluateModel(model, set);
                for (int k = 0; k < data.numClasses(); k++) {
                    auc[i][k] = e.areaUnderROC(k);
                    avgAUC[i] += auc[i][k];
                }
                avgAUC[i] /= data.numClasses();
            }
        }
    }
    
    public double[][] underROCValues() {
        return auc;
    }
    
    public double[] underROCAverageValues() {
        return avgAUC;
    }
    
    public boolean isSignificant(int attrIndex) {
        return avgAUC[attrIndex] > 0.6;
    }

    
    private Instances createInstance(int attrIndex) {
        ArrayList<Attribute> attr = new ArrayList<>(2);
        attr.add(data.attribute(attrIndex).copy(data.attribute(attrIndex).name()));
        attr.add(data.classAttribute().copy(data.classAttribute().name()));
        Instances set = new Instances(data.relationName(), attr, data.numInstances());
        //------------------------------------------------
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
