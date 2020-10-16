package eca.data.file;

import eca.config.EcaCoreTestConfiguration;
import eca.data.file.resource.UrlResource;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.net.URL;

import static eca.AssertionUtils.assertInstances;
import static eca.TestHelperUtils.loadInstances;

/**
 * Integration tests for data loading.
 *
 * @author Roman Batygin
 */
class FileDataLoaderIT {

    private static final String DATA_LOADING_HTTP_URL = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls";
    private static final String DATA_LOADING_FTP_URL = "ftp://%s:%s@localhost/glass.arff";
    private static final String DATA_GLASS_ARFF = "data/glass.arff";
    private static final String DATA_IRIS_XLS = "data/iris.xls";

    @Test
    void testHttpLoading() throws Exception {
        testDataLoading(DATA_LOADING_HTTP_URL, DATA_IRIS_XLS);
    }

    @Test
    void testFtpLoading() throws Exception {
        String url = String.format(DATA_LOADING_FTP_URL, EcaCoreTestConfiguration.getFtpUsername(),
                EcaCoreTestConfiguration.getFtpPassword());
        testDataLoading(url, DATA_GLASS_ARFF);
    }

    private void testDataLoading(String url,
                                 String expectedInstancesFile) throws Exception {
        Instances expected = loadInstances(expectedInstancesFile);
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setSource(new UrlResource(new URL(url)));
        Instances actual = dataLoader.loadInstances();
        assertInstances(expected, actual);
    }
}
