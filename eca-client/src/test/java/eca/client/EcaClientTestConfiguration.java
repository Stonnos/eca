package eca.client;

import com.rabbitmq.client.ConnectionFactory;
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
}
