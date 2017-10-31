/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.service;

import eca.dictionary.AttributesTypesDictionary;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.StackingClassifier;
import eca.statistics.AttributeStatistics;
import org.apache.commons.lang3.StringUtils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Classifier input options service.
 *
 * @author Roman Batygin
 */
public class ClassifierInputOptionsService {

    public static final String CLASSIFIER_INPUT_OPTIONS_TEXT = "Входные параметры классификатора";
    public static final String ATTRIBUTE_TEXT = "Атрибут:";
    public static final String ATTRIBUTE_TYPE_TEXT = "Тип:";
    public static final String MIN_VALUE_TEXT = "Минимальное значение:";
    public static final String MAX_VALUE_TEXT = "Максимальное значение:";
    public static final String MEAN_VALUE_TEXT = "Математическое ожидание:";
    public static final String VARIANCE_VALUE_TEXT = "Дисперсия:";
    public static final String STD_DEV_TEXT = "Среднеквадратическое отклонение:";
    public static final String NOMINAL_ATTR_VALUES_TEXT = "Значения:";
    public static final String NOMINAL_ATTR_CODE_TEXT = "Код:";
    public static final String NOMINAL_ATTR_VALUE_TEXT = "Значение:";

    public static final String SEPARATOR = System.getProperty("line.separator");
    public static final String INDIVIDUAL_CLASSIFIER_INPUT_OPTIONS_TEXT = "Входные параметры базовых классификаторов:";
    public static final String META_CLASSIFIER_INPUT_OPTIONS_TEXT = "Входные параметры мета-классификатора:";
    public static final String META_CLASSIFIER_TEXT = "Мета-классификатор:";
    public static final String INPUT_OPTIONS_TEXT = "Входные параметры:";
    public static final String INDIVIDUAL_CLASSIFIER_TEXT = "Базовый классификатор:";
    public static final String RELATION_NAME_TEXT = "Данные: ";
    public static final String OBJECTS_NUM_TEXT = "Число объектов: ";
    public static final String ATTRIBUTES_NUM_TEXT = "Число атрибутов: ";
    public static final String CLASSES_NUM_TEXT = "Число классов: ";

    /**
     * Returns classifier input options html string.
     *
     * @param classifier classifier object
     * @return classifier input options html string
     */
    public static String getInputOptionsInfoAsHtml(Classifier classifier) {
        StringBuilder info =
                new StringBuilder("<html>");
        info.append("<head><style>.attr {font-weight: bold;} th {font-size: 14;}</style></head>");
        info.append("<body>");
        info.append("<table>");
        info.append("<tr>");
        info.append("<th colspan = '2'>").append(CLASSIFIER_INPUT_OPTIONS_TEXT).append("</th>");
        info.append("</tr>");
        String[] option = ((AbstractClassifier) classifier).getOptions();
        for (int i = 0; i < option.length; i += 2) {
            info.append("<tr>");
            info.append("<td class = 'attr'>");
            info.append(option[i]);
            info.append("</td>");
            info.append("<td>");
            info.append(option[i + 1]);
            info.append("</td>");
            info.append("</tr>");
        }
        info.append("</table></body></html>");
        return info.toString();
    }

