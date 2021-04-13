package eca.gui.service;

import eca.client.dto.EcaResponse;
import eca.client.dto.MessageError;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eca.config.VelocityConfigService.getTemplate;
import static eca.util.VelocityUtils.mergeContext;

/**
 * Velocity templates service.
 *
 * @author Roman Batygin
 */
public class TemplateService {

    private static final String ERROR_MESSAGE_VM = "vm-templates/errorMessage.vm";
    private static final String VALIDATION_ERRORS_MESSAGE_VM = "vm-templates/validationErrors.vm";


    private static final String ERROR_MESSAGE_PARAM = "errorMessage";
    private static final String VALIDATION_ERRORS_PARAM = "validationErrors";

    /**
     * Gets formatted error message string.
     *
     * @param message - error message
     * @return formatted error message string
     */
    public static String getErrorMessageAsHtml(String message) {
        Template template = getTemplate(ERROR_MESSAGE_VM);
        VelocityContext context = new VelocityContext();
        context.put(ERROR_MESSAGE_PARAM, message);
        return mergeContext(template, context);
    }

    /**
     * Gets eca response validation errors string as html.
     *
     * @param ecaResponse - eca response
     * @return validation errors string as html
     */
    public static String getValidationErrorsMessageAsHtml(EcaResponse ecaResponse) {
        List<MessageError> errors =
                Optional.of(ecaResponse).map(EcaResponse::getErrors).orElse(Collections.emptyList());
        Template template = getTemplate(VALIDATION_ERRORS_MESSAGE_VM);
        VelocityContext context = new VelocityContext();
        context.put(VALIDATION_ERRORS_PARAM, errors);
        return mergeContext(template, context);
    }

}
