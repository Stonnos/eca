package eca.core;

import lombok.Builder;
import lombok.Data;
import weka.core.Instances;

/**
 * Instances data model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class InstancesDataModel {

    /**
     * Instances uuid
     */
    private String uuid;

    /**
     * Last modifications count
     */
    private int lastModificationCount;

    /**
     * Instances model
     */
    private Instances data;
}
