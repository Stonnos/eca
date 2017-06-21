/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.beans;

import eca.core.evaluation.Evaluation;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
/**
 *
 * @author Рома
 */
public class ModelDescriptor implements java.io.Serializable {
    
    public AbstractClassifier classifier;
    public Instances data;
    public Evaluation evaluation;
    public String description;
    public int digits;
    
    public ModelDescriptor(AbstractClassifier classifier, Instances data,
            Evaluation evaluation, String description, int digits) {
        this.classifier = classifier;
        this.data = data;
        this.evaluation = evaluation;
        this.description = description;
        this.digits = digits;
    }
    
}
