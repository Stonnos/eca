package eca.data.file;

import eca.config.EcaCoreTestConfiguration;
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

    private static final String DATA_LOADING_HTTP_URL = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls";
    private static final String DATA_LOADING_FTP_URL = "ftp://%s:%s@localhost/glass.arff";
    private static final String EXPECTED_RELATION_NAME_FROM_HTTP = "IrisEX";
    private static final int EXPECTED_NUM_ATTRIBUTES_FROM_HTTP = 5;
    private static final int EXPECTED_NUM_INSTANCES_FROM_HTTP = 150;
    private static final int EXPECTED_NUM_CLASSES_FROM_HTTP = 3;
    private static final String EXPECTED_RELATION_NAME_FROM_FTP = "Glass";
    private static final int EXPECTED_NUM_ATTRIBUTES_FROM_FTP = 10;
    private static final int EXPECTED_NUM_INSTANCES_FROM_FTP = 214;
    private static final int EXPECTED_NUM_CLASSES_FROM_FTP = 7;

    @Test
    void testHttpLoading() throws Exception {
        testDataLoading(DATA_LOADING_HTTP_URL, EXPECTED_RELATION_NAME_FROM_HTTP, EXPECTED_NUM_INSTANCES_FROM_HTTP,
                EXPECTED_NUM_ATTRIBUTES_FROM_HTTP, EXPECTED_NUM_CLASSES_FROM_HTTP);
    }

    @Test
    void testFtpLoading() throws Exception {
        String url = String.format(DATA_LOADING_FTP_URL, EcaCoreTestConfiguration.getFtpUsername(),
                EcaCoreTestConfiguration.getFtpPassword());
        testDataLoading(url, EXPECTED_RELATION_NAME_FROM_FTP, EXPECTED_NUM_INSTANCES_FROM_FTP,
                EXPECTED_NUM_ATTRIBUTES_FROM_FTP, EXPECTED_NUM_CLASSES_FROM_FTP);
    }

    private void testDataLoading(String url,
                                 String expectedRelationName,
                                 int expectedNumInstances,
                                 int expectedNumAttributes,
                                 int expectedNumClasses) throws Exception {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setSource(new UrlResource(new URL(url)));
        Instances instances = dataLoader.loadInstances();
        assertNotNull(instances);
        assertEquals(expectedRelationName, instances.relationName());
        assertEquals(expectedNumAttributes, instances.numAttributes());
        assertEquals(expectedNumInstances, instances.numInstances());
        assertEquals(expectedNumClasses, instances.numClasses());
    }
}
