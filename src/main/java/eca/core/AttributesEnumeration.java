/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core;

import weka.core.Attribute;
import weka.core.Instances;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Random;
/**
 *
 * @author Рома
 */
public class AttributesEnumeration implements Enumeration<Attribute> {
    
    private final Iterator<Attribute> iter;
     
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
        iter = attributes.iterator();
    }
     
    @Override
    public boolean hasMoreElements() {
         return iter.hasNext();
    }
     
    @Override
    public Attribute nextElement() {
        return iter.next();
    }
    
} //End of class AttributesEnumeration
