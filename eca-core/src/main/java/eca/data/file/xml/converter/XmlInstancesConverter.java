package eca.data.file.xml.converter;

import eca.data.file.xml.model.AttributeType;
import eca.data.file.xml.model.AttributeTypeVisitor;
import eca.data.file.xml.model.XmlAttribute;
import eca.data.file.xml.model.XmlInstance;
import eca.data.file.xml.model.XmlInstances;
import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Xml instances converter.
 *
 * @author Roman Batygin
 */
public class XmlInstancesConverter {

    /**
     * Converts instances into xml model.
     *
     * @param data - instances
     * @return xml instances
     */
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

    /**
     * Converts xml instances into instances model.
     *
     * @param xmlInstances - xml instances
     * @return instances
     */
    public Instances convert(XmlInstances xmlInstances) {
        ArrayList<Attribute> attributes = xmlInstances.getAttributes().stream().map(this::convertAttribute).collect(
                Collectors.toCollection(ArrayList::new));
        Instances instances =
                new Instances(xmlInstances.getRelationName(), attributes, xmlInstances.getInstances().size());
        xmlInstances.getInstances().forEach(xmlInstance -> instances.add(convertInstance(xmlInstance, instances)));
        if (!StringUtils.isEmpty(xmlInstances.getClassName())) {
            instances.setClass(instances.attribute(xmlInstances.getClassName()));
        }
        return instances;
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

    private Attribute convertAttribute(XmlAttribute xmlAttribute) {
        return xmlAttribute.getType().handle(new AttributeTypeVisitor<Attribute>() {
            @Override
            public Attribute caseNumeric() {
                return new Attribute(xmlAttribute.getName());
            }

            @Override
            public Attribute caseNominal() {
                return new Attribute(xmlAttribute.getName(), xmlAttribute.getValues());
            }

            @Override
            public Attribute caseDate() {
                return new Attribute(xmlAttribute.getName(), xmlAttribute.getDateFormat());
            }
        });
    }

    private Instance convertInstance(XmlInstance xmlInstance, Instances instances) {
        Instance instance = new DenseInstance(instances.numAttributes());
        instance.setDataset(instances);
        for (int j = 0; j < instances.numAttributes(); j++) {
            Attribute attribute = instances.attribute(j);
            String val = xmlInstance.getValues().get(j);
            if (StringUtils.isEmpty(val)) {
                instance.setValue(attribute, Utils.missingValue());
            } else if (attribute.isDate()) {
                try {
                    instance.setValue(attribute, attribute.parseDate(val));
                } catch (ParseException ex) {
                    throw new IllegalArgumentException(ex.getMessage());
                }
            } else if (attribute.isNumeric()) {
                instance.setValue(attribute, Double.valueOf(val));
            } else {
                instance.setValue(attribute, val);
            }
        }
        return instance;
    }
}
