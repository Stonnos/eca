package eca.data.file.converter;

import eca.data.file.model.AttributeModel;
import eca.data.file.model.AttributeType;
import eca.data.file.model.AttributeTypeVisitor;
import eca.data.file.model.InstanceModel;
import eca.data.file.model.InstancesModel;
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
public class InstancesConverter {

    /**
     * Converts instances into xml model.
     *
     * @param data - instances
     * @return xml instances
     */
    public InstancesModel convert(Instances data) {
        InstancesModel instancesModel = new InstancesModel();
        instancesModel.setRelationName(data.relationName());
        if (data.classIndex() >= 0) {
            instancesModel.setClassName(data.classAttribute().name());
        }
        instancesModel.setAttributes(convertAttributes(data));
        instancesModel.setInstances(data.stream().map(this::convertInstance).collect(Collectors.toList()));
        return instancesModel;
    }

    /**
     * Converts xml instances into instances model.
     *
     * @param instancesModel - xml instances
     * @return instances
     */
    public Instances convert(InstancesModel instancesModel) {
        ArrayList<Attribute> attributes = instancesModel.getAttributes()
                .stream()
                .map(this::convertAttribute)
                .collect(Collectors.toCollection(ArrayList::new));
        Instances instances =
                new Instances(instancesModel.getRelationName(), attributes, instancesModel.getInstances().size());
        instancesModel.getInstances().forEach(
                instanceModel -> instances.add(convertInstance(instanceModel, instances)));
        if (!StringUtils.isEmpty(instancesModel.getClassName())) {
            if (attributes.stream().noneMatch(attribute -> attribute.name().equals(instancesModel.getClassName()))) {
                throw new IllegalStateException(
                        String.format("Class attribute [%s] not found in instances [%s] attributes set",
                                instancesModel.getClassName(), instances.relationName()));
            }
            Attribute classAttribute = instances.attribute(instancesModel.getClassName());
            if (classAttribute.isNumeric()) {
                throw new IllegalStateException(String.format("Class attribute [%s] must be nominal for instances [%s]",
                        instancesModel.getClassName(), instances.relationName()));
            }
            instances.setClass(classAttribute);
        }
        return instances;
    }

    private InstanceModel convertInstance(Instance instance) {
        InstanceModel instanceModel = new InstanceModel();
        instanceModel.setValues(new ArrayList<>());
        for (int i = 0; i < instance.numAttributes(); i++) {
            String value = StringUtils.EMPTY;
            if (!instance.isMissing(i)) {
                Attribute attribute = instance.attribute(i);
                switch (attribute.type()) {
                    case Attribute.DATE:
                    case Attribute.NOMINAL:
                        value = instance.stringValue(i);
                        break;
                    case Attribute.NUMERIC:
                        value = String.valueOf(instance.value(i));
                        break;
                    default:
                        throw new IllegalArgumentException(
                                String.format("Unexpected attribute [%s] type: %d!", attribute.name(),
                                        attribute.type()));
                }
            }
            instanceModel.getValues().add(value);
        }
        return instanceModel;
    }

    private List<AttributeModel> convertAttributes(Instances data) {
        List<AttributeModel> attributeModels = new ArrayList<>();
        for (int i = 0; i < data.numAttributes(); i++) {
            attributeModels.add(convertAttribute(data.attribute(i)));
        }
        return attributeModels;
    }

    private AttributeModel convertAttribute(Attribute attribute) {
        AttributeModel attributeModel = new AttributeModel();
        attributeModel.setName(attribute.name());
        switch (attribute.type()) {
            case Attribute.DATE:
                attributeModel.setType(AttributeType.DATE);
                attributeModel.setDateFormat(attribute.getDateFormat());
                break;
            case Attribute.NUMERIC:
                attributeModel.setType(AttributeType.NUMERIC);
                break;
            case Attribute.NOMINAL:
                attributeModel.setType(AttributeType.NOMINAL);
                attributeModel.setValues(new ArrayList<>());
                for (int i = 0; i < attribute.numValues(); i++) {
                    attributeModel.getValues().add(attribute.value(i));
                }
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Unexpected attribute [%s] type: %d!", attribute.name(), attribute.type()));
        }
        return attributeModel;
    }

    private Attribute convertAttribute(AttributeModel attributeModel) {
        return attributeModel.getType().handle(new AttributeTypeVisitor<Attribute>() {
            @Override
            public Attribute caseNumeric() {
                return new Attribute(attributeModel.getName());
            }

            @Override
            public Attribute caseNominal() {
                return new Attribute(attributeModel.getName(), attributeModel.getValues());
            }

            @Override
            public Attribute caseDate() {
                return new Attribute(attributeModel.getName(), attributeModel.getDateFormat());
            }
        });
    }

    private Instance convertInstance(InstanceModel instanceModel, Instances instances) {
        Instance instance = new DenseInstance(instances.numAttributes());
        instance.setDataset(instances);
        for (int j = 0; j < instances.numAttributes(); j++) {
            Attribute attribute = instances.attribute(j);
            String val = instanceModel.getValues().get(j);
            if (StringUtils.isEmpty(val)) {
                instance.setValue(attribute, Utils.missingValue());
            } else if (attribute.isDate()) {
                try {
                    instance.setValue(attribute, attribute.parseDate(val));
                } catch (ParseException ex) {
                    throw new IllegalArgumentException(ex.getMessage());
                }
            } else if (attribute.isNumeric()) {
                instance.setValue(attribute, Double.parseDouble(val));
            } else {
                instance.setValue(attribute, val);
            }
        }
        return instance;
    }
}
