package eca.client.core;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import eca.client.converter.JsonMessageConverter;
import eca.client.dto.ExperimentRequestDto;
import eca.client.exception.EcaServiceException;
import eca.client.rabbit.ConnectionManager;
import eca.client.rabbit.RabbitSender;
import eca.client.util.RabbitUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static eca.client.TestHelperUtils.createExperimentRequestDto;
import static eca.client.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RabbitSender} class.
 *
 * @author Roman Batygin
 */
class RabbitSenderTest {

    private static final String QUEUE = "queue";

    private JsonMessageConverter jsonMessageConverter = new JsonMessageConverter();

    private RabbitSender rabbitSender = new RabbitSender();

    private Instances instances;

    private Channel channel;

    @BeforeEach
    void init() throws IOException, TimeoutException {
        instances = loadInstances();
        rabbitSender.setMessageConverter(jsonMessageConverter);
        ConnectionManager connectionManager = mock(ConnectionManager.class);
        rabbitSender.setConnectionManager(connectionManager);
        channel = mock(Channel.class);
        when(connectionManager.getChannel()).thenReturn(channel);
    }

    @Test
    void testSendMessage() throws IOException {
        ExperimentRequestDto experimentRequestDto = createExperimentRequestDto();
        experimentRequestDto.setData(instances);
        AMQP.BasicProperties properties = RabbitUtils.buildMessageProperties(QUEUE, UUID.randomUUID().toString());
        rabbitSender.sendMessage(QUEUE, experimentRequestDto, properties);
        verify(channel, atLeastOnce()).basicPublish(StringUtils.EMPTY, QUEUE, properties,
                jsonMessageConverter.toMessage(experimentRequestDto));
    }

    @Test
    void testSendMessageWithException() throws IOException {
        ExperimentRequestDto experimentRequestDto = createExperimentRequestDto();
        experimentRequestDto.setData(instances);
        AMQP.BasicProperties properties = RabbitUtils.buildMessageProperties(QUEUE, UUID.randomUUID().toString());
        doThrow(new EcaServiceException(StringUtils.EMPTY)).when(channel).basicPublish(anyString(), anyString(), any(),
                any());
        assertThrows(EcaServiceException.class,
                () -> rabbitSender.sendMessage(QUEUE, experimentRequestDto, properties));
    }
}
