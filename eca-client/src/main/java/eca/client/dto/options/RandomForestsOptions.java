package eca.client.dto.options;

import eca.ensemble.forests.DecisionTreeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Random forests options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RandomForestsOptions extends IterativeEnsembleOptions {

    /**
     * Random attributes number at each node split
     */
    private Integer numRandomAttr;

    /**
     * Min. objects per leaf
     */
    private Integer minObj;

    /**
     * Maximum tree depth
     */
    private Integer maxDepth;

    /**
     * Decision tree algorithm
     */
    private DecisionTreeType decisionTreeType;
}
