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
 *
 * @author Рома
 */
public class RandomForests extends IterativeEnsembleClassifier {
     
    private int numRandomAttr;
    private int minObj = 2;
    private int maxDepth;
    
    public RandomForests(Instances data) {
        numRandomAttr = (data.numAttributes() - 1) / 3;
    }
    
    public RandomForests() {
    }
    
    public final void setNumRandomAttr(int numRandomAttr) {
        checkForNegative(numRandomAttr);
        this.numRandomAttr = numRandomAttr;
    }
    
    public final int getNumRandomAttr() {
        return numRandomAttr;
    }
    
    public final void setMinObj(int minObj) {
        checkForNegative(minObj);
        this.minObj = minObj;
    }
    
    public final void setMaxDepth(int maxDepth) {
        checkForNegative(maxDepth);
        this.maxDepth = maxDepth;
    }
    
    public final int getMinObj() {
        return minObj;
    }
    
    public final int getMaxDepth() {
        return maxDepth;
    }
    
    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new ForestBuilder(data);
    }
    
    @Override
    public String[] getOptions() {
        String[] options = {"Число деревьев:",String.valueOf(numIterations),
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
            if (!hasNext())
                throw new NoSuchElementException();
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
