package eca.client.listener.adapter;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import eca.client.converter.JsonMessageConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EvaluationListenerAdapter} class.
 *
 * @author Roman Batygin
 */
class EvaluationListenerAdapterTest {

    private static final String QUEUE_NAME = "queue";

    private EvaluationListenerAdapter evaluationListenerAdapter;

    private Channel channel;

    private ArgumentCaptor<String> queueNameCaptor;

    @BeforeEach
    void init() {
        channel = mock(Channel.class);
        queueNameCaptor = ArgumentCaptor.forClass(String.class);
        evaluationListenerAdapter =
                new EvaluationListenerAdapter(new JsonMessageConverter(), (evaluationResponse, properties) -> {
                });
    }

    @Test
    void testConsumeMessage() throws IOException {
        evaluationListenerAdapter.basicConsume(channel, QUEUE_NAME);
        verify(channel, atLeastOnce()).basicConsume(queueNameCaptor.capture(), anyBoolean(), any(DeliverCallback.class),
                any(CancelCallback.class));
        assertEquals(QUEUE_NAME, queueNameCaptor.getValue());
    }
}
