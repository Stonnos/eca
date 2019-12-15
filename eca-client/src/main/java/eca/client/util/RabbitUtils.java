package eca.client.util;

import com.rabbitmq.client.AMQP;
import lombok.experimental.UtilityClass;
import org.springframework.util.MimeTypeUtils;

import java.nio.charset.StandardCharsets;

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
}
