package eca.gui.actions;

import eca.client.EcaServiceClient;
import eca.core.evaluation.EvaluationResults;
import weka.core.Instances;

/**
 * Implementd optimal classifier request sender.
 *
 * @author Roman Batygin
 */
public class OptimalClassifierRequestSender implements CallbackAction {

    private EcaServiceClient ecaServiceClient;
    private Instances data;
    private EvaluationResults evaluationResults;

    /**
     * Constructor with params
     *
     * @param ecaServiceClient - eca service client
     * @param data             - training data
     */
    public OptimalClassifierRequestSender(EcaServiceClient ecaServiceClient, Instances data) {
        this.ecaServiceClient = ecaServiceClient;
        this.data = data;
    }

    @Override
    public void apply() throws Exception {
        evaluationResults = ecaServiceClient.performRequest(data);
    }

    /**
     * Gets evaluation results.
     *
     * @return evaluation results
     */
    public EvaluationResults getEvaluationResults() {
        return evaluationResults;
    }
}
