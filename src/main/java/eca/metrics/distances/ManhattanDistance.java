/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.metrics.distances;

import weka.core.Instance;

/**
 * Manhattan distance function model.
 * @author Рома
 */
public class ManhattanDistance implements Distance {
    
    @Override
    public double distance(Instance x1, Instance x2) {
        double dist = 0.0;
        for (int i = 0; i < x1.numAttributes(); i++) {
            if (i != x1.classIndex())
                dist += Math.abs(x2.value(i) - x1.value(i));
        }
        return dist;
    }
    
}
