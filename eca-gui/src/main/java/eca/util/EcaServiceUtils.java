package eca.util;

import eca.client.dto.EcaResponse;
import lombok.experimental.UtilityClass;

import java.util.Optional;

/**
 * Eca - service response utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class EcaServiceUtils {

    /**
     * Gets first error message.
     *
     * @param ecaResponse - eca response
     * @return error message string
     */
    public static String getFirstErrorAsString(EcaResponse ecaResponse) {
        if (Optional.ofNullable(ecaResponse).map(EcaResponse::getErrors).isPresent()) {
            return ecaResponse.getErrors().iterator().next().getMessage();
        }
        return null;
    }
}
