package eca.data.file.xml.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Xml attribute model.
 *
 * @author Roman Batygin
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlAttribute", propOrder = {
        "name",
        "type",
        "dateFormat",
        "values"
})
public class XmlAttribute {

    /**
     * Attribute name
     */
    @XmlElement
    private String name;

    /**
     * Attribute type
     */
    @XmlElement
    @XmlSchemaType(name = "string")
    private AttributeType type;

    /**
     * Date format for date attribute
     */
    @XmlElement
    private String dateFormat;

    /**
     * Nominal attribute values
     */
    @XmlElement(name = "value")
    @XmlElementWrapper
    private List<String> values;
}
