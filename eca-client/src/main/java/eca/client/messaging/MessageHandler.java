package eca.client.messaging;

import com.rabbitmq.client.AMQP;

/**
 * Message handler interface.
 *
 * @param <T> - message generic type
 * @author Roman Batygin
 */
@FunctionalInterface
public interface MessageHandler<T> {

    /**
     * Handles message.
     *
     * @param message         - message object
     * @param basicProperties - message properties
     */
    void handle(T message, AMQP.BasicProperties basicProperties);
}
