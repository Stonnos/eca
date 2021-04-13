package eca.data.file;

import eca.config.EcaCoreTestConfiguration;
import eca.data.file.resource.UrlResource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.net.URL;

import static eca.AssertionUtils.assertInstances;
import static eca.TestHelperUtils.DATA_GLASS_ARFF;
import static eca.TestHelperUtils.DATA_IRIS_XLS;
import static eca.TestHelperUtils.loadInstances;

/**
 * Integration tests for data loading.
 *
 * @author Roman Batygin
 */
class FileDataLoaderIT {

    private static final String DATA_LOADING_HTTP_URL = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls";
    private static final String DATA_LOADING_FTP_URL = "ftp://%s:%s@localhost/glass.arff";

    @Test
    void testHttpLoading() {
        testDataLoading(DATA_LOADING_HTTP_URL, DATA_IRIS_XLS);
    }

    @Test
    void testFtpLoading() {
        String url = String.format(DATA_LOADING_FTP_URL, EcaCoreTestConfiguration.getFtpUsername(),
                EcaCoreTestConfiguration.getFtpPassword());
        testDataLoading(url, DATA_GLASS_ARFF);
    }

    @SneakyThrows
    private void testDataLoading(String url, String expectedInstancesFile) {
        Instances expected = loadInstances(expectedInstancesFile);
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setSource(new UrlResource(new URL(url)));
        Instances actual = dataLoader.loadInstances();
        assertInstances(expected, actual);
    }
}
