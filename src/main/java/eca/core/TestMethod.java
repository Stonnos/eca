/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core;

/**
 * Classifiers evaluation type.
 *
 * @author Roman93
 */
public class TestMethod {

    /**
     * Use training data
     **/
    public static final int TRAINING_SET = 0;

    /**
     * Use k * V - folds cross - validation method
     **/
    public static final int CROSS_VALIDATION = 1;
}
