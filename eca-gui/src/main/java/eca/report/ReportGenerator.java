/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.report;

import eca.config.VelocityConfigService;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import eca.dictionary.AttributesTypesDictionary;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.EnsembleUtils;
import eca.ensemble.StackingClassifier;
import eca.report.contingency.ContingencyTableReportModel;
import eca.statistics.AttributeStatistics;
import eca.statistics.contingency.ChiSquareTestResult;
import eca.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import static eca.util.VelocityUtils.mergeContext;

/**
 * Implements reports generation.
 *
 * @author Roman Batygin
 */
public class ReportGenerator {

    /**
     * VM templates paths
     */
    private static final String ATTRIBUTE_STATISTICS_VM = "vm-templates/attributeStatistics.vm";
    private static final String CLASSIFIER_INPUT_OPTIONS_VM = "vm-templates/classifierInputOptions.vm";
    private static final String EXPERIMENT_RESULTS_VM = "vm-templates/experimentResults.vm";
    private static final String CHI_SQUARE_TEST_VM = "vm-templates/chiSquaredTestResult.vm";

    /**
     * Velocity context variables
     */
    private static final String ATTR_NAME = "name";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_TYPE_DESCRIPTION = "typeDescription";
    private static final String ATTR_MIN_VALUE = "minValue";
    private static final String ATTR_MAX_VALUE = "maxValue";
    private static final String ATTR_MEAN_VALUE = "meanValue";
    private static final String ATTR_VARIANCE_VALUE = "varianceValue";
    private static final String ATTR_STD_DEV_VALUE = "stdDevValue";
    private static final String ATTRIBUTE_VALUES = "attributeValues";
    private static final String OPTIONS_MAP = "optionsMap";
    private static final String CLASSIFIERS_OPTIONS = "classifiersOptions";
    private static final String EXTENDED_OPTIONS = "extendedOptions";
    private static final String RELATION_NAME = "relationName";
    private static final String NUM_INSTANCES = "numInstances";
    private static final String NUM_ATTRIBUTES = "numAttributes";
    private static final String NUM_CLASSES = "numClasses";
    private static final String CLASS_ATTRIBUTE = "classAttribute";
    private static final String EVALUATION_METHOD = "evaluationMethod";
    private static final String CH_VAL = "chiVal";
    private static final String CHI_CRITICAL_VALUE = "chiCriticalValue";
    private static final String DF = "df";
    private static final String ALPHA = "alpha";
    private static final String SIGNIFICANT = "significant";
    private static final String ROW_ATTR_NAME = "rowAttrName";
    private static final String COL_ATTR_NAME = "colAttrName";

    /**
     * Other constants
     */
    private static final String CLASSIFIER_KEY_FORMAT = "%s №%d";
    private static final String META_CLASSIFIER_FORMAT = "Мета - классификатор: %s";
    private static final String TRAINING_DATA_METHOD_TEXT = "Использование обучающей выборки";
    private static final String CROSS_VALIDATION_METHOD_FORMAT = "Кросс - проверка, %s%d - блочная";
    private static final String KV_CROSS_VALIDATION = "k * V блочная кросс - проверка";


    /**
     * Returns attribute statistics html string.
     *
     * @param attribute           - attribute
     * @param attributeStatistics - attribute statistics object
     * @return attribute statistics html string
     */
    public static String getAttributeStatisticsAsHtml(Attribute attribute, AttributeStatistics attributeStatistics) {
        Template template = VelocityConfigService.getTemplate(ATTRIBUTE_STATISTICS_VM);
        VelocityContext context = new VelocityContext();
        context.put(ATTR_NAME, attribute.name());
        context.put(ATTR_TYPE, attribute.isNumeric());
        context.put(ATTR_TYPE_DESCRIPTION, AttributesTypesDictionary.getType(attribute));
        if (attribute.isNumeric()) {
            context.put(ATTR_MIN_VALUE, attributeStatistics.getMinAsString(attribute));
            context.put(ATTR_MAX_VALUE, attributeStatistics.getMaxAsString(attribute));
            context.put(ATTR_MEAN_VALUE, attributeStatistics.meanOrMode(attribute));
            context.put(ATTR_VARIANCE_VALUE, attributeStatistics.variance(attribute));
            context.put(ATTR_STD_DEV_VALUE, attributeStatistics.stdDev(attribute));

        } else {
            context.put(ATTRIBUTE_VALUES, Utils.getAttributeValues(attribute));
        }
        return mergeContext(template, context);
    }