    /**
     * Returns attribute statistics string.
     *
     * @param a                   attribute
     * @param attributeStatistics {@link AttributeStatistics} object
     * @return attribute statistics string
     */
    public static StringBuilder getAttributeInfo(Attribute a, AttributeStatistics attributeStatistics) {
        StringBuilder attrInfo = new StringBuilder();
        attrInfo.append(ATTRIBUTE_TEXT).append(StringUtils.SPACE).append(StringUtils.SPACE)
                .append(a.name()).append(SEPARATOR);
        attrInfo.append(ATTRIBUTE_TYPE_TEXT).append(StringUtils.SPACE).append(StringUtils.SPACE);
        if (a.isNumeric()) {
            attrInfo.append(a.isDate() ? AttributesTypesDictionary.DATE
                    : AttributesTypesDictionary.NUMERIC).append(SEPARATOR);
            attrInfo.append(MIN_VALUE_TEXT).append(StringUtils.SPACE).append(StringUtils.SPACE)
                    .append(attributeStatistics.getMinAsString(a)).append(SEPARATOR);
            attrInfo.append(MAX_VALUE_TEXT).append(StringUtils.SPACE).append(StringUtils.SPACE)
                    .append(attributeStatistics.getMaxAsString(a)).append(SEPARATOR);
            attrInfo.append(MEAN_VALUE_TEXT).append(StringUtils.SPACE).append(StringUtils.SPACE)
                    .append(attributeStatistics.meanOrMode(a)).append(SEPARATOR);
            attrInfo.append(VARIANCE_VALUE_TEXT).append(StringUtils.SPACE).append(StringUtils.SPACE)
                    .append(attributeStatistics.variance(a)).append(SEPARATOR);
            attrInfo.append(STD_DEV_TEXT).append(StringUtils.SPACE).append(StringUtils.SPACE)
                    .append(attributeStatistics.stdDev(a)).append(SEPARATOR);
        } else {
            attrInfo.append(AttributesTypesDictionary.NOMINAL).append(SEPARATOR);
            attrInfo.append(NOMINAL_ATTR_VALUES_TEXT).append(SEPARATOR);
            for (int k = 0; k < a.numValues(); k++) {
                attrInfo.append(NOMINAL_ATTR_CODE_TEXT).append(k).append(",").append(StringUtils.SPACE)
                        .append(NOMINAL_ATTR_VALUE_TEXT).append(a.value(k)).append(SEPARATOR);
            }
        }
        return attrInfo;
    }

    /**
     * Returns all attributes statistics string.
     *
     * @param data                input data
     * @param attributeStatistics {@link AttributeStatistics} object
     * @return all attributes statistics string
     */
    public static String getAttributesInfo(Instances data, AttributeStatistics attributeStatistics) {
        StringBuilder attrInfo = new StringBuilder();
        for (int i = 0; i < data.numAttributes(); i++) {
            attrInfo.append(getAttributeInfo(data.attribute(i), attributeStatistics))
                    .append(SEPARATOR).append(SEPARATOR);
        }
        return attrInfo.toString();
    }

    /**
     * Returns classifier input options string.
     *
     * @param classifier classifier object
     * @return classifier input options string
     */
    public static String getInputOptionsInfo(Classifier classifier) {
        AbstractClassifier abstractClassifier = (AbstractClassifier) classifier;
        StringBuilder inputInfo = new StringBuilder();
        String[] options = abstractClassifier.getOptions();
        setOptions(inputInfo, options);
        if (abstractClassifier instanceof AbstractHeterogeneousClassifier) {
            inputInfo.append(SEPARATOR).append(INDIVIDUAL_CLASSIFIER_INPUT_OPTIONS_TEXT).append(SEPARATOR);
            AbstractHeterogeneousClassifier ens = (AbstractHeterogeneousClassifier) abstractClassifier;
            ClassifiersSet set = ens.getClassifiersSet();
            setOptionsForEnsemble(inputInfo, set.toList());
        }
        if (abstractClassifier instanceof StackingClassifier) {
            inputInfo.append(SEPARATOR).append(SEPARATOR).append(INDIVIDUAL_CLASSIFIER_INPUT_OPTIONS_TEXT)
                    .append(SEPARATOR);
            StackingClassifier ens = (StackingClassifier) abstractClassifier;
            setOptionsForEnsemble(inputInfo, ens.getClassifiers().toList());
            inputInfo.append(META_CLASSIFIER_INPUT_OPTIONS_TEXT);
            String[] metaOptions = ((AbstractClassifier) ens.getMetaClassifier()).getOptions();
            inputInfo.append(SEPARATOR).append(META_CLASSIFIER_TEXT)
                    .append(StringUtils.SPACE).append(StringUtils.SPACE)
                    .append(ens.getMetaClassifier().getClass().getSimpleName())
                    .append(SEPARATOR).append(INPUT_OPTIONS_TEXT).append(SEPARATOR);
            setOptions(inputInfo, metaOptions);
        }
        return inputInfo.toString();
    }

