package eca.client.dto.options;

import eca.ensemble.sampling.SamplingMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Heterogeneous classifier options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HeterogeneousClassifierOptions extends AbstractHeterogeneousClassifierOptions {

    /**
     * Is use weighted votes method?
     */
    private Boolean useWeightedVotes;

    /**
     * Is use random classifier at each iteration?
     */
    private Boolean useRandomClassifier;

    /**
     * Sampling method at each iteration
     */
    private SamplingMethod samplingMethod;

    /**
     * Use random subspaces?
     */
    private Boolean useRandomSubspaces;
}
