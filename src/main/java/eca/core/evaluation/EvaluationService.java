package eca.core.evaluation;

import eca.core.EvaluationMethod;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.Random;

/**
 * Classifier evaluation service.
 *
 * @author Roman Batygin
 */
public class EvaluationService {

    /**
     * Evaluates classifier model using specified evaluation method.
     *
     * @param model            {@link Classifier} model
     * @param data             {@link Instances} object
     * @param evaluationMethod {@link EvaluationMethod} type
     * @param numFolds         number of folds for k * V cross validation method
     * @param numTests         number of tests for k * V cross validation method
     * @return {@link Evaluation} object
     * @throws Exception
     */
    public static Evaluation evaluateModel(Classifier model,
                                           Instances data,
                                           EvaluationMethod evaluationMethod,
                                           int numFolds,
                                           int numTests,
                                           Random random) throws Exception {
        Evaluation evaluation = new Evaluation(data);

        switch (evaluationMethod) {

            case TRAINING_DATA: {
                model.buildClassifier(data);
                evaluation.evaluateModel(model, data);
                break;
            }

            case CROSS_VALIDATION: {
                evaluation.kCrossValidateModel(AbstractClassifier.makeCopy(model), data,
                        numFolds, numTests, random);
                model.buildClassifier(data);
                break;
            }

            default:
                throw new IllegalArgumentException("Incorrect evaluation method!");
        }

        return evaluation;
    }
}
