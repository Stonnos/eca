package eca.dataminer;

import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import static eca.TestHelperUtils.SEED;
import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link AutomatedHeterogeneousEnsemble} class.
 *
 * @author Roman Batygin
 */
class AutomatedHeterogeneousEnsembleTest {

    private static final String DATA_IRIS_XLS = "data/iris.xls";

    private AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble;

    @BeforeEach
    void init() {
        Instances instances = loadInstances(DATA_IRIS_XLS);
        HeterogeneousClassifier classifier = new HeterogeneousClassifier();
        ClassifiersSet classifiers = new ClassifiersSet();
        classifiers.addClassifier(new CART());
        classifiers.addClassifier(new C45());
        classifiers.addClassifier(new ID3());
        classifier.setClassifiersSet(classifiers);
        automatedHeterogeneousEnsemble = new AutomatedHeterogeneousEnsemble(classifier, instances);
        automatedHeterogeneousEnsemble.setSeed(SEED);
    }

    @Test
    void testBuildModels() throws Exception {
        automatedHeterogeneousEnsemble.beginExperiment();
        assertEquals(112, automatedHeterogeneousEnsemble.getHistory().size());
    }
}
