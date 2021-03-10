package eca.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Message error model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageError {

    /**
     * Error code
     */
    private String code;

    /**
     * Error field
     */
    private String fieldName;

    /**
     * Errors list
     */
    private List<MessageError> errors;
}
