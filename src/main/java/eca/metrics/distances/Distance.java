/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.metrics.distances;

import weka.core.Instance;
/**
 *
 * @author Рома
 */
public interface Distance extends java.io.Serializable {

    double distance(Instance x1, Instance x2);

}
