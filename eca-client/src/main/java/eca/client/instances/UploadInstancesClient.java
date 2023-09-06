package eca.client.instances;

import com.fasterxml.jackson.databind.ObjectMapper;
import eca.client.dto.UploadInstancesResponseDto;
import eca.client.exception.EcaServiceException;
import eca.client.exception.WebClientErrorException;
import eca.client.exception.WebClientResponseException;
import eca.client.http.HttpRequestExecutor;
import eca.client.http.Oauth2TokenInterceptor;
import eca.data.file.model.InstancesModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Upload instances client.
 *
 * @author Roman Batygin
 */
@Slf4j
public class UploadInstancesClient {

    public static final String INSTANCES_FILE_PARAM = "instancesFile";

    @Getter
    @Setter
    private String dataLoaderUrl;
    @Getter
    private final Oauth2TokenProvider oauth2TokenProvider = new Oauth2TokenProvider();

    private final HttpRequestExecutor httpRequestExecutor = new HttpRequestExecutor();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Default constructor.
     */
    public UploadInstancesClient() {
        httpRequestExecutor.addInterceptor(new Oauth2TokenInterceptor(oauth2TokenProvider));
    }

    /**
     * Uploads instances model to central data storage.
     *
     * @param instancesModel - instances model
     * @return data uuid
     */
    public String uploadInstances(InstancesModel instancesModel) {
        try {
            log.info("Starting to upload instances [{}] to central data storage", instancesModel.getRelationName());
            HttpPost httpUriRequest = new HttpPost();
            httpUriRequest.setURI(new URI(dataLoaderUrl));
            byte[] bytes = objectMapper.writeValueAsBytes(instancesModel);
            String fileName = String.format("%s.json", instancesModel.getRelationName());
            HttpEntity httpEntity = MultipartEntityBuilder.create()
                    .addBinaryBody(INSTANCES_FILE_PARAM, bytes, ContentType.MULTIPART_FORM_DATA, fileName)
                    .build();
            httpUriRequest.setEntity(httpEntity);
            UploadInstancesResponseDto uploadInstancesResponseDto =
                    httpRequestExecutor.execute(httpUriRequest, UploadInstancesResponseDto.class);
            log.info("Instances [{}] has been uploaded with uuid [{}]", instancesModel.getRelationName(),
                    uploadInstancesResponseDto.getUuid());
            return uploadInstancesResponseDto.getUuid();
        } catch (IOException | URISyntaxException ex) {
            throw new IllegalStateException(ex);
        } catch (WebClientResponseException ex) {
            log.error(
                    "Web client response error code [{}] while upload instances [{}] to central data storage. Error response [{}]",
                    ex.getStatusCode(), instancesModel.getRelationName(), ex.getResponseBody());
            throw new EcaServiceException("Instances client error");
        } catch (WebClientErrorException ex) {
            log.error("Web client error while upload instances [{}] to central data storage: {}",
                    instancesModel.getRelationName(), ex.getMessage());
            throw new EcaServiceException("Instances client error");
        }
    }
}
