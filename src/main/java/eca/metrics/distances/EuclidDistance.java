/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.metrics.distances;

import weka.core.Instance;

/**
 * Euclid distance function model.
 *
 * @author Roman Batygin
 */
public class EuclidDistance extends SquareEuclidDistance {

    @Override
    public double distance(Instance x1, Instance x2) {
        return Math.sqrt(super.distance(x1, x2));
    }

}
