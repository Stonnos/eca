package eca.data.file.converter;

import com.google.common.math.DoubleMath;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

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
        if (instancesModel.getAttributes() == null || instancesModel.getAttributes().isEmpty()) {
            throw new IllegalStateException("Empty attributes list!");
        }
        ArrayList<Attribute> attributes = instancesModel.getAttributes()
                .stream()
                .map(this::convertAttribute)
                .collect(Collectors.toCollection(ArrayList::new));
        List<InstanceModel> instanceModels = Optional.ofNullable(instancesModel.getInstances()).orElse(newArrayList());
        Instances instances =
                new Instances(instancesModel.getRelationName(), attributes, instanceModels.size());
        instanceModels.forEach(instanceModel -> instances.add(convertInstance(instanceModel, instances)));
        if (!StringUtils.isEmpty(instancesModel.getClassName())) {
            if (attributes.stream().noneMatch(attribute -> attribute.name().equals(instancesModel.getClassName()))) {
                throw new IllegalStateException(
                        String.format("Class attribute [%s] not found in instances [%s] attributes set",
                                instancesModel.getClassName(), instances.relationName()));
            }
            Attribute classAttribute = instances.attribute(instancesModel.getClassName());
            instances.setClass(classAttribute);
        } else {
            instances.setClassIndex(instances.numAttributes() - 1);
        }
        return instances;
    }

    private InstanceModel convertInstance(Instance instance) {
        InstanceModel instanceModel = new InstanceModel();
        instanceModel.setValues(newArrayList());
        for (int i = 0; i < instance.numAttributes(); i++) {
            Double value = !instance.isMissing(i) ? instance.value(i) : null;
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
                attributeModel.setValues(newArrayList());
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
        if (StringUtils.isBlank(attributeModel.getName())) {
            throw new IllegalStateException("Attribute name must be not blank!");
        }
        if (attributeModel.getType() == null) {
            throw new IllegalStateException("Attribute type must be not null!");
        }
        return attributeModel.getType().handle(new AttributeTypeVisitor<>() {
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
        if (instanceModel.getValues() == null || instanceModel.getValues().size() != instances.numAttributes()) {
            String errorMessage =
                    String.format("Values list must contains [%d] values! Actual is [%d]", instances.numAttributes(),
                            Optional.ofNullable(instanceModel.getValues()).map(List::size).orElse(0));
            throw new IllegalStateException(errorMessage);
        }
        Instance instance = new DenseInstance(instances.numAttributes());
        instance.setDataset(instances);
        for (int j = 0; j < instances.numAttributes(); j++) {
            Attribute attribute = instances.attribute(j);
            Double val = instanceModel.getValues().get(j);
            if (attribute.isNominal() && val != null && !DoubleMath.isMathematicalInteger(val)) {
                String errorMessage =
                        String.format("Invalid value %s. Nominal attribute [%s] code must be integer", val,
                                attribute.name());
                throw new IllegalStateException(errorMessage);
            }
            if (attribute.isNominal() && val != null && (val < 0 || val > attribute.numValues())) {
                String errorMessage =
                        String.format("Invalid value %s. Nominal attribute [%s] code must in interval [%d, %d]", val,
                                attribute.name(), 0, attribute.numValues() - 1);
                throw new IllegalStateException(errorMessage);
            }
            instance.setValue(attribute, Objects.requireNonNullElseGet(val, Utils::missingValue));
        }
        return instance;
    }
}
