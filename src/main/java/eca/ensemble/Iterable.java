/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.core.Instances;

/**
 * Class for generating <code>IterativeBuilder</code> objects.
 * @author Рома
 */
public interface Iterable {

    /**
     * Returns <code>IterativeBuilder</code> object.
     * @param data <code>Instances</code> object
     * @return <code>IterativeBuilder</code> object
     * @throws Exception
     */
    IterativeBuilder getIterativeBuilder(Instances data) throws Exception;

}
