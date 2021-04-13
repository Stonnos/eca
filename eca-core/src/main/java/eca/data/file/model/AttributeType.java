package eca.data.file.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Attribute type xml enum.
 *
 * @author Roman Batygin
 */
@XmlType(name = "attributeType")
@XmlEnum
public enum AttributeType {

    /**
     * Numeric type
     */
    NUMERIC {
        @Override
        public <T> T handle(AttributeTypeVisitor<T> attributeTypeVisitor) {
            return attributeTypeVisitor.caseNumeric();
        }
    },

    /**
     * Nominal type
     */
    NOMINAL {
        @Override
        public <T> T handle(AttributeTypeVisitor<T> attributeTypeVisitor) {
            return attributeTypeVisitor.caseNominal();
        }
    },

    /**
     * Date type
     */
    DATE {
        @Override
        public <T> T handle(AttributeTypeVisitor<T> attributeTypeVisitor) {
            return attributeTypeVisitor.caseDate();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param attributeTypeVisitor visitor class
     * @param <T>                  generic class
     * @return generic class
     */
    public abstract <T> T handle(AttributeTypeVisitor<T> attributeTypeVisitor);
}
