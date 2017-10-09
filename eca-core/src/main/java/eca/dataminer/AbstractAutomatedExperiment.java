package eca.dataminer;

import eca.core.evaluation.EvaluationResults;

/**
 * Iterative experiment based on state machine model.
 *
 * @author Roman Batygin
 */
public abstract class AbstractAutomatedExperiment implements IterativeExperiment {

    /**
     * Performs next state.
     *
     * @return {@link EvaluationResults} object
     * @throws Exception
     */
    public abstract EvaluationResults nextState() throws Exception;

    /**
     * Calculates next evaluation results.
     *
     * @return {@link EvaluationResults} object
     * @throws Exception
     */
    public EvaluationResults searchNext() throws Exception {
        EvaluationResults object = null;
        while (object == null) {
            object = nextState();
        }
        return object;
    }
}
