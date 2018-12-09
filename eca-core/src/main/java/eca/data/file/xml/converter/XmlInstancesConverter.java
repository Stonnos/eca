package eca.data.file.xml.converter;

import eca.data.file.xml.model.AttributeType;
import eca.data.file.xml.model.XmlAttribute;
import eca.data.file.xml.model.XmlInstance;
import eca.data.file.xml.model.XmlInstances;
import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Roman Batygin
 */
public class XmlInstancesConverter {

    public XmlInstances convert(Instances data) {
        XmlInstances xmlInstances = new XmlInstances();
        xmlInstances.setRelationName(data.relationName());
        if (data.classIndex() > 0) {
            xmlInstances.setClassName(data.classAttribute().name());
        }
        xmlInstances.setAttributes(convertAttributes(data));
        xmlInstances.setInstances(data.stream().map(this::convertInstance).collect(Collectors.toList()));
        return xmlInstances;
    }

    public Instances convert(XmlInstances xmlInstances) {
        return null;
    }

    private XmlInstance convertInstance(Instance instance) {
        XmlInstance xmlInstance = new XmlInstance();
        xmlInstance.setValues(new ArrayList<>());
        for (int i = 0; i < instance.numAttributes(); i++) {
            String value = StringUtils.EMPTY;
            if (!instance.isMissing(i)) {
                Attribute attribute = instance.attribute(i);
                switch (attribute.type()) {
                    case Attribute.DATE:
                        value = instance.stringValue(i);
                        break;
                    case Attribute.NUMERIC:
                        value = String.valueOf(instance.value(i));
                        break;
                    case Attribute.NOMINAL:
                        value = instance.stringValue(i);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                String.format("Unexpected attribute [%s] type: %d!", attribute.name(),
                                        attribute.type()));
                }
            }
            xmlInstance.getValues().add(value);
        }
        return xmlInstance;
    }

    private List<XmlAttribute> convertAttributes(Instances data) {
        List<XmlAttribute> xmlAttributes = new ArrayList<>();
        for (int i = 0; i < data.numAttributes(); i++) {
            xmlAttributes.add(convertAttribute(data.attribute(i)));
        }
        return xmlAttributes;
    }

    private XmlAttribute convertAttribute(Attribute attribute) {
        XmlAttribute xmlAttribute = new XmlAttribute();
        xmlAttribute.setName(attribute.name());
        switch (attribute.type()) {
            case Attribute.DATE:
                xmlAttribute.setType(AttributeType.DATE);
                xmlAttribute.setDateFormat(attribute.getDateFormat());
                break;
            case Attribute.NUMERIC:
                xmlAttribute.setType(AttributeType.NUMERIC);
                break;
            case Attribute.NOMINAL:
                xmlAttribute.setType(AttributeType.NOMINAL);
                xmlAttribute.setValues(new ArrayList<>());
                for (int i = 0; i < attribute.numValues(); i++) {
                    xmlAttribute.getValues().add(attribute.value(i));
                }
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Unexpected attribute [%s] type: %d!", attribute.name(), attribute.type()));
        }
        return xmlAttribute;
    }
}
