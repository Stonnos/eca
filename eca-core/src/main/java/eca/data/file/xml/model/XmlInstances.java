package eca.data.file.xml.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * @author Roman Batygin
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "relationName",
        "className",
        "attributes",
        "instances"
})
@XmlRootElement(name = "xmlInstances")
public class XmlInstances {

    @XmlElement
    private String relationName;

    @XmlElement
    private String className;

    @XmlElement(name = "attribute")
    @XmlElementWrapper
    private List<XmlAttribute> attributes;

    @XmlElement(name = "instance")
    @XmlElementWrapper
    private List<XmlInstance> instances;
}
