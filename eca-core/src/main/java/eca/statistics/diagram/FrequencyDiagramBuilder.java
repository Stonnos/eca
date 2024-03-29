package eca.statistics.diagram;

import eca.statistics.AttributeStatistics;
import eca.util.FrequencyUtils;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Frequency diagram builder.
 *
 * @author Roman Batygin
 */

public class FrequencyDiagramBuilder {

    private AttributeStatistics attributeStatistics;

    /**
     * Creates <code>FrequencyDiagramBuilder</code> object.
     *
     * @param attributeStatistics {@link AttributeStatistics} object
     */
    public FrequencyDiagramBuilder(AttributeStatistics attributeStatistics) {
        Objects.requireNonNull(attributeStatistics, "Attribute statistics is not specified!");
        this.attributeStatistics = attributeStatistics;
    }

    /**
     * Returns attributes statistics object.
     *
     * @return {@link AttributeStatistics} object
     */
    public AttributeStatistics getAttributeStatistics() {
        return attributeStatistics;
    }

    /**
     * Returns sample object.
     *
     * @return {@link Instances} object
     */
    public Instances getData() {
        return attributeStatistics.getData();
    }

    /**
     * Sets the sample object.
     *
     * @param data {@link Instances} object
     */
    public void setData(Instances data) {
        attributeStatistics.setData(data);
    }

    /**
     * Calculates the frequency diagram data for numeric attribute.
     *
     * @param attribute {@link Attribute} object
     * @return frequency diagram data {@link List}
     */
    public List<FrequencyData> calculateFrequencyDiagramDataForNumericAttribute(Attribute attribute) {
        Objects.requireNonNull(attribute, "Attribute is not specified!");
        if (!attribute.isNumeric()) {
            throw new IllegalArgumentException("Attribute must be numeric!");
        }
        int intervalsNum = FrequencyUtils.stigessFormula(getData().numInstances());
        List<FrequencyData> frequencyModelList = new ArrayList<>(intervalsNum);
        double minAttrValue = attributeStatistics.getMin(attribute);
        double maxAttrValue = attributeStatistics.getMax(attribute);
        double delta = (maxAttrValue - minAttrValue) / intervalsNum;
        FrequencyData first = createFirstFrequency(minAttrValue, minAttrValue + delta, attribute);
        frequencyModelList.add(first);
        for (int i = 2; i <= intervalsNum; i++) {
            FrequencyData frequencyData = new FrequencyData();
            frequencyData.setLowerBound(minAttrValue + (i - 1) * delta);
            frequencyData.setUpperBound(minAttrValue + i * delta);
            frequencyData.setNumeric(true);
            FrequencyUtils.calculateFrequency(getData(), attribute, frequencyData);
            frequencyModelList.add(frequencyData);
        }
        return frequencyModelList;
    }

    /**
     * Calculates the frequency diagram data for nominal attribute.
     *
     * @param attribute {@link Attribute} object
     * @return frequency diagram data {@link List}
     */
    public List<FrequencyData> calculateFrequencyDiagramDataForNominalAttribute(Attribute attribute) {
        Objects.requireNonNull(attribute, "Attribute is not specified!");
        if (!attribute.isNominal()) {
            throw new IllegalArgumentException("Attribute must be nominal!");
        }
        List<FrequencyData> frequencyModelList = new ArrayList<>(attribute.numValues());
        for (int i = 0; i < attribute.numValues(); i++) {
            FrequencyData frequencyData = new FrequencyData(i, i, false);
            frequencyData.setFrequency(attributeStatistics.getValuesNum(attribute, i));
            frequencyModelList.add(frequencyData);
        }
        return frequencyModelList;
    }

    private FrequencyData createFirstFrequency(double lowerBound, double upperBound, Attribute attribute) {
        FrequencyData frequencyData = new FrequencyData(lowerBound, upperBound, true);
        FrequencyUtils.calculateFirstFrequency(getData(), attribute, frequencyData);
        return frequencyData;
    }

}
