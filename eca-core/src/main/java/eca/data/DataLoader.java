/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.data;

import weka.core.Instances;

/**
 * Interface for loading data from network resource.
 *
 * @author Roman Batygin
 */
public interface DataLoader {

    /**
     * Returns <tt>Instances</tt> object loaded from data resource.
     *
     * @return {@link Instances} object
     * @throws Exception
     */
    Instances loadInstances() throws Exception;

}
