package eca.dataminer;

import eca.ensemble.ClassifiersSet;
import eca.ensemble.StackingClassifier;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.ID3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import static eca.TestHelperUtils.SEED;
import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link AutomatedStacking} class.
 *
 * @author Roman Batygin
 */
class AutomatedStackingTest {

    private static final String DATA_IRIS_XLS = "data/iris.xls";

    private AutomatedStacking automatedStacking;

    @BeforeEach
    void init() {
        Instances instances = loadInstances(DATA_IRIS_XLS);
        StackingClassifier classifier = new StackingClassifier();
        ClassifiersSet classifiers = new ClassifiersSet();
        classifiers.addClassifier(new CART());
        classifiers.addClassifier(new C45());
        classifiers.addClassifier(new ID3());
        classifier.setClassifiers(classifiers);
        automatedStacking = new AutomatedStacking(classifier, instances);
        automatedStacking.setSeed(SEED);
    }

    @Test
    void testBuildModels() throws Exception {
        automatedStacking.beginExperiment();
        assertEquals(21, automatedStacking.getHistory().size());
    }
}
