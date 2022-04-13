package eca.client.listener.adapter;

import eca.client.converter.MessageConverter;
import eca.client.dto.ExperimentResponse;
import eca.client.messaging.MessageHandler;

/**
 * Experiment listener adapter
 *
 * @author Roman Batygin
 */
public class ExperimentListenerAdapter extends AbstractRabbitListenerAdapter<ExperimentResponse> {

    /**
     * Creates ExperimentListenerAdapter object.
     *
     * @param messageConverter - message converter
     * @param messageHandler   - message handler
     */
    public ExperimentListenerAdapter(MessageConverter messageConverter,
                                     MessageHandler<ExperimentResponse> messageHandler) {
        super(ExperimentResponse.class, messageConverter, messageHandler);
    }
}