    /**
     * Returns classifier input options html string.
     *
     * @param inputOptions - classifier input options map
     * @return classifier input options html string
     */
    public static String getClassifierInputOptionsAsHtml(Map<String, String> inputOptions) {
        Template template = VelocityConfigService.getTemplate(CLASSIFIER_INPUT_OPTIONS_VM);
        VelocityContext context = new VelocityContext();
        context.put(OPTIONS_MAP, inputOptions);
        return mergeContext(template, context);
    }

    /**
     * Returns classifier input options html string.
     *
     * @param classifier classifier object
     * @param extended   extended options info for ensemble algorithms
     * @return classifier input options html string
     */
    public static String getClassifierInputOptionsAsHtml(Classifier classifier, boolean extended) {
        Template template = VelocityConfigService.getTemplate(CLASSIFIER_INPUT_OPTIONS_VM);
        VelocityContext context = new VelocityContext();
        context.put(OPTIONS_MAP, Utils.getClassifierInputOptionsMap((AbstractClassifier) classifier));
        boolean canHandleExtendedOptions = canHandleExtendedOptions(classifier, extended);
        context.put(EXTENDED_OPTIONS, canHandleExtendedOptions);
        if (canHandleExtendedOptions) {
            if (classifier instanceof AbstractHeterogeneousClassifier) {
                AbstractHeterogeneousClassifier heterogeneousClassifier = (AbstractHeterogeneousClassifier) classifier;
                context.put(CLASSIFIERS_OPTIONS,
                        getClassifiersOptions(heterogeneousClassifier.getClassifiersSet()));
            }
            if (classifier instanceof StackingClassifier) {
                StackingClassifier stackingClassifier = (StackingClassifier) classifier;
                fillStackingExtendedOptions(stackingClassifier, context);
            }
        }
        return mergeContext(template, context);
    }

    /**
     * Returns chi-square test results as html.
     *
     * @param contingencyTableReportModel - contingency table report model
     * @return chi-square test results as html
     */
    public static String getChiSquareTestResultAsHtml(ContingencyTableReportModel contingencyTableReportModel) {
        Template template = VelocityConfigService.getTemplate(CHI_SQUARE_TEST_VM);
        VelocityContext context = new VelocityContext();
        DecimalFormat decimalFormat = contingencyTableReportModel.getDecimalFormat();
        ChiSquareTestResult chiSquareTestResult = contingencyTableReportModel.getChiSquareTestResult();
        context.put(ROW_ATTR_NAME, contingencyTableReportModel.getRowAttribute().name());
        context.put(COL_ATTR_NAME, contingencyTableReportModel.getColAttribute().name());
        context.put(CH_VAL, decimalFormat.format(chiSquareTestResult.getChiSquaredValue()));
        context.put(CHI_CRITICAL_VALUE, decimalFormat.format(chiSquareTestResult.getChiSquaredCriticalValue()));
        context.put(DF, chiSquareTestResult.getDf());
        context.put(ALPHA, decimalFormat.format(chiSquareTestResult.getAlpha()));
        context.put(SIGNIFICANT, chiSquareTestResult.isSignificant());
        return mergeContext(template, context);
    }

    /**
     * Returns experiment results as html string.
     *
     * @param experimentHistory - experiment history
     * @param resultsSize       - results size
     * @return experiment results as html string
     */
    public static String getExperimentResultsAsHtml(ExperimentHistory experimentHistory, int resultsSize) {
        Template template = VelocityConfigService.getTemplate(EXPERIMENT_RESULTS_VM);
        VelocityContext context = new VelocityContext();
        fillExperimentInputOptions(experimentHistory, context);
        fillExperimentBestClassifiers(experimentHistory, resultsSize, context);
        return mergeContext(template, context);
    }

