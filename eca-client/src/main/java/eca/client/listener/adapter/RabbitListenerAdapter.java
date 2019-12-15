package eca.client.listener.adapter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import eca.client.converter.MessageConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Rabbit listener adapter.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
@RequiredArgsConstructor
public class RabbitListenerAdapter {

    private final MessageConverter messageConverter;

    /**
     * Setup consumer for outbound messages.
     *
     * @param channel      - channel object
     * @param queueName    - queue name to listen
     * @param callback     - callback invokes when message is delivered and converted
     * @param messageClazz - message class
     * @param <T>          - message generic type
     * @throws IOException in case of I/O error
     */
    public <T> void basicConsume(Channel channel, String queueName, Consumer<T> callback, Class<T> messageClazz)
            throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            T message = messageConverter.fromMessage(delivery.getBody(), messageClazz);
            callback.accept(message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
        });
    }
}
