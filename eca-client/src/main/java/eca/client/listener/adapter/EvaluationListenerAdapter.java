package eca.client.listener.adapter;

import eca.client.converter.MessageConverter;
import eca.client.dto.EvaluationResponse;
import eca.client.messaging.MessageHandler;

/**
 * Evaluation listener adapter
 *
 * @author Roman Batygin
 */
public class EvaluationListenerAdapter extends AbstractRabbitListenerAdapter<EvaluationResponse> {

    /**
     * Creates EvaluationListenerAdapter object.
     *
     * @param messageConverter - message converter
     * @param messageHandler   - message handler
     */
    public EvaluationListenerAdapter(MessageConverter messageConverter,
                                     MessageHandler<EvaluationResponse> messageHandler) {
        super(EvaluationResponse.class, messageConverter, messageHandler);
    }
}
