/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.metrics.distances;

/**
 * Instance distance descriptor.
 *
 * @author Рома
 */
public class InstanceDistance implements java.io.Serializable, Comparable<InstanceDistance> {

    /**
     * Instance id
     **/
    private int id;

    /**
     * Instance distance
     **/
    private double distance;

    public InstanceDistance() {
    }

    /**
     * Creates <code>InstanceDistance</code> object.
     *
     * @param id instance id
     */
    public InstanceDistance(int id) {
        this.id = id;
    }

    /**
     * Creates <code>InstanceDistance</code> object.
     *
     * @param id       instance id
     * @param distance instance distance
     */
    public InstanceDistance(int id, double distance) {
        this(id);
        this.distance = distance;
    }

    /**
     * Sets the value of instance id.
     *
     * @param id the value of instance id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the value of instance id.
     *
     * @return the value of instance id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of instance distance.
     *
     * @param distance the value of instance distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns the value of instance distance.
     *
     * @return the value of instance distance.
     */
    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(InstanceDistance obj) {
        return Double.valueOf(getDistance()).compareTo(obj.getDistance());
    }

}
