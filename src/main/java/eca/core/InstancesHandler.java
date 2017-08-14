/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core;

import weka.core.Instances;

/**
 * Interface for obtaining training set.
 *
 * @author Roman93
 */
public interface InstancesHandler {

    /**
     * Returns initial training set object.
     *
     * @return initial training set object.
     */
    Instances getData();

}
