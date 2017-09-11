package eca.filter;

import weka.core.Instance;
import weka.core.Instances;

/**
 * Data filter interface.
 * @author Roman Batygin
 */
public interface Filter {

    /**
     * Filters input data.
     * @param data instances object
     * @return {@link Instances} object
     * @throws Exception
     */
    Instances filterInstances(Instances data) throws Exception;

    /**
     * Filters input instance.
     * @param obj instance object
     * @return {@link Instance} object
     * @throws Exception
     */
    Instance filterInstance(Instance obj) throws Exception;

}
