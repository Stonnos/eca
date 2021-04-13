package eca.data.file.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
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
