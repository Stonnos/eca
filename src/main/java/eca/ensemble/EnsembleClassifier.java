/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import java.util.ArrayList;
import weka.classifiers.Classifier;
/**
 *
 * @author Рома
 */
public interface EnsembleClassifier {

    ArrayList<Classifier> getStructure() throws Exception;

}
