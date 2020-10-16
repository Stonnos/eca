package eca.converters;

import eca.converters.model.ClassificationModel;
import eca.core.evaluation.Evaluation;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.ID3;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.File;

import static eca.TestHelperUtils.buildAndEvaluateModel;
import static eca.TestHelperUtils.getTargetPath;
import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for classifier model saving into file.
 *
 * @author Roman Batygin
 */
class ModelConverterTest {

    private static final String DATA_IRIS_XLS = "data/iris.xls";
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
        classifier.setClassifiersSet(classifiers);
        Evaluation evaluation = buildAndEvaluateModel(instances, classifier);
        ClassificationModel expected =
                new ClassificationModel(classifier, instances, evaluation, MAXIMUM_FRACTION_DIGITS,
                        classifier.getClass().getSimpleName());
        ModelConverter.saveModel(file, expected);
        //Compare models
        ClassificationModel actual = ModelConverter.loadModel(file, ClassificationModel.class);
        assertNotNull(actual);
        assertEquals(expected.getDetails(), actual.getDetails());
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
