package eca.client.util;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.experimental.UtilityClass;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * Rabbit utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class RabbitUtils {

    /**
     * Builds message properties.
     *
     * @param replyTo       - reply to header
     * @param correlationId - correlation id header
     * @return amqp basic properties object
     */
    public static AMQP.BasicProperties buildMessageProperties(String replyTo, String correlationId) {
        return new AMQP.BasicProperties.Builder()
                .replyTo(replyTo)
                .correlationId(correlationId)
                .contentEncoding(StandardCharsets.UTF_8.name())
                .contentType(MimeTypeUtils.APPLICATION_JSON.toString()).build();
    }

    /**
     * Close rabbit connection.
     *
     * @param connection - connection object
     * @throws IOException in case of I/O error
     */
    public static void closeConnection(Connection connection) throws IOException {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Close channel.
     *
     * @param channel - channel object
     * @throws IOException      in case of I/O error
     * @throws TimeoutException in case og timeout error
     */
    public static void closeChannel(Channel channel) throws IOException, TimeoutException {
        if (channel != null) {
            channel.close();
        }
    }

    /**
     * Declares reply to queue.
     *
     * @param queue   - queue name
     * @param channel - channel object
     * @return generated queue name
     * @throws IOException in case of I/O error
     */
    public static String declareReplyToQueue(String queue, Channel channel) throws IOException {
        return channel.queueDeclare(queue, false, true, true, null).getQueue();
    }
}
