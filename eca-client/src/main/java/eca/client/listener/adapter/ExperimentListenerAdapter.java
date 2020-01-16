package eca.client.listener.adapter;

import eca.client.converter.MessageConverter;
import eca.client.dto.EcaResponse;
import eca.client.messaging.MessageHandler;

/**
 * Experiment listener adapter
 *
 * @author Roman Batygin
 */
public class ExperimentListenerAdapter extends AbstractRabbitListenerAdapter<EcaResponse> {

    /**
     * Creates ExperimentListenerAdapter object.
     *
     * @param messageConverter - message converter
     * @param messageHandler   - message handler
     */
    public ExperimentListenerAdapter(MessageConverter messageConverter,
                                     MessageHandler<EcaResponse> messageHandler) {
        super(EcaResponse.class, messageConverter, messageHandler);
    }
}
