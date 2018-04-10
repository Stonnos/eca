package eca.gui.actions;

import eca.client.EcaServiceClient;
import eca.client.dto.EcaResponse;
import eca.client.dto.ExperimentRequestDto;

/**
 * Implements experiment request sender.
 *
 * @author Roman Batygin
 */
public class ExperimentRequestSender implements CallbackAction {

    private EcaServiceClient ecaServiceClient;
    private ExperimentRequestDto experimentRequestDto;
    private EcaResponse ecaResponse;

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
    public void apply() throws Exception {
        ecaResponse = ecaServiceClient.createExperimentRequest(experimentRequestDto);
    }

    /**
     * Returns eca response.
     *
     * @return eca response
     */
    public EcaResponse getEcaResponse() {
        return ecaResponse;
    }
}
