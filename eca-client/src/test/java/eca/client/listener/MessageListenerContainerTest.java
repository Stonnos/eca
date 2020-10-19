package eca.client.listener;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eca.client.listener.adapter.EvaluationListenerAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MessageListenerContainer} class.
 *
 * @author Roman Batygin
 */
class MessageListenerContainerTest {

    private MessageListenerContainer messageListenerContainer = new MessageListenerContainer();

    private EvaluationListenerAdapter evaluationListenerAdapter;

    @BeforeEach
    void init() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        messageListenerContainer.setConnectionFactory(connectionFactory);
        Connection connection = mock(Connection.class);
        when(connectionFactory.newConnection()).thenReturn(connection);
        Channel channel = mock(Channel.class);
        when(channel.queueDeclare(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), any())).thenReturn(
                mock(AMQP.Queue.DeclareOk.class));
        when(connection.createChannel()).thenReturn(channel);
        evaluationListenerAdapter = mock(EvaluationListenerAdapter.class);
        messageListenerContainer.getRabbitListenerAdapters().put(UUID.randomUUID().toString(),
                evaluationListenerAdapter);
    }

    @Test
    void testSetupContainer() throws IOException {
        messageListenerContainer.start();
        await().until(messageListenerContainer::isStarted);
        assertTrue(messageListenerContainer.isRunning());
        assertTrue(messageListenerContainer.isStarted());
        verify(evaluationListenerAdapter, atLeastOnce()).basicConsume(any(Channel.class), any());
    }
}
