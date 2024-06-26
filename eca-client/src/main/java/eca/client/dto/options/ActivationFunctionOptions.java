package eca.client.dto.options;

import eca.neural.functions.ActivationFunctionType;
import lombok.Data;

import java.io.Serializable;

/**
 * Activation function options.
 *
 * @author Roman Batygin
 */
@Data
public class ActivationFunctionOptions implements Serializable {

    /**
     * Activation function type
     */
    private ActivationFunctionType activationFunctionType;

    /**
     * Activation function coefficient value
     */
    private Double coefficient;
}
