package eca.core;

import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.model.ClassificationModel;
import eca.data.file.resource.FileResource;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.File;

import static eca.TestHelperUtils.DATA_IRIS_XLS;
import static eca.TestHelperUtils.SEED;
import static eca.TestHelperUtils.getTargetPath;
import static eca.TestHelperUtils.loadInstances;
import static eca.core.evaluation.EvaluationService.evaluateModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for classifier model saving into file.
 *
 * @author Roman Batygin
 */
class ModelSerializationHelperTest {

    private static final int MAXIMUM_FRACTION_DIGITS = 4;
    private static final String CLASSIFIER_MODEL_FILE = "classifier-%s.model";

    private Instances instances;
    private File file;

    @BeforeEach
    void init() {
        instances = loadInstances(DATA_IRIS_XLS);
        file = new File(getTargetPath(), String.format(CLASSIFIER_MODEL_FILE, System.currentTimeMillis()));
    }

    @Test
    void testSaveClassifierModel() throws Exception {
        HeterogeneousClassifier classifier = new HeterogeneousClassifier();
        ClassifiersSet classifiers = new ClassifiersSet();
        classifiers.addClassifier(new CART());
        classifiers.addClassifier(new Logistic());
        classifiers.addClassifier(new C45());
        classifiers.addClassifier(new ID3());
        classifiers.addClassifier(new CHAID());
        classifiers.addClassifier(new KNearestNeighbours());
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        neuralNetwork.setSeed(SEED);
        classifiers.addClassifier(neuralNetwork);
        classifier.setClassifiersSet(classifiers);
        Evaluation evaluation = evaluateModel(classifier, instances, EvaluationMethod.TRAINING_DATA,0, 0, null);
        ClassificationModel expected =
                new ClassificationModel(classifier, instances, evaluation, MAXIMUM_FRACTION_DIGITS);
        ModelSerializationHelper.serialize(file, expected);
        //Compare models
        FileResource fileResource = new FileResource(file);
        ClassificationModel actual = ModelSerializationHelper.deserialize(fileResource, ClassificationModel.class);
        assertNotNull(actual);
        assertEquals(expected.getMaximumFractionDigits(), actual.getMaximumFractionDigits());
        assertEquals(expected.getData().relationName(),  actual.getData().relationName());
        assertEquals(expected.getEvaluation().pctCorrect(), actual.getEvaluation().pctCorrect());
        assertEquals(expected.getEvaluation().meanAbsoluteError(), actual.getEvaluation().meanAbsoluteError());
    }

    @AfterEach
    void delete() {
         FileUtils.deleteQuietly(file);
    }
}
