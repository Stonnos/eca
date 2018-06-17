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
public class EvaluationRequestSender extends AbstractCallback<EvaluationResults> {

    private EcaServiceClient ecaServiceClient;
    private AbstractClassifier classifier;
    private Instances data;

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
        result = ecaServiceClient.performRequest(classifier, data);
    }
}
