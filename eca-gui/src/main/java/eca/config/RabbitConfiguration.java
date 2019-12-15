package eca.config;

import com.rabbitmq.client.ConnectionFactory;
import eca.client.ConnectionManager;
import eca.client.RabbitClient;
import eca.client.RabbitClientImpl;
import eca.client.RabbitSender;
import eca.client.converter.JsonMessageConverter;
import eca.client.converter.MessageConverter;
import eca.client.listener.MessageListenerContainer;
import eca.client.listener.adapter.RabbitListenerAdapter;

/**
 * Rabbit configuration.
 *
 * @author Roman Batygin
 */
public class RabbitConfiguration {

    private static RabbitConfiguration rabbitConfiguration;

    private final MessageConverter messageConverter = new JsonMessageConverter();

    private RabbitClientImpl rabbitClient;
    private MessageListenerContainer messageListenerContainer;

    private RabbitConfiguration() {
    }

    /**
     * Gets rabbit configuration singleton instances.
     *
     * @return rabbit configuration singleton instances.
     */
    public static RabbitConfiguration getRabbitConfiguration() {
        if (rabbitConfiguration == null) {
            rabbitConfiguration = new RabbitConfiguration();
        }
        return rabbitConfiguration;
    }

    /**
     * Configure rabbit client.
     *
     * @param ecaServiceConfig - eca - service config
     * @return rabbit client
     */
    public RabbitClientImpl configureRabbitClient(EcaServiceConfig ecaServiceConfig) {
        if (rabbitClient == null) {
            RabbitSender rabbitSender = rabbitSender(ecaServiceConfig);
            rabbitClient = new RabbitClientImpl(rabbitSender);
        }
        return rabbitClient;
    }

    /**
     * Configure message listener container.
     *
     * @param ecaServiceConfig - eca - service config
     * @return message listener container
     */
    public MessageListenerContainer configureMessageListenerContainer(EcaServiceConfig ecaServiceConfig) {
        if (messageListenerContainer == null) {
            ConnectionFactory connectionFactory = connectionFactory(ecaServiceConfig);
            messageListenerContainer = new MessageListenerContainer(connectionFactory, rabbitListenerAdapter());
        }
        return messageListenerContainer;
    }

    private ConnectionFactory connectionFactory(EcaServiceConfig ecaServiceConfig) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        return connectionFactory;
    }

    private RabbitListenerAdapter rabbitListenerAdapter() {
        return new RabbitListenerAdapter(messageConverter);
    }

    private ConnectionManager connectionManager(EcaServiceConfig ecaServiceConfig) {
        ConnectionFactory connectionFactory = connectionFactory(ecaServiceConfig);
        return new ConnectionManager(connectionFactory);
    }

    private RabbitSender rabbitSender(EcaServiceConfig ecaServiceConfig) {
        ConnectionManager connectionManager = connectionManager(ecaServiceConfig);
        return new RabbitSender(messageConverter, connectionManager);
    }
}
