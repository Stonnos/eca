package eca.client;

import com.rabbitmq.client.ConnectionFactory;

/**
 * Eca client configuration class.
 *
 * @author Roman Batygin
 */
public class EcaClientConfiguration {

    private ConnectionFactory connectionFactory;

    private static EcaClientConfiguration instance;

    private EcaClientConfiguration() {
    }

    /**
     * Creates eca client configuration singleton instance.
     *
     * @return eca client configuration object
     */
    public static EcaClientConfiguration getInstance() {
        if (instance == null) {
            instance = new EcaClientConfiguration();
        }
        return instance;
    }

    /**
     * Creates connection factory.
     *
     * @return connection factory
     */
    public ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            connectionFactory.setPort(5672);
            connectionFactory.setUsername("guest");
            connectionFactory.setPassword("guest");
        }
        return connectionFactory;
    }
}
