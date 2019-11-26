package eca;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import eca.client.dto.EvaluationRequestDto;
import eca.client.dto.EvaluationResponse;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.FileResource;
import eca.trees.CART;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MimeTypeUtils;
import weka.core.Instances;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

/**
 * Main class.
 *
 * @author Roman Batygin
 */
@Slf4j
public class RabbitClientTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();
            channel.queueDeclare("evaluation-request-queue", true, false, false, null);

            String replyTo = channel.queueDeclare(StringUtils.EMPTY, false, true, true, null).getQueue();
            String correlationId = UUID.randomUUID().toString();
            System.out.println("Reply to : " + replyTo);
            System.out.println("Correlation id: " + correlationId);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println(" [x] Received message with correlation id: " + delivery.getProperties().getCorrelationId());
                byte[] body = delivery.getBody();
                EvaluationResponse evaluationResponse =
                        OBJECT_MAPPER.readValue(delivery.getBody(), 0, body.length, EvaluationResponse.class);
                System.out.println(evaluationResponse.getEvaluationResults().getEvaluation().toSummaryString());
                //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            channel.basicConsume(replyTo, false, deliverCallback, consumerTag -> {
            });


            //Sent message
            CART cart = new CART();
            FileDataLoader dataLoader = new FileDataLoader();
            dataLoader.setSource(new FileResource(new File("D:/IdeaProjects/eca/docs/data/iris.xls")));
            Instances instances = dataLoader.loadInstances();
            EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
            evaluationRequestDto.setClassifier(cart);
            evaluationRequestDto.setData(instances);
            evaluationRequestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
            evaluationRequestDto.setEvaluationOptionsMap(Collections.emptyMap());
            byte[] message = OBJECT_MAPPER.writeValueAsBytes(evaluationRequestDto);
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .correlationId(correlationId)
                    .replyTo(replyTo)
                    .contentEncoding(StandardCharsets.UTF_8.name())
                    .contentType(MimeTypeUtils.APPLICATION_JSON.toString()).build();
            channel.basicPublish(StringUtils.EMPTY, "evaluation-request-queue", properties, message);

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }
}
