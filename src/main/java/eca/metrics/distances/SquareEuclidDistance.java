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
public class SquareEuclidDistance implements Distance {
    
    @Override
    public double distance(Instance x1, Instance x2) {
        double dist = 0.0;
        for (int i = 0; i < x1.numAttributes(); i++) {
            if (i != x1.classIndex())
                dist += (x2.value(i) - x1.value(i)) * (x2.value(i) - x1.value(i));
        }
        return dist;
    }
    
}