    private static void fillStackingExtendedOptions(StackingClassifier stackingClassifier, VelocityContext context) {
        Map<String, Map<String, String>> inputOptionsMap = getClassifiersOptions(stackingClassifier.getClassifiers());
        inputOptionsMap.put(String.format(META_CLASSIFIER_FORMAT,
                stackingClassifier.getMetaClassifier().getClass().getSimpleName()),
                Utils.getClassifierInputOptionsMap((AbstractClassifier) stackingClassifier.getMetaClassifier()));
        context.put(CLASSIFIERS_OPTIONS, inputOptionsMap);
    }

    private static Map<String, Map<String, String>> getClassifiersOptions(ClassifiersSet classifiers) {
        LinkedHashMap<String, Map<String, String>> allOptionsMap = new LinkedHashMap<>();
        for (int i = 0; i < classifiers.size(); i++) {
            Classifier currentClassifier = classifiers.getClassifier(i);
            allOptionsMap.put(String.format(CLASSIFIER_KEY_FORMAT, currentClassifier.getClass().getSimpleName(), i),
                    Utils.getClassifierInputOptionsMap((AbstractClassifier) currentClassifier));
        }
        return allOptionsMap;
    }

    private static void fillExperimentBestClassifiers(ExperimentHistory experimentHistory, int resultsSize,
                                                      VelocityContext velocityContext) {
        LinkedHashMap<String, Map<String, String>> allOptionsMap = new LinkedHashMap<>();
        for (int i = 0; i < Integer.min(experimentHistory.getExperiment().size(), resultsSize); i++) {
            EvaluationResults evaluationResults = experimentHistory.getExperiment().get(i);
            Classifier classifier = evaluationResults.getClassifier();
            allOptionsMap.put(String.format(CLASSIFIER_KEY_FORMAT, classifier.getClass().getSimpleName(), i),
                    Utils.getClassifierInputOptionsMap((AbstractClassifier) classifier));
        }
        velocityContext.put(CLASSIFIERS_OPTIONS, allOptionsMap);
    }

    private static void fillExperimentInputOptions(ExperimentHistory experimentHistory,
                                                   VelocityContext velocityContext) {
        velocityContext.put(RELATION_NAME, experimentHistory.getDataSet().relationName());
        velocityContext.put(NUM_INSTANCES, experimentHistory.getDataSet().numInstances());
        velocityContext.put(NUM_ATTRIBUTES, experimentHistory.getDataSet().numAttributes());
        velocityContext.put(NUM_CLASSES, experimentHistory.getDataSet().numClasses());
        velocityContext.put(CLASS_ATTRIBUTE, experimentHistory.getDataSet().classAttribute().name());

        switch (experimentHistory.getEvaluationMethod()) {
            case TRAINING_DATA:
                velocityContext.put(EVALUATION_METHOD, TRAINING_DATA_METHOD_TEXT);
                break;
            case CROSS_VALIDATION:
                if (experimentHistory.getEvaluationParams() == null) {
                    velocityContext.put(EVALUATION_METHOD, KV_CROSS_VALIDATION);
                } else {
                    String evaluationMethod = String.format(CROSS_VALIDATION_METHOD_FORMAT,
                            (experimentHistory.getEvaluationParams().getNumTests() > 1 ?
                                    experimentHistory.getEvaluationParams().getNumTests() + "*" : StringUtils.EMPTY),
                            experimentHistory.getEvaluationParams().getNumFolds());
                    velocityContext.put(EVALUATION_METHOD, evaluationMethod);
                }
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Unexpected evaluation method [%s]!", experimentHistory.getEvaluationMethod()));
        }

    }

    private static boolean canHandleExtendedOptions(Classifier classifier, boolean extended) {
        return extended && EnsembleUtils.isHeterogeneousEnsembleClassifier(classifier);
    }
}
