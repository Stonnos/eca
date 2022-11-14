package eca.dataminer;

import eca.core.evaluation.EvaluationResults;

/**
 * Experiment history mode.
 *
 * @author Roman Batygin
 */
public enum ExperimentHistoryMode {

    /**
     * Full history
     */
    FULL {
        @Override
        public void visit(ExperimentHistoryModeVisitor visitor, EvaluationResults evaluationResults) {
            visitor.visitFull(evaluationResults);
        }
    },

    /**
     * Only best models
     */
    ONLY_BEST_MODELS {
        @Override
        public void visit(ExperimentHistoryModeVisitor visitor, EvaluationResults evaluationResults) {
            visitor.visitOnlyBestModels(evaluationResults);
        }
    };

    /**
     * Invokes visitor.
     *
     * @param visitor           - visitor interface
     * @param evaluationResults - evaluation results
     */
    public abstract void visit(ExperimentHistoryModeVisitor visitor, EvaluationResults evaluationResults);
}
