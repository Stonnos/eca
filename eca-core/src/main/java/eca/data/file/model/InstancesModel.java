package eca.data.file.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

import java.util.List;

/**
 * Xml instances model.
 *
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
public class InstancesModel {

    /**
     * Relation name
     */
    @XmlElement
    private String relationName;

    /**
     * Class attribute name
     */
    @XmlElement
    private String className;

    /**
     * Attributes list
     */
    @XmlElement(name = "attribute")
    @XmlElementWrapper
    private List<AttributeModel> attributes;

    /**
     * Data list
     */
    @XmlElement(name = "instance")
    @XmlElementWrapper
    private List<InstanceModel> instances;
}
