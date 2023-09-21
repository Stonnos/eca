package eca.client.dto.options;

import lombok.Data;

import java.io.Serializable;

/**
 * Back propagation learning algorithm options model.
 *
 * @author Roman Batygin
 */
@Data
public class BackPropagationOptions implements Serializable {

    /**
     * Learning rate value
     */
    private Double learningRate;

    /**
     * Momentum coefficient value
     */
    private Double momentum;
}
