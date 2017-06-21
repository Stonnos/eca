/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

/**
 *
 * @author Рома
 */
public class NeuralLink implements java.io.Serializable {

    private Neuron source, target;
    private double weight, previousCorrect;
    
    public NeuralLink(Neuron source, Neuron target) {
	    this.source = source;
        this.target = target;
    }
        
    public NeuralLink(Neuron u, Neuron v, double weight) {
	    this(u,v);
        this.weight = weight;
    }
    
    public final Neuron source() {
        return source;
    }
    
    public final Neuron target() {
        return target;
    }
    
    public final void setWeight(double weight) {
        this.weight = weight;
    }
    
    public final double getWeight() {
        return weight;
    }
    
    public final void setPreviousCorrect(double previousCorrect) {
        this.previousCorrect = previousCorrect;
    }
    
    public final double getPreviousCorrect() {
        return previousCorrect;
    }
}
