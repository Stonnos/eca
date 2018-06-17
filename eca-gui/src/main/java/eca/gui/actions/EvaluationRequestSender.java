package eca.gui.actions;

import eca.client.EcaServiceClient;
import eca.core.evaluation.EvaluationResults;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Evaluation request sender.
 *
 * @author Roman Batygin
 */
public class EvaluationRequestSender implements CallbackAction {

    private EcaServiceClient ecaServiceClient;
    private AbstractClassifier classifier;
    private Instances data;
    private EvaluationResults evaluationResults;

    /**
     * Constructor with params.
     *
     * @param ecaServiceClient - eca service client
     * @param classifier       - classifier object
     * @param data             - training data
     */
    public EvaluationRequestSender(EcaServiceClient ecaServiceClient, AbstractClassifier classifier, Instances data) {
        this.ecaServiceClient = ecaServiceClient;
        this.classifier = classifier;
        this.data = data;
    }

    @Override
    public void apply() throws Exception {
        evaluationResults = ecaServiceClient.performRequest(classifier, data);
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
