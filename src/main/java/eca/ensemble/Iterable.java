/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.core.Instances;

/**
 * Class for generating {@link IterativeBuilder} objects.
 * @author Рома
 */
public interface Iterable {

    /**
     * Returns <tt>IterativeBuilder</tt> object.
     * @param data <tt>Instances</tt> object
     * @return <tt>IterativeBuilder</t> object
     * @throws Exception
     */
    IterativeBuilder getIterativeBuilder(Instances data) throws Exception;

}
