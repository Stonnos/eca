package eca.statistics.diagram;

import eca.statistics.AttributeStatistics;
import eca.util.FrequencyUtils;
import eca.util.IntervalUtils;
import org.springframework.util.Assert;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Frequency diagram builder.
 *
 * @author Roman Batygin
 */

public class FrequencyDiagramBuilder {

    private static final List<FrequencyIntervalsTable.Intervals> RECOMMENDED_INTERVALS =
            FrequencyIntervalsTable.getFrequencyIntervalsTable().getIntervals();

    private AttributeStatistics attributeStatistics;

    /**
     * Creates <code>FrequencyDiagramBuilder</code> object.
     *
     * @param attributeStatistics {@link AttributeStatistics} object
     */
    public FrequencyDiagramBuilder(AttributeStatistics attributeStatistics) {
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
        Assert.notNull(attribute, "Attribute is not specified!");
        Assert.isTrue(attribute.isNumeric(), "Attribute must be numeric!");
        int intervalsNum = calculateRecommendedIntervals();
        if (intervalsNum > 0) {
            List<FrequencyData> frequencyModelList = new ArrayList<>(intervalsNum);
            double minAttrValue = getData().kthSmallestValue(attribute, 1);
            double maxAttrValue = getData().kthSmallestValue(attribute, getData().numInstances());
            double delta = (maxAttrValue - minAttrValue) / intervalsNum;
            FrequencyData first = createFirstFrequency(minAttrValue, minAttrValue + delta, attribute);
            frequencyModelList.add(first);
            for (int i = 1; i < intervalsNum; i++) {
                FrequencyData frequencyData = new FrequencyData();
                frequencyData.setLowerBound(first.getUpperBound());
                frequencyData.setUpperBound(first.getUpperBound() + delta);
                frequencyData.setNumValues(FrequencyUtils.calculateFrequency(getData(), attribute, frequencyData));
                frequencyModelList.add(frequencyData);
                first = frequencyData;
            }
            return frequencyModelList;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Calculates the frequency diagram data for nominal attribute.
     *
     * @param attribute {@link Attribute} object
     * @return frequency diagram data {@link List}
     */
    public List<FrequencyData> calculateFrequencyDiagramDataForNominalAttribute(Attribute attribute) {
        Assert.notNull(attribute, "Attribute is not specified!");
        Assert.isTrue(attribute.isNominal(), "Attribute must be nominal!");
        List<FrequencyData> frequencyModelList = new ArrayList<>(attribute.numValues());
        for (int i = 0; i < attribute.numValues(); i++) {
            FrequencyData frequencyData = new FrequencyData();
            frequencyData.setLowerBound(i);
            frequencyData.setUpperBound(i);
            frequencyData.setNumValues(attributeStatistics.getValuesNum(attribute, i));
            frequencyModelList.add(frequencyData);
        }
        return frequencyModelList;
    }

    private FrequencyData createFirstFrequency(double lowerBound, double upperBound, Attribute attribute) {
        FrequencyData frequencyData = new FrequencyData();
        frequencyData.setLowerBound(lowerBound);
        frequencyData.setUpperBound(upperBound);
        frequencyData.setNumValues(FrequencyUtils.calculateFirstFrequency(getData(), attribute, frequencyData));
        return frequencyData;
    }

    private int calculateRecommendedIntervals() {
        int intervalsNum = 0;
        if (getData().numInstances() <= FrequencyIntervalsTable.MIN_SAMPLE_SIZE) {
            intervalsNum = FrequencyIntervalsTable.MIN_INTERVALS_NUM;
        } else if (getData().numInstances() > FrequencyIntervalsTable.MAX_SAMPLE_SIZE) {
            intervalsNum = FrequencyIntervalsTable.MAX_INTERVALS_NUM;
        } else {
            for (FrequencyIntervalsTable.Intervals intervals : RECOMMENDED_INTERVALS) {
                if (IntervalUtils.containsValueIncludeRightBound(intervals.getSampleSizeInterval(),
                        getData().numInstances())) {
                    IntervalData intervalData = intervals.getIntervalsNum();
                    intervalsNum = (int) (intervalData.getLowerBound() + intervalData.getUpperBound()) / 2;
                    break;
                }
            }
        }
        return intervalsNum;
    }

}
