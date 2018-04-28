/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.roc;

import eca.core.InstancesHandler;
import eca.core.evaluation.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Optional;

/**
 * Class for providing ROC - curve results.
 *
 * @author Roman Batygin
 */
public class RocCurve implements InstancesHandler {

    public static final int SPECIFICITY_INDEX = 4;
    public static final int SENSITIVITY_INDEX = 5;
    public static final int THRESHOLD_INDEX = 12;

    private final ThresholdCurve curve = new ThresholdCurve();
    private final Evaluation evaluation;

    /**
     * Creates <tt>ROCCurve</tt> object.
     *
     * @param evaluation <tt>Evaluation</tt> object
     */
    public RocCurve(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    /**
     * Returns <tt>Evaluation</tt> object.
     *
     * @return <tt>Evaluation</tt> object
     */
    public Evaluation evaluation() {
        return evaluation;
    }

    /**
     * Returns training data associated with <tt>Evaluation</tt> object.
     *
     * @return training data associated with <tt>Evaluation</tt> object
     */
    @Override
    public Instances getData() {
        return evaluation().getData();
    }

    /**
     * Returns ROC - curve results for given class.
     *
     * @param classIndex class index
     * @return ROC - curve results for given class
     */
    public Instances getROCCurve(int classIndex) {
        return curve.getCurve(evaluation.predictions(), classIndex);
    }

    /**
     * Finds optimal threshold value for specified class.
     *
     * @param classIndex - class index
     * @return optimal threshold value
     */
    public ThresholdModel findOptimalThreshold(int classIndex) {
        return getROCCurve(classIndex).stream().max(((o1, o2) -> {
            double x = 1.0 - o1.value(SPECIFICITY_INDEX) + o1.value(SENSITIVITY_INDEX);
            double y = 1.0 - o2.value(SPECIFICITY_INDEX) + o2.value(SENSITIVITY_INDEX);
            return Double.compare(x, y);
        })).map(instance -> {
            ThresholdModel thresholdModel = new ThresholdModel();
            thresholdModel.setSpecificity(instance.value(SPECIFICITY_INDEX));
            thresholdModel.setSensitivity(instance.value(SENSITIVITY_INDEX));
            thresholdModel.setThresholdValue(instance.value(THRESHOLD_INDEX));
            return thresholdModel;
        }).orElse(null);
    }
}
