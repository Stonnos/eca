package eca.dataminer;

import eca.core.evaluation.EvaluationResults;

/**
 * Experiment history mode
 *
 * @author Roman Batygin
 */
public interface ExperimentHistoryModeVisitor {

    /**
     * Visit full mode.
     *
     * @param evaluationResults - evaluation results
     */
    void visitFull(EvaluationResults evaluationResults);

    /**
     * Visit only best models.
     *
     * @param evaluationResults - evaluation results
     */
    void visitOnlyBestModels(EvaluationResults evaluationResults);
}
