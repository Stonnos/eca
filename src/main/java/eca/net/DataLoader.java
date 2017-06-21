/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.net;

import weka.core.Instances;
import java.net.URL;
/**
 *
 * @author Roman93
 */
public interface DataLoader extends java.io.Serializable {

    Instances loadInstances() throws Exception;

}
