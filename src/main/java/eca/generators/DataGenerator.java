package eca.generators;

import weka.core.Instances;

/**
 * Interface for generating data.
 * @author Roman Batygin
 */
public interface DataGenerator {

    /**
     * Generate simulated data.
     *
     * @return <tt>Instances</tt> object.
     */
    Instances generate();
}
