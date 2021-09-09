package eca.client.listener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eca.client.listener.adapter.AbstractRabbitListenerAdapter;
import eca.client.util.RabbitUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static com.google.common.collect.Maps.newHashMap;
import static eca.client.util.RabbitUtils.declareReplyToQueue;

/**
 * Message listener container.
 *
 * @author Roman Batygin
 */
@Slf4j
public class MessageListenerContainer {

    /**
     * Connection factory
     */
    @Getter
    @Setter
    private ConnectionFactory connectionFactory;

    /**
     * Rabbit listeners adapters map, where key is queue name
     */
    @Getter
    @Setter
    private Map<String, AbstractRabbitListenerAdapter<?>> rabbitListenerAdapters = newHashMap();

    /**
     * Connection attempt interval in millis
     */
    @Getter
    @Setter
    private long connectionAttemptIntervalMillis = 2000L;

    /**
     * Callback after success connection
     */
    @Getter
    @Setter
    private Consumer<ConnectionFactory> successCallback;

    private Connection connection;

    private Channel channel;

    private ExecutorService executorService;

    private FutureTask<Void> futureTask;

    @Getter
    private volatile boolean running;

    @Getter
    private volatile boolean started;

    private final Object lifecycleMonitor = new Object();

    /**
     * Default constructor.
     */
    public MessageListenerContainer() {
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Starts message listener container.
     */
    public void start() {
        synchronized (lifecycleMonitor) {
            if (running) {
                throw new IllegalStateException();
            }
            Callable<Void> callable = () -> {
                openConnection(futureTask);
                setupConsumers(futureTask);
                return null;
            };
            futureTask = new FutureTask<>(callable);
            executorService.execute(futureTask);
            running = true;
        }
    }

    /**
     * Stop message listener container.
     */
    public void stop() throws IOException, TimeoutException {
        synchronized (lifecycleMonitor) {
            if (!running) {
                throw new IllegalStateException();
            }
            futureTask.cancel(true);
            closeChannel();
            closeConnection();
            started = false;
            running = false;
            log.info("Message listener container has been stopped");
        }
    }

    private void openConnection(FutureTask<Void> task) {
        while (!task.isCancelled() && connection == null) {
            try {
                log.info("Attempting connect to {}:{}", connectionFactory.getHost(), connectionFactory.getPort());
                connection = connectionFactory.newConnection();
                log.info("Connected to {}:{}", connectionFactory.getHost(), connectionFactory.getPort());
            } catch (Exception ex) {
                log.error("There was an error while attempting to connect to {}:{}: {}", connectionFactory.getHost(),
                        connectionFactory.getPort(), ex.getMessage(), ex);
                waitForNextAttempt();
            }
        }
    }

    private void closeChannel() throws IOException, TimeoutException {
        RabbitUtils.closeChannel(channel);
        channel = null;
    }

    private void closeConnection() throws IOException {
        RabbitUtils.closeConnection(connection);
        connection = null;
    }

    private void setupConsumers(FutureTask<Void> task) {
        if (!task.isCancelled()) {
            try {
                channel = connection.createChannel();
                for (Map.Entry<String, AbstractRabbitListenerAdapter<?>> adapterEntry :
                        rabbitListenerAdapters.entrySet()) {
                    String queue = declareReplyToQueue(adapterEntry.getKey(), channel);
                    adapterEntry.getValue().basicConsume(channel, queue);
                }
                started = true;
                log.info("Consumers initialization has been finished");
                Optional.ofNullable(successCallback).ifPresent(consumer -> consumer.accept(connectionFactory));
            } catch (IOException ex) {
                log.error("There was an error while initialize consumers: {}", ex.getMessage(), ex);
            }
        }
    }

    private void waitForNextAttempt() {
        try {
            Thread.sleep(connectionAttemptIntervalMillis);
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for next attempt");
            Thread.currentThread().interrupt();
        }
    }
}
