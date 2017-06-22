/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.trees.DecisionTreeClassifier;
import java.util.NoSuchElementException;
import weka.core.Instances;
import eca.trees.CART;

/**
 * Class for generating Random forests model. <p>
 *
 * Valid options are: <p>
 *
 * Set minimum number of instances per leaf. (Default: 2) <p>
 *
 * Set maximum tree depth. (Default: 0 (denotes infinity)) <p>
 *
 * Set number of random attributes at each split. (Default: 0 (denotes all attributes)) <p>
 *
 * @author Рома
 */
public class RandomForests extends IterativeEnsembleClassifier {

    /** Number of random attributes at each split **/
    private int numRandomAttr;

    /** Number of instances per leaf **/
    private int minObj = 2;

    /** Maximum tree depth **/
    private int maxDepth;

    /**
     * Creates <code>RandomForests</code> object with K / 3 random attributes
     * at each split, where K is the number of input attributes.
     * @param data <code>Instances</code> object
     */
    public RandomForests(Instances data) {
        numRandomAttr = (data.numAttributes() - 1) / 3;
    }

    /**
     * Creates <code>RandomForests</code> object
     */
    public RandomForests() {
    }

    /**
     * Sets the value of random attributes number
     * @param numRandomAttr the value of random attributes number
     * @exception IllegalArgumentException if the value of random attributes number is less than zero
     */
    public final void setNumRandomAttr(int numRandomAttr) {
        checkForNegative(numRandomAttr);
        this.numRandomAttr = numRandomAttr;
    }

    /**
     * Returns the value of random attributes number.
     * @return the value of random attributes number
     */
    public final int getNumRandomAttr() {
        return numRandomAttr;
    }

    /**
     * Sets the value of minimum objects per leaf.
     * @param minObj the value of minimum objects per leaf
     * @exception IllegalArgumentException if the value of minimum objects per leaf is less than zero
     */
    public final void setMinObj(int minObj) {
        checkForNegative(minObj);
        this.minObj = minObj;
    }

    /**
     * Sets the value of maximum tree depth.
     * @param maxDepth the value of maximum tree depth.
     * @exception IllegalArgumentException if the value of maximum tree depth is less than zero
     */
    public final void setMaxDepth(int maxDepth) {
        checkForNegative(maxDepth);
        this.maxDepth = maxDepth;
    }

    /**
     * Returns the value of minimum objects per leaf.
     * @return the value of minimum objects per leaf
     */
    public final int getMinObj() {
        return minObj;
    }

    /**
     * Returns the value of maximum tree depth.
     * @return the value of maximum tree depth
     */
    public final int getMaxDepth() {
        return maxDepth;
    }
    
    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new ForestBuilder(data);
    }
    
    @Override
    public String[] getOptions() {
        String[] options = {"Число деревьев:", String.valueOf(numIterations),
                            "Минимальное число объектов в листе:",  String.valueOf(minObj),
                            "Максиальная глубина дерева:", String.valueOf(maxDepth),
                            "Число случайных атрибутов:", String.valueOf(numRandomAttr)};
        return options;
    }
    
    /**
     * 
     */
    private class ForestBuilder extends AbstractBuilder {
       
        Sampler sampler = new Sampler();
        
        public ForestBuilder(Instances dataSet) throws Exception {
            super(dataSet);
            if (numRandomAttr > data.numAttributes() - 1)
                throw new IllegalArgumentException("Illegal value of randomAttrNum: " +
                           String.valueOf(numRandomAttr));
        }
        
        @Override
        public int next()  throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Instances bag = sampler.bootstrap(data);
            DecisionTreeClassifier model = new CART();
            model.setRandomTree(true);
            model.setNumRandomAttr(numRandomAttr);
            model.setMinObj(minObj);
            model.setMaxDepth(maxDepth);
            model.buildClassifier(bag);
            classifiers.add(model);
            return ++index;
        }
              
    } //End of class ForestBuilder
    
}
