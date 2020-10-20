package eca.ensemble.forests;

import eca.core.InstancesHandler;
import eca.trees.CART;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.List;
import java.util.stream.Collectors;

import static eca.TestHelperUtils.DATA_IRIS_XLS;
import static eca.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link RandomForests} class.
 *
 * @author Roman Batygin
 */
class RandomForestsTest {

    private RandomForests randomForests = new RandomForests();

    private Instances instances;

    @BeforeEach
    void init() {
        instances = loadInstances(DATA_IRIS_XLS);
        randomForests.setDecisionTreeType(DecisionTreeType.CART);
    }

    @Test
    void testBuildModel() throws Exception {
        randomForests.buildClassifier(instances);
        List<Classifier> classifiers = randomForests.getStructure();
        assertNotNull(classifiers);
        assertEquals(randomForests.getNumIterations(), classifiers.size());
        classifiers.forEach(classifier -> assertThat(classifier).isInstanceOf(CART.class));
        List<InstancesHandler> instancesHandlers = classifiers.stream()
                .filter(InstancesHandler.class::isInstance)
                .map(InstancesHandler.class::cast)
                .collect(Collectors.toList());
        assertThat(classifiers).hasSameSizeAs(instancesHandlers);
        //Verify that all bootstrap sample has same size as initial training data
        instancesHandlers.forEach(
                instancesHandler -> assertEquals(instances.numInstances(), instancesHandler.getData().numInstances()));
    }
}
