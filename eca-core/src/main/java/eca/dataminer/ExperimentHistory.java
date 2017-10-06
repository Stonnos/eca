/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.core.evaluation.EvaluationResults;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Experiment unit model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentHistory implements java.io.Serializable {

    private ArrayList<EvaluationResults> experiment;

    private Instances dataSet;

}
