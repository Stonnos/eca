package eca.client.dto.options;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Base options model for heterogeneous ensemble.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractHeterogeneousClassifierOptions extends IterativeEnsembleOptions {

    /**
     * Min. error threshold
     */
    private Double minError;

    /**
     * Max. error threshold
     */
    private Double maxError;

    /**
     * Individual classifiers options
     */
    private List<ClassifierOptions> classifierOptions;
}
