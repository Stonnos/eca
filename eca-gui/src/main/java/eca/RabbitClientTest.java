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
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.sampling.SamplingMethod;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import weka.core.Instances;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

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
            Connection conn = conn(factory, 1);
            Channel channel = conn.createChannel();
            //channel.queueDeclare("evaluation-request-queue", true, false, false, null);

            String replyTo = channel.queueDeclare(StringUtils.EMPTY, false, true, true, null).getQueue();
            String correlationId = UUID.randomUUID().toString();
            System.out.println("Reply to : " + replyTo);
            System.out.println("Correlation id: " + correlationId);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println(" [x] Received message with correlation id: " + delivery.getProperties().getCorrelationId());
                byte[] body = delivery.getBody();
                EvaluationResponse evaluationResponse =
                        OBJECT_MAPPER.readValue(delivery.getBody(), 0, body.length, EvaluationResponse.class);
                log.info(evaluationResponse.getEvaluationResults().getEvaluation().toSummaryString());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            channel.basicConsume(replyTo, false, deliverCallback, consumerTag -> {
            });


            //Sent message
            HeterogeneousClassifier cart = new HeterogeneousClassifier();
            cart.setNumIterations(10);
            cart.setSamplingMethod(SamplingMethod.BAGGING);
            cart.setClassifiersSet(new ClassifiersSet());
            cart.getClassifiersSet().addClassifier(new CART());
            cart.getClassifiersSet().addClassifier(new C45());
            cart.getClassifiersSet().addClassifier(new Logistic());
          //  cart.getClassifiersSet().addClassifier(new NeuralNetwork());
            FileDataLoader dataLoader = new FileDataLoader();
            dataLoader.setSource(new FileResource(new File("D:/IdeaProjects/eca/docs/data/ionosphere.arff")));
            Instances instances = dataLoader.loadInstances();
            EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
            evaluationRequestDto.setClassifier(cart);
            evaluationRequestDto.setData(instances);
            evaluationRequestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
            byte[] message = OBJECT_MAPPER.writeValueAsBytes(evaluationRequestDto);
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .correlationId(correlationId)
                    .replyTo(replyTo)
                    .contentEncoding(StandardCharsets.UTF_8.name())
                    .contentType("application/json").build();
            channel.basicPublish(StringUtils.EMPTY, "evaluation-request-queue", properties, message);

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
       //getConn(new ConnectionFactory());

    }


    private static Object monitor = new Object();

    private static Connection conn(ConnectionFactory factory, int i) throws IOException, TimeoutException {
        Connection connection = null;
        synchronized (monitor) {
            do {
                try {
                    log.info("Try connect " + i);
                    connection = factory.newConnection();
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    //waitForNextAttempt();
                }
            } while (connection == null);
        }
        return connection;
    }

    private static void waitForNextAttempt() {
        try {
            monitor.wait(5000L);
        } catch (InterruptedException e) {
            //ignore
        }
    }

    private static void getConn(ConnectionFactory connectionFactory) {
        /*CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
           try {
               conn(connectionFactory, 1);
           } catch (Exception ex) {
               log.error(ex.getMessage());
           }
        }, Executors.newSingleThreadExecutor());
        future.thenAccept(result -> log.info("Done!"));
        log.info("dede");
        try {
            Thread.sleep(10000L);
            log.info("cancel");
            futureTask.cancel(true);
            Thread.sleep(50000000L);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }*/
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Connection connection = null;
                synchronized (monitor) {
                    do {
                        try {
                            log.info("Try connect ");
                            connection = connectionFactory.newConnection();
                        } catch (Exception ex) {
                            log.error(ex.getMessage());
                            waitForNextAttempt();
                        }
                    } while (!isCancelled() && connection == null);
                }
                return null;
            }
        };
        worker.execute();
        log.info("dede");
        try {
            Thread.sleep(10000L);
            log.info("cancel");
            worker.cancel(true);
            Thread.sleep(50000000L);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
