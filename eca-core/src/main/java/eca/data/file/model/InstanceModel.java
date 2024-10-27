package eca.data.file.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

import java.util.List;

/**
 * Xml instance model.
 *
 * @author Roman Batygin
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlInstance", propOrder = "values")
public class InstanceModel {

    /**
     * Values list
     */
    @XmlElement(name = "value")
    @XmlElementWrapper
    private List<Double> values;
}
