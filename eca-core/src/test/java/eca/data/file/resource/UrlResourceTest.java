package eca.data.file.resource;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Unit tests for {@code UrlResource} class.
 *
 * @author Roman Batygin
 */
class UrlResourceTest {

    public static final String MODEL_S3_URL =
            "http://localhost:8098/object-storage/eca-service/classifier-1e0e62b1-6d66-4ea6-92ca-f903c52b9229.model" +
                    "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minio%2F20220720%2Fus-east-1%2Fs3%2Faws4_request" +
                    "&X-Amz-Date=20220720T120738Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host" +
                    "&X-Amz-Signature=b828e7099ecdcca904db26e36a9de829aa3c7ac08e3fdd269cf75ed5dc21f38b";

    public static final String SIMPLE_MODEL_URL =
            "http://localhost:8098/object-storage/eca-service/classifier-1e0e62b1-6d66-4ea6-92ca-f903c52b9229.model";

    @Test
    void testGetExtensionForS3Url() throws MalformedURLException {
        UrlResource urlResource = new UrlResource(new URL(MODEL_S3_URL));
        assertThat(urlResource.getExtension()).isEqualTo("model");
    }

    @Test
    void testGetExtensionForSimpleUrl() throws MalformedURLException {
        UrlResource urlResource = new UrlResource(new URL(SIMPLE_MODEL_URL));
        assertThat(urlResource.getExtension()).isEqualTo("model");
    }
}
