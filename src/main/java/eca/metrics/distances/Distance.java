/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.metrics.distances;

import weka.core.Instance;

/**
 * Distance function model.
 *
 * @author Рома
 */
public interface Distance extends java.io.Serializable {

    /**
     * Returns the value of distance between x1 and x2 instances.
     *
     * @param x1 first instance
     * @param x2 second instance
     * @return the value of distance between x1 and x2 instances
     */
    double distance(Instance x1, Instance x2);

}
