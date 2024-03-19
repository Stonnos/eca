package eca.client;

import com.rabbitmq.client.ConnectionFactory;
import eca.client.instances.UploadInstancesCacheService;
import eca.client.instances.UploadInstancesClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * Eca client configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
public class EcaClientTestConfiguration {

    private static final String APPLICATION_TEST_PROPERTIES = "application-test.properties";
    private static final String RABBIT_PASSWORD = "rabbit.password";
    private static final String RABBIT_USERNAME = "rabbit.username";
    private static final String RABBIT_PORT = "rabbit.port";
    private static final String RABBIT_HOST = "rabbit.host";
    private static final String EVALUATION_REQUEST_QUEUE = "queue.evaluation.request";
    private static final String EXPERIMENT_REQUEST_QUEUE = "queue.experiment.request";
    private static final String DATA_LOADER_URL = "data.loader.url";
    private static final String TOKEN_URL = "token.url";
    private static final String CLIENT_ID = "client-id";
    private static final String CLIENT_SECRET = "client-secret";

    private ConnectionFactory connectionFactory;

    private static EcaClientTestConfiguration instance;

    private static Properties properties = new Properties();

    static {
        try {
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(APPLICATION_TEST_PROPERTIES));
        } catch (IOException ex) {
            log.error("There wa an error while load configs: [{}]", ex.getMessage());
        }
    }

    private EcaClientTestConfiguration() {
    }

    /**
     * Creates eca client configuration singleton instance.
     *
     * @return eca client configuration object
     */
    public static EcaClientTestConfiguration getInstance() {
        if (instance == null) {
            instance = new EcaClientTestConfiguration();
        }
        return instance;
    }

    /**
     * Gets evaluation request queue name.
     *
     * @return evaluation request queue name
     */
    public static String getEvaluationRequestQueue() {
        return properties.getProperty(EVALUATION_REQUEST_QUEUE);
    }

    /**
     * Gets experiment request queue name.
     *
     * @return experiment request queue name
     */
    public static String getExperimentRequestQueue() {
        return properties.getProperty(EXPERIMENT_REQUEST_QUEUE);
    }

    /**
     * Creates connection factory.
     *
     * @return connection factory
     */
    public ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(properties.getProperty(RABBIT_HOST));
            connectionFactory.setPort(Integer.parseInt(properties.getProperty(RABBIT_PORT)));
            connectionFactory.setUsername(properties.getProperty(RABBIT_USERNAME));
            connectionFactory.setPassword(properties.getProperty(RABBIT_PASSWORD));
        }
        return connectionFactory;
    }

    /**
     * Creates upload instances cache service.
     *
     * @return upload instances cache service
     */
    public UploadInstancesCacheService createUploadInstancesCacheService() {
        UploadInstancesCacheService uploadInstancesCacheService = new UploadInstancesCacheService();
        uploadInstancesCacheService.getUploadInstancesClient().setDataLoaderUrl(
                properties.getProperty(DATA_LOADER_URL));
        uploadInstancesCacheService.getUploadInstancesClient().getOauth2TokenProvider().setTokenUrl(
                properties.getProperty(TOKEN_URL));
        uploadInstancesCacheService.getUploadInstancesClient().getOauth2TokenProvider().setClientId(
                properties.getProperty(CLIENT_ID));
        uploadInstancesCacheService.getUploadInstancesClient().getOauth2TokenProvider().setClientSecret(
                properties.getProperty(CLIENT_SECRET));
        return uploadInstancesCacheService;
    }
}
