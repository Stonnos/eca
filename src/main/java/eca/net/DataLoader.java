/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.net;

import weka.core.Instances;

/**
 * Interface for loading data from network resource.
 *
 * @author Roman93
 */
public interface DataLoader extends java.io.Serializable {

    /**
     * Returns <tt>Instances</tt> object loaded from network resource.
     *
     * @return <tt>Instances</tt> object loaded from network resource
     * @throws Exception
     */
    Instances loadInstances() throws Exception;

}
