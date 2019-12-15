package eca.client.listener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eca.client.converter.JsonMessageConverter;
import eca.client.dto.EcaResponse;
import eca.client.dto.EvaluationResponse;
import eca.client.listener.adapter.RabbitListenerAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static eca.client.util.RabbitUtils.closeChannel;
import static eca.client.util.RabbitUtils.closeConnection;
import static eca.client.util.RabbitUtils.declareReplyToQueue;

/**
 * Message listener container.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class MessageListenerContainer {

    @Getter
    private final ConnectionFactory connectionFactory;

    @Getter
    private final RabbitListenerAdapter rabbitListenerAdapter;

    @Getter
    @Setter
    private long connectionAttemptIntervalMillis = 5000L;

    @Getter
    @Setter
    private Consumer<EcaResponse> experimentResponseConsumer;

    @Getter
    @Setter
    private Consumer<EvaluationResponse> evaluationResultsConsumer;

    private Connection connection;

    private Channel channel;

    private ExecutorService executorService;

    private FutureTask<Void> futureTask;

    @Getter
    private String evaluationResultsQueue;

    /**
     * Starts message listener container.
     */
    public void start() {
        executorService = Executors.newSingleThreadExecutor();
        Callable<Void> callable = () -> {
            doConnect();
            setupConsumers();
            return null;
        };
        futureTask = new FutureTask<>(callable);
        executorService.execute(futureTask);
    }

    /**
     * Stop message listener container.
     */
    public void stop() {
        futureTask.cancel(true);
        executorService.shutdown();
        closeAndResetChannel();
        closeAndResetConnection();
        log.info("OK");
    }

    private void doConnect() {
        while (!futureTask.isCancelled() && connection == null) {
            try {
                log.info("Attempting connect to {}:{}", connectionFactory.getHost(), connectionFactory.getPort());
                connection = connectionFactory.newConnection();
            } catch (Exception ex) {
                log.error(ex.getMessage());
                waitForNextAttempt();
            }
        }
    }

    private void closeAndResetChannel() {
        try {
            closeChannel(channel);
        } catch (IOException | TimeoutException ex) {
            log.error(ex.getMessage());
            //ignored
        }
        channel = null;
    }

    private void closeAndResetConnection() {
        try {
            closeConnection(connection);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            //ignored
        }
        connection = null;
    }

    private void setupConsumers() {
        if (!futureTask.isCancelled()) {
            try {
                channel = connection.createChannel();
                evaluationResultsQueue = declareReplyToQueue(channel);
                rabbitListenerAdapter.basicConsume(channel, declareReplyToQueue(channel), experimentResponseConsumer,
                        EcaResponse.class);
                rabbitListenerAdapter.basicConsume(channel, evaluationResultsQueue, evaluationResultsConsumer,
                        EvaluationResponse.class);
                log.info("Consumers initialization has been finished");
            } catch (IOException ex) {
                log.error("There was an error while initialize consumers: {}", ex.getMessage());
            }
        }
    }

    private void waitForNextAttempt() {
        try {
            Thread.sleep(connectionAttemptIntervalMillis);
        } catch (InterruptedException e) {
            //ignored
        }
    }

    public static void main(String[] args) throws Exception {
        RabbitListenerAdapter adapter = new RabbitListenerAdapter(new JsonMessageConverter());
        MessageListenerContainer container = new MessageListenerContainer(new ConnectionFactory(), adapter);
        container.start();
        Thread.sleep(20000L);
        log.info("cancel");
        container.stop();
        Thread.sleep(50000000L);
    }
}
