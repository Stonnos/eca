package eca.gui.actions;

import eca.client.EcaServiceClient;
import eca.client.dto.EcaResponse;
import eca.client.dto.ExperimentRequestDto;

/**
 * Implements experiment request sender.
 *
 * @author Roman Batygin
 */
public class ExperimentRequestSender extends AbstractCallback<EcaResponse> {

    private EcaServiceClient ecaServiceClient;
    private ExperimentRequestDto experimentRequestDto;
    /**
     * Constructor with params.
     *
     * @param ecaServiceClient     - eca service client
     * @param experimentRequestDto - experiment request dto.
     */
    public ExperimentRequestSender(EcaServiceClient ecaServiceClient, ExperimentRequestDto experimentRequestDto) {
        this.ecaServiceClient = ecaServiceClient;
        this.experimentRequestDto = experimentRequestDto;
    }

    @Override
    protected EcaResponse performAndGetResult() {
        return ecaServiceClient.createExperimentRequest(experimentRequestDto);
    }
}
