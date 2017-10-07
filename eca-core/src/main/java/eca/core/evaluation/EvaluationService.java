package eca.core.evaluation;

import eca.core.EvaluationMethod;
import org.springframework.util.Assert;
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

    public static final int MINIMUM_NUMBER_OF_FOLDS = 2;
    public static final int MINIMUM_NUMBER_OF_TESTS = 1;

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
                Assert.isTrue(numFolds >= MINIMUM_NUMBER_OF_FOLDS,
                        String.format("Number of folds must be greater or equals to %d!", numFolds));
                Assert.isTrue(numTests >= MINIMUM_NUMBER_OF_TESTS,
                        String.format("Number of tests must be greater or equals to %d!", numTests));
                evaluation.kCrossValidateModel(AbstractClassifier.makeCopy(model), data,
                        numFolds, numTests, random);
                model.buildClassifier(data);
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid evaluation method!");
        }

        return evaluation;
    }
}
