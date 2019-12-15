package eca.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static eca.client.util.RabbitUtils.closeChannel;
import static eca.client.util.RabbitUtils.closeConnection;

/**
 * Rabbit MQ connection manager.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class ConnectionManager implements AutoCloseable {

    @Setter
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
    public void close() throws Exception {
        closeChannel(channel);
        closeConnection(connection);
    }
}
