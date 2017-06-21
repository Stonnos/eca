/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.metrics.distances;

/**
 *
 * @author Рома
 */
public class InstanceDistance implements java.io.Serializable, Comparable<InstanceDistance> {

    private int id;
    private double distance;

    public InstanceDistance() {
    }

    public InstanceDistance(int id) {
        this.id = id;
    }

    public InstanceDistance(int id, double distance) {
        this(id);
        this.distance = distance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(InstanceDistance obj) {
        return Double.valueOf(getDistance()).compareTo(obj.getDistance());
    }

}
