package eca.client;

import eca.beans.ClassifierDescriptor;
import eca.beans.InputData;
import eca.core.TestMethod;

/**
 * @author Roman Batygin
 */
public interface RestClient {

    ClassifierDescriptor execute(InputData inputData);

}
