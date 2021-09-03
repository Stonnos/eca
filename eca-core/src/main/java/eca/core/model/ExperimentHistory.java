/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core.model;

import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.core.Instances;

import java.util.List;

/**
 * Experiment unit model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentHistory implements java.io.Serializable {

    /**
     * Experiment type
     */
    private String type;

    /**
     * Experiment history
     */
    private List<EvaluationResults> experiment;

    /**
     * Training data
     */
    private Instances dataSet;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;

    /**
     * Evaluation params
     */
    private EvaluationParams evaluationParams;

}
