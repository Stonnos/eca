/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.roc;

import weka.core.Instances;
import eca.core.evaluation.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
/**
 *
 * @author Рома
 */
public class ROCCurve {
    
    private final Evaluation evaluation;
    
    public ROCCurve(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
    
    public Evaluation evaluation() {
        return evaluation;
    }
    
    public Instances data() {
        return evaluation().getHeader();
    }
    
    public Instances getROCCurve(int classIndex) {
        ThresholdCurve curve = new ThresholdCurve();
        return curve.getCurve(evaluation.predictions(), classIndex);
    }
     
}
