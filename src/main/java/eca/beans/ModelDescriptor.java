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
    
    private InputData inputData;
    private Evaluation evaluation;
    private String description;
    private int digits;
    
    public ModelDescriptor(InputData inputData,
            Evaluation evaluation, String description, int digits) {
        this.inputData = inputData;
        this.evaluation = evaluation;
        this.description = description;
        this.digits = digits;
    }

    public InputData getInputData() {
        return inputData;
    }

    public void setInputData(InputData inputData) {
        this.inputData = inputData;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }
}
