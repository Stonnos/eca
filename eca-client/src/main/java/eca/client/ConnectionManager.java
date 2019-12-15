package eca.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Rabbit MQ connection manager.
 *
 * @author Roman Batygin
 */
public class ConnectionManager {

    /**
     * Retry to connect if connection attempt failed?
     */
    @Getter
    @Setter
    private boolean retryToConnect;

    @Getter
    @Setter
    private long connectionAttemptIntervalMillis = 10000L;

    @Getter
    @Setter
    private ConnectionFactory connectionFactory;


    private Channel channel;
    private Connection connection;

    /**
     * Gets channel associated with current connection.
     *
     * @return channel object
     * @throws IOException      in case of I/O error
     * @throws TimeoutException in case of timeout
     */
    public synchronized Channel getChannel() throws IOException, TimeoutException {
        if (connection == null) {
            createConnection();
        }
        if (channel == null) {
            channel = connection.createChannel();
        }
        return channel;
    }

    private void createConnection() throws IOException, TimeoutException {
        if (connection == null) {
            if (retryToConnect) {
                tryToConnect();
            } else {
                connection = connectionFactory.newConnection();
            }
        }
    }

    private void tryToConnect() {
        do {
            try {
                connection = connectionFactory.newConnection();
            } catch (Exception ex) {
                waitForNextAttempt();
            }
        } while (connection == null);
    }

    private void waitForNextAttempt() {
        try {
            wait(connectionAttemptIntervalMillis);
        } catch (InterruptedException e) {
            //ignored
        }
    }
}
