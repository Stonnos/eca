package eca.util;

import eca.client.dto.EcaResponse;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Eca - service response utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class EcaServiceResponseUtils {

    private static final String VALIDATION_ERRORS = "Validation errors:";
    private static final String FIELD_NAME = "Field name:";
    private static final String ERROR_CODE = "Error code:";
    private static final String MESSAGE = "Message:";
    private static final String COMMA_SEPARATOR = ",";

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
                stringBuilder.append(StringUtils.LF);
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
                        .append(messageError.getFieldName());
            });
            return stringBuilder.toString();
        }
        return null;
    }
}
