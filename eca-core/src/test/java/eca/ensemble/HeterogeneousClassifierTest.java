package eca.ensemble;

import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationService;
import eca.ensemble.sampling.SamplingMethod;
import eca.metrics.KNearestNeighbours;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import static eca.TestHelperUtils.DATA_IRIS_XLS;
import static eca.TestHelperUtils.SEED;
import static eca.TestHelperUtils.loadInstances;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Unit tests for {@link HeterogeneousClassifier} class.
 *
 * @author Roman Batygin
 */
class HeterogeneousClassifierTest {

    private static final int NUM_ITERATIONS = 50;
    private static final int NUM_THREADS = 4;
    private static final int NUM_FOLDS = 10;
    private static final int NUM_TESTS = 1;

    private Instances instances;

    @BeforeEach
    void init() {
        instances = loadInstances(DATA_IRIS_XLS);
    }

    @Test
    void testConcurrentBuildModel() throws Exception {
        HeterogeneousClassifier notConcurrentClassifier = initializeClassifier();
        Evaluation notConcurrentEvaluation = EvaluationService.evaluateModel(notConcurrentClassifier, instances,
                EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS, NUM_TESTS, SEED);
        HeterogeneousClassifier concurrentClassifier = initializeClassifier();
        concurrentClassifier.setNumThreads(NUM_THREADS);
        Evaluation concurrentEvaluation = EvaluationService.evaluateModel(concurrentClassifier, instances,
                EvaluationMethod.CROSS_VALIDATION, NUM_FOLDS, NUM_TESTS, SEED);
        assertThat(notConcurrentEvaluation).isNotNull();
        assertThat(concurrentEvaluation).isNotNull();
        assertThat(notConcurrentEvaluation.pctCorrect()).isEqualTo(concurrentEvaluation.pctCorrect());
        assertThat(notConcurrentEvaluation.meanAbsoluteError()).isEqualTo(concurrentEvaluation.meanAbsoluteError());
        assertThat(notConcurrentEvaluation.maxAreaUnderROC()).isEqualTo(concurrentEvaluation.maxAreaUnderROC());
        assertThat(notConcurrentEvaluation.varianceError()).isEqualTo(concurrentEvaluation.varianceError());
    }

    private HeterogeneousClassifier initializeClassifier() {
        HeterogeneousClassifier classifier = new HeterogeneousClassifier();
        ClassifiersSet classifiers = new ClassifiersSet();
        classifiers.addClassifier(new CART());
        classifiers.addClassifier(new C45());
        classifiers.addClassifier(new KNearestNeighbours());
        classifiers.addClassifier(new Logistic());
        classifier.setClassifiersSet(classifiers);
        classifier.setSamplingMethod(SamplingMethod.BAGGING);
        classifier.setUseWeightedVotes(true);
        classifier.setUseRandomClassifier(true);
        classifier.setSeed(SEED);
        classifier.setNumIterations(NUM_ITERATIONS);
        return classifier;
    }
}
