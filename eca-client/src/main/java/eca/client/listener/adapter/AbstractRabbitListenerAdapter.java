package eca.client.listener.adapter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import eca.client.converter.MessageConverter;
import eca.client.messaging.MessageHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;

/**
 * Rabbit listener adapter.
 *
 * @param <T> - message generic type
 * @author Roman Batygin
 */
@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractRabbitListenerAdapter<T> {

    private final Class<T> messageClass;
    private final MessageConverter messageConverter;
    private final MessageHandler<T> messageHandler;

    /**
     * Setup consumer for outbound messages.
     *
     * @param channel   - channel object
     * @param queueName - queue name to listen
     * @throws IOException in case of I/O error
     */
    public void basicConsume(Channel channel, String queueName) throws IOException {
        DeliverCallback deliverCallback = createDeliverCallback(channel);
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
        });
    }

    private DeliverCallback createDeliverCallback(Channel channel) {
        return (consumerTag, delivery) -> {
            T message = messageConverter.fromMessage(delivery.getBody(), messageClass);
            messageHandler.handle(message, delivery.getProperties());
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
    }
}
