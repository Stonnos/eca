/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.core.Instances;

/**
 *
 * @author Рома
 */
public interface Iterativeable {

    IterativeBuilder getIterativeBuilder(Instances data) throws Exception;

}
