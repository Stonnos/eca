package eca.config;

import com.rabbitmq.client.ConnectionFactory;
import eca.client.converter.JsonMessageConverter;
import eca.client.converter.MessageConverter;
import eca.client.core.ConnectionManager;
import eca.client.core.RabbitClient;
import eca.client.core.RabbitSender;
import eca.client.listener.MessageListenerContainer;
import lombok.Getter;

/**
 * Rabbit configuration.
 *
 * @author Roman Batygin
 */
public class RabbitConfiguration {

    private static RabbitConfiguration rabbitConfiguration;

    private RabbitClient rabbitClient;
    private MessageListenerContainer messageListenerContainer;

    @Getter
    private final MessageConverter messageConverter = new JsonMessageConverter();

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
    public RabbitClient configureRabbitClient(EcaServiceConfig ecaServiceConfig) {
        if (rabbitClient == null) {
            RabbitSender rabbitSender = new RabbitSender();
            rabbitSender.setMessageConverter(messageConverter);
            rabbitClient = new RabbitClient(rabbitSender);
        }
        ConnectionManager connectionManager = connectionManager(ecaServiceConfig);
        rabbitClient.getRabbitSender().setConnectionManager(connectionManager);
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
            messageListenerContainer = new MessageListenerContainer();
        }
        ConnectionFactory connectionFactory = connectionFactory(ecaServiceConfig);
        messageListenerContainer.setConnectionFactory(connectionFactory);
        return messageListenerContainer;
    }

    private ConnectionFactory connectionFactory(EcaServiceConfig ecaServiceConfig) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ecaServiceConfig.getHost());
        connectionFactory.setPort(ecaServiceConfig.getPort());
        connectionFactory.setUsername(ecaServiceConfig.getUsername());
        connectionFactory.setPassword(ecaServiceConfig.getPassword());
        return connectionFactory;
    }

    private ConnectionManager connectionManager(EcaServiceConfig ecaServiceConfig) {
        ConnectionFactory connectionFactory = connectionFactory(ecaServiceConfig);
        return new ConnectionManager(connectionFactory);
    }
}
