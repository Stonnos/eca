package eca.filter;

import weka.core.Instance;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public interface Filter {

    Instances filterInstances(Instances data) throws Exception;

    Instance filterInstance(Instance obj) throws Exception;

}
