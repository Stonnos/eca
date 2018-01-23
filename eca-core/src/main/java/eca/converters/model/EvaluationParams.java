package eca.converters.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * K * V cross - validation params model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationParams implements Serializable {

    /**
     * Number of folds using in k * V cross - validation method
     **/
    private int numFolds;

    /**
     * Number of tests using in k * V cross - validation method
     **/
    private int numTests;
}