    /**
     * Returns attribute statistics html string.
     *
     * @param a                   attribute
     * @param attributeStatistics {@link AttributeStatistics} object
     * @return attribute statistics html string
     */
    public static String getAttributeInfoAsHtml(Attribute a, AttributeStatistics attributeStatistics) {
        StringBuilder info = new StringBuilder("<html><head><style>.attr {font-weight: bold;}</style></head><body>");
        info.append("<table><tr>");
        info.append("<td class = 'attr'>").append(ATTRIBUTE_TEXT).append("</td>")
                .append("<td>").append(a.name()).append("</td>");
        info.append("</tr><tr>");
        info.append("<td class = 'attr'>").append(ATTRIBUTE_TYPE_TEXT).append("</td>");
        if (a.isNumeric()) {
            info.append("<td>").append(a.isDate() ? AttributesTypesDictionary.DATE
                    : AttributesTypesDictionary.NUMERIC).append("</td>");
            info.append("</tr><tr>");
            info.append("<td class = 'attr'>").append(MIN_VALUE_TEXT)
                    .append("</td>").append("<td>").append(attributeStatistics.getMinAsString(a)).append("</td>");
            info.append("</tr><tr>");
            info.append("<td class = 'attr'>").append(MAX_VALUE_TEXT).append("</td>")
                    .append("<td>").append(attributeStatistics.getMaxAsString(a)).append("</td>");
            info.append("</tr><tr>");
            info.append("<td class = 'attr'>").append(MEAN_VALUE_TEXT).append("</td>")
                    .append("<td>").append(attributeStatistics.meanOrMode(a)).append("</td>");
            info.append("</tr><tr>");
            info.append("<td class = 'attr'>").append(VARIANCE_VALUE_TEXT).append("</td>")
                    .append("<td>").append(attributeStatistics.variance(a)).append("</td>");
            info.append("</tr><tr>");
            info.append("<td class = 'attr'>").append(STD_DEV_TEXT).append("</td>")
                    .append("<td>").append(attributeStatistics.stdDev(a)).append("</td>");
            info.append("</tr>");
        } else {
            info.append("<td>").append(AttributesTypesDictionary.NOMINAL).append("</td>");
            info.append("</tr><tr>");
            info.append("<td class = 'attr' colspan = '2' style = 'text-align: center;'>")
                    .append(NOMINAL_ATTR_VALUES_TEXT).append("</td>");
            info.append("</tr>");
            for (int k = 0; k < a.numValues(); k++) {
                info.append("<tr>");
                info.append("<td>").append(NOMINAL_ATTR_CODE_TEXT).append(StringUtils.SPACE)
                        .append(k).append("</td>");
                info.append("<td>").append("|").append(NOMINAL_ATTR_VALUE_TEXT)
                        .append(StringUtils.SPACE).append(a.value(k)).append("</td>");
                info.append("</tr>");
            }
        }
        info.append("</table></body></html>");

        return info.toString();
    }

    /**
     * Returns instances info string.
     *
     * @param data {@link Instances} object
     * @return instances info string
     */
    public static String getInstancesInfo(Instances data) {
        StringBuilder str = new StringBuilder();
        str.append(RELATION_NAME_TEXT).append(data.relationName()).append(SEPARATOR);
        str.append(OBJECTS_NUM_TEXT).append(data.numInstances()).append(SEPARATOR);
        str.append(ATTRIBUTES_NUM_TEXT).append(data.numAttributes()).append(SEPARATOR);
        str.append(CLASSES_NUM_TEXT).append(data.numClasses()).append(SEPARATOR);
        return str.toString();
    }

    private static void setOptions(StringBuilder info, String[] options) {
        for (int i = 0; i < options.length; i += 2) {
            info.append(options[i]).append(StringUtils.SPACE).append(StringUtils.SPACE)
                    .append(StringUtils.SPACE).append(options[i + 1]).append(SEPARATOR);
        }
    }

    private static void setOptionsForEnsemble(StringBuilder info, ArrayList<Classifier> set) {
        for (int i = 0; i < set.size(); i++) {
            AbstractClassifier single = (AbstractClassifier) set.get(i);
            info.append(INDIVIDUAL_CLASSIFIER_TEXT).append(StringUtils.SPACE).append(StringUtils.SPACE)
                    .append(single.getClass().getSimpleName()).append(SEPARATOR);
            info.append(INPUT_OPTIONS_TEXT).append(SEPARATOR);
            String[] singleOptions = single.getOptions();
            setOptions(info, singleOptions);
            info.append(SEPARATOR);
        }
    }
}
