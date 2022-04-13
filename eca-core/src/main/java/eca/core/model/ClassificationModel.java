/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core.model;

import eca.core.evaluation.Evaluation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Classification results model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationModel implements java.io.Serializable {

    /**
     * Classifier model
     */
    private AbstractClassifier classifier;

    /**
     * Training data
     */
    private Instances data;

    /**
     * Evaluation object
     */
    private Evaluation evaluation;

    /**
     * Maximum fraction digits
     */
    private Integer maximumFractionDigits;
}
