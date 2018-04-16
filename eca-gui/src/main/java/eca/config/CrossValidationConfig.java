package eca.config;

import lombok.Data;

/**
 * Cross - validation config.
 *
 * @author Roman Batygin
 */
@Data
public class CrossValidationConfig {

    /**
     * Number of folds
     */
    private Integer numFolds;
    /**
     * Number of tests
     */
    private Integer numTests;
}