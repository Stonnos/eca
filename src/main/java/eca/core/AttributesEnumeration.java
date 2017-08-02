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
 * @author Рома
 */
public class AttributesEnumeration implements Enumeration<Attribute> {
    
    private final Iterator<Attribute> iterator;

    /**
     * Creates <tt>AttributesEnumeration</tt> object.
     * @param data <tt>Instances</tt> object.
     * @param count random attributes number
     */
    public AttributesEnumeration(Instances data, int count) {
        ArrayList<Attribute> attributes = new ArrayList<>(count);
        Random random = new Random();
        while (count != 0) {
            int i = random.nextInt(data.numAttributes());
            Attribute a = data.attribute(i);
            if (i != data.classIndex() && !attributes.contains(a)) {
                attributes.add(a);
                count--;
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
    
} //End of class AttributesEnumeration
