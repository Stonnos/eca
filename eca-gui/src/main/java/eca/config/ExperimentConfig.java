package eca.config;

import lombok.Data;

/**
 * Experiment config.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentConfig {

    /**
     * Number of best classifier models to show
     */
    private Integer numBestResults;
}
