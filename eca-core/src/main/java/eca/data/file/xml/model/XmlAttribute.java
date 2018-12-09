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

    @XmlElement
    private String name;

    @XmlElement
    @XmlSchemaType(name = "string")
    private AttributeType type;

    @XmlElement
    private String dateFormat;

    @XmlElement(name = "value")
    @XmlElementWrapper
    private List<String> values;
}
