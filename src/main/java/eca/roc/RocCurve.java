/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.roc;

import eca.core.evaluation.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
/**
 * Class for providing ROC - curve results.
 * @author Рома
 */
public class RocCurve {
    
    private final Evaluation evaluation;

    /**
     * Creates <tt>ROCCurve</tt> object.
     * @param evaluation <tt>Evaluation</tt> object
     */
    public RocCurve(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    /**
     * Returns <tt>Evaluation</tt> object.
     * @return <tt>Evaluation</tt> object
     */
    public Evaluation evaluation() {
        return evaluation;
    }

    /**
     * Returns training data associated with <tt>Evaluation</tt> object.
     * @return training data associated with <tt>Evaluation</tt> object
     */
    public Instances data() {
        return evaluation().getHeader();
    }

    /**
     * Returns ROC - curve results for given class.
     * @param classIndex class index
     * @return ROC - curve results for given class
     */
    public Instances getROCCurve(int classIndex) {
        ThresholdCurve curve = new ThresholdCurve();
        return curve.getCurve(evaluation.predictions(), classIndex);
    }
     
}
