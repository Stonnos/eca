/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

/**
 * Neural link model.
 *
 * @author Roman Batygin
 */
public class NeuralLink implements java.io.Serializable {

    /**
     * Source neuron
     **/
    private Neuron source;

    /**
     * Target neuron
     */
    private Neuron target;

    /**
     * Current weight
     **/
    private double weight;

    /**
     * Previous weight correct
     */
    private double previousCorrect;

    /**
     * Creates neural link with given neurons.
     *
     * @param source source neuron
     * @param target target neuron
     */
    public NeuralLink(Neuron source, Neuron target) {
        this.source = source;
        this.target = target;
    }

    /**
     * Creates neural link with given options
     *
     * @param u      source neuron
     * @param v      target neuron
     * @param weight neuron weight
     */
    public NeuralLink(Neuron u, Neuron v, double weight) {
        this(u, v);
        this.weight = weight;
    }

    /**
     * Return the source neuron.
     *
     * @return the source neuron
     */
    public final Neuron source() {
        return source;
    }

    /**
     * Returns the target neuron.
     *
     * @return the target neuron
     */
    public final Neuron target() {
        return target;
    }

    /**
     * Sets the value of neuron weight.
     *
     * @param weight the value of neuron weight
     */
    public final void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Returns the value of neuron weight.
     *
     * @return the value of neuron weight
     */
    public final double getWeight() {
        return weight;
    }

    /**
     * Sets the value of previous neuron weight correct.
     *
     * @param previousCorrect the value of previous neuron weight correct
     */
    public final void setPreviousCorrect(double previousCorrect) {
        this.previousCorrect = previousCorrect;
    }

    /**
     * Returns the value of previous neuron weight correct.
     *
     * @return the value of previous neuron weight correct
     */
    public final double getPreviousCorrect() {
        return previousCorrect;
    }
}
