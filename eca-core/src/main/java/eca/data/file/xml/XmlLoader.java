/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.data.file.xml;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.file.resource.DataResource;
import eca.data.file.xml.converter.XmlInstancesConverter;
import eca.data.file.xml.model.XmlInstances;
import weka.core.Instances;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Class for loading xml files.
 *
 * @author Roman Batygin
 */
public class XmlLoader extends AbstractDataLoader<DataResource> {


    private XmlInstancesConverter xmlInstancesConverter = new XmlInstancesConverter();

    @Override
    public Instances loadInstances() throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getSource().openInputStream(), StandardCharsets.UTF_8))) {
            JAXBContext jaxbContext = JAXBContext.newInstance(XmlInstances.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            XmlInstances xmlInstances = (XmlInstances) unmarshaller.unmarshal(reader);
            Instances instances = xmlInstancesConverter.convert(xmlInstances);
            instances.setClassIndex(instances.numAttributes() - 1);
            return instances;
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
