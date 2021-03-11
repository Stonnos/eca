package eca.util;

import eca.client.dto.EcaResponse;
import eca.model.EcaServiceTrack;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Eca - service response utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class EcaServiceUtils {

    /**
     * Gets eca - service track details as string or default value.
     *
     * @param ecaServiceTrack - eca - service track
     * @param defaultValue    - default string value
     * @return eca - service track details as string
     */
    public static String getEcaServiceTrackDetailsOrDefault(EcaServiceTrack ecaServiceTrack, String defaultValue) {
        return !StringUtils.isBlank(ecaServiceTrack.getDetails()) ? ecaServiceTrack.getDetails() : defaultValue;
    }

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
