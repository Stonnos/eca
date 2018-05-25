/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core;

import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;

/**
 * Implements iterations over the set of the random attributes.
 *
 * @author Roman Batygin
 */
public class RandomAttributesEnumeration implements Enumeration<Attribute> {

    private final Iterator<Attribute> iterator;

    /**
     * Creates <tt>AttributesEnumeration</tt> object.
     *
     * @param data   <tt>Instances</tt> object.
     * @param count  random attributes number
     * @param random random object
     */
    public RandomAttributesEnumeration(Instances data, int count, Random random) {
        ArrayList<Attribute> attributes = new ArrayList<>(count);
        int k = count;
        while (k != 0) {
            int i = random.nextInt(data.numAttributes());
            Attribute a = data.attribute(i);
            if (i != data.classIndex() && !attributes.contains(a)) {
                attributes.add(a);
                k--;
            }
        }
        iterator = attributes.iterator();
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public Attribute nextElement() {
        return iterator.next();
    }

}
