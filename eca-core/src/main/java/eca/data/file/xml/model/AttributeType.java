package eca.data.file.xml.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Roman Batygin
 */
@XmlType(name = "attributeType")
@XmlEnum
public enum AttributeType {

    NUMERIC,
    NOMINAL,
    DATE
}
