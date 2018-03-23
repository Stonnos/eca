package eca.client.util;

import eca.client.exception.EcaServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
public class Utils {

    private static final String EMPTY_RESPONSE_MESSAGE =
            "No or empty response has been received from eca - service!";
    private static final String ERROR_MESSAGE_FORMAT = "Got HTTP error [%d, %s] from eca - service!";

    /**
     * Validates response for Http status with 200 code.
     *
     * @param response - response
     */
    public static void validateResponse(ResponseEntity response) {
        if (!Optional.ofNullable(response).map(ResponseEntity::getBody).isPresent()) {
            throw new EcaServiceException(EMPTY_RESPONSE_MESSAGE);
        }
        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            String errorMessage = String.format(ERROR_MESSAGE_FORMAT,
                    response.getStatusCode().value(), response.getStatusCode().getReasonPhrase());
            throw new EcaServiceException(errorMessage);
        }
    }
}
