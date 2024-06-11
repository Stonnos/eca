package eca.data.file.model;

import jakarta.xml.bind.annotation.XmlSchemaType;
import lombok.Data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
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
public class AttributeModel {

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
