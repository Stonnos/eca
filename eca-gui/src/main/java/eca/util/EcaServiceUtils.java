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

    private static final String VALIDATION_ERRORS = "Validation errors:";
    private static final String FIELD_NAME = "Field name:";
    private static final String ERROR_CODE = "Error code:";
    private static final String MESSAGE = "Message:";
    private static final String COMMA_SEPARATOR = ",";
    private static final String RAW_SEPARATOR = ";";

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

    /**
     * Gets validation errors as string.
     *
     * @param ecaResponse - eca response
     * @return validation error message string
     */
    public static String getValidationErrorsAsString(EcaResponse ecaResponse) {
        if (Optional.ofNullable(ecaResponse).map(EcaResponse::getErrors).isPresent()) {
            StringBuilder stringBuilder = new StringBuilder(VALIDATION_ERRORS);
            ecaResponse.getErrors().forEach(messageError -> {
                stringBuilder.append(StringUtils.SPACE);
                stringBuilder.append(FIELD_NAME)
                        .append(StringUtils.SPACE)
                        .append(messageError.getFieldName())
                        .append(COMMA_SEPARATOR)
                        .append(StringUtils.SPACE);
                stringBuilder.append(ERROR_CODE)
                        .append(StringUtils.SPACE)
                        .append(messageError.getCode())
                        .append(COMMA_SEPARATOR)
                        .append(StringUtils.SPACE);
                stringBuilder.append(MESSAGE)
                        .append(StringUtils.SPACE)
                        .append(messageError.getMessage());
                stringBuilder.append(RAW_SEPARATOR);
            });
            return stringBuilder.toString();
        }
        return null;
    }
}
