/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core.evaluation;

import weka.classifiers.Classifier;

import java.io.Serializable;

/**
 * Classifier evaluation model.
 *
 * @author Roman Batygin
 */
public class EvaluationResults implements Serializable {

    private Classifier classifier;
    private Evaluation evaluation;

    public EvaluationResults(Classifier classifier, Evaluation evaluation) {
        this.classifier = classifier;
        this.evaluation = evaluation;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

}
