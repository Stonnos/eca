package eca.generators;

import weka.core.Instances;

/**
 * Interface for generating data.
 * Created by Roman93 on 15.04.2017.
 */
public interface DataGenerator {

    /**
     * Generate simulated data.
     *
     * @return <tt>Instances</tt> object.
     */
    Instances generate();
}
