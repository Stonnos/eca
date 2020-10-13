package eca.data.file;

import eca.EcaCoreTestConfiguration;
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
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setSource(new UrlResource(new URL(DATA_LOADING_HTTP_URL)));
        Instances instances = dataLoader.loadInstances();
        assertNotNull(instances);
        assertEquals(EXPECTED_RELATION_NAME_FROM_HTTP, instances.relationName());
        assertEquals(EXPECTED_NUM_ATTRIBUTES_FROM_HTTP, instances.numAttributes());
        assertEquals(EXPECTED_NUM_INSTANCES_FROM_HTTP, instances.numInstances());
        assertEquals(EXPECTED_NUM_CLASSES_FROM_HTTP, instances.numClasses());
    }

    @Test
    void testFtpLoading() throws Exception {
        FileDataLoader dataLoader = new FileDataLoader();
        String url = String.format(DATA_LOADING_FTP_URL, EcaCoreTestConfiguration.getFtpUsername(),
                EcaCoreTestConfiguration.getFtpPassword());
        dataLoader.setSource(new UrlResource(new URL(url)));
        Instances instances = dataLoader.loadInstances();
        assertNotNull(instances);
        assertEquals(EXPECTED_RELATION_NAME_FROM_FTP, instances.relationName());
        assertEquals(EXPECTED_NUM_ATTRIBUTES_FROM_FTP, instances.numAttributes());
        assertEquals(EXPECTED_NUM_INSTANCES_FROM_FTP, instances.numInstances());
        assertEquals(EXPECTED_NUM_CLASSES_FROM_FTP, instances.numClasses());
    }
}
