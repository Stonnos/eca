package eca.client.dto.options;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Iterative ensemble classifier options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class IterativeEnsembleOptions extends ClassifierOptions {

    /**
     * Iterations number
     */
    private Integer numIterations;

    /**
     * Threads number
     */
    private Integer numThreads;

    /**
     * Seed value for random generator
     */
    private Integer seed;
}
