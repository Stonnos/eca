package eca.data.file;

import eca.data.file.resource.UrlResource;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for data loading.
 *
 * @author Roman Batygin
 */
class FileDataLoaderIT {

    private static final String DATA_LOADING_URL = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls";
    private static final String EXPECTED_RELATION_NAME = "IrisEX";
    private static final int EXPECTED_NUM_ATTRIBUTES = 5;
    private static final int EXPECTED_NUM_INSTANCES = 150;
    private static final int EXPECTED_NUM_CLASSES = 3;

    @Test
    void testHttpLoading() throws Exception {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setSource(new UrlResource(new URL(DATA_LOADING_URL)));
        Instances instances = dataLoader.loadInstances();
        assertNotNull(instances);
        assertEquals(EXPECTED_RELATION_NAME, instances.relationName());
        assertEquals(EXPECTED_NUM_ATTRIBUTES, instances.numAttributes());
        assertEquals(EXPECTED_NUM_INSTANCES, instances.numInstances());
        assertEquals(EXPECTED_NUM_CLASSES, instances.numClasses());
    }
}
