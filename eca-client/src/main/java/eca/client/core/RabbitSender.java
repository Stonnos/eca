package eca.client.core;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import eca.client.converter.MessageConverter;
import eca.client.exception.EcaServiceException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Rabbit MQ sender.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class RabbitSender {

    private MessageConverter messageConverter;
    private ConnectionManager connectionManager;

    /**
     * Sends message to specified queue.
     *
     * @param queue      - queue name
     * @param request    - request message
     * @param properties - message properties
     * @param <T>        - message generic type
     */
    public <T> void sendMessage(String queue, T request, AMQP.BasicProperties properties) {
        try {
            Channel channel = connectionManager.getChannel();
            byte[] message = messageConverter.toMessage(request);
            channel.basicPublish(StringUtils.EMPTY, queue, properties, message);
        } catch (IOException | TimeoutException ex) {
            throw new EcaServiceException(ex.getMessage());
        }
    }
}
