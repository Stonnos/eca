/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.data.file.xml;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.file.resource.DataResource;
import weka.core.Instances;
import weka.core.xml.XMLInstances;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Class for loading xml files.
 *
 * @author Roman Batygin
 */
public class XmlLoader extends AbstractDataLoader<DataResource> {

    private static final String UTF_8 = "UTF-8";

    @Override
    public Instances loadInstances() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getSource().openInputStream(), UTF_8))) {
            XMLInstances xmlInstances = new XMLInstances();
            xmlInstances.setXML(reader);
            return xmlInstances.getInstances();
        }
    }

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!resource.getFile().endsWith(DataFileExtension.XML.getExtendedExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

}
