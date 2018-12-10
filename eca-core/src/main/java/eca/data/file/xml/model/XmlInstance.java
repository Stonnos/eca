package eca.data.file.xml.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Xml instance model.
 *
 * @author Roman Batygin
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlInstance", propOrder = "values")
public class XmlInstance {

    /**
     * Values list
     */
    @XmlElement(name = "value")
    @XmlElementWrapper
    private List<String> values;
}
