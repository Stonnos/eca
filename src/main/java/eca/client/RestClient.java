package eca.client;

import eca.model.ClassifierDescriptor;
import eca.model.InputData;

/**
 * Implements service for communication with eca - service api.
 *
 * @author Roman Batygin
 */
public interface RestClient {

    /**
     * Sends request to eca - service.
     *
     * @param inputData <tt>InputData</tt> object.
     * @return {@link ClassifierDescriptor} object
     */
    ClassifierDescriptor execute(InputData inputData);

}
