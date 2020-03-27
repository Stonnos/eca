package eca.client.core;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eca.client.util.RabbitUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Rabbit MQ connection manager.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class ConnectionManager implements AutoCloseable {

    @Getter
    private final ConnectionFactory connectionFactory;

    private Channel channel;
    private Connection connection;

    /**
     * Gets channel. Also opens connection if closed.
     *
     * @return channel object
     * @throws IOException      in case of I/O error
     * @throws TimeoutException in case of timeout
     */
    public synchronized Channel getChannel() throws IOException, TimeoutException {
        if (connection == null) {
            connection = connectionFactory.newConnection();
        }
        if (channel == null) {
            channel = connection.createChannel();
        }
        return channel;
    }

    @Override
    public void close() throws IOException, TimeoutException {
        closeChannel();
        closeConnection();
    }

    private void closeChannel() throws IOException, TimeoutException {
        RabbitUtils.closeChannel(channel);
        channel = null;
    }

    private void closeConnection() throws IOException {
        RabbitUtils.closeConnection(connection);
        connection = null;
    }
}
