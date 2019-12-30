package eca.gui.frames.results.provider;

import eca.config.ApplicationConfig;
import eca.config.ConfigurationService;
import eca.gui.frames.results.model.ComponentModel;
import eca.gui.tables.LogisticCoefficientsTable;
import eca.gui.tables.SignificantAttributesTable;
import eca.regression.Logistic;
import eca.roc.AttributesSelection;
import weka.core.Instances;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class LogisticComponentsProvider extends EvaluationResultsComponentsProvider<Logistic> {

    private static final String LOGISTIC_COEFFICIENTS_TAB_TITLE = "Оценки коэффициентов";
    private static final String SIGNIFICANT_ATTRIBUTES_TAB_TITLE = "Значимые атрибуты";

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();
    private static final ApplicationConfig APPLICATION_CONFIG = CONFIG_SERVICE.getApplicationConfig();

    public LogisticComponentsProvider() {
        super(Logistic.class);
    }

    @Override
    public List<ComponentModel> getComponents(Logistic classifier,
                                              Instances data,
                                              int maxFractionDigits,
                                              JFrame parent) throws Exception {
        ComponentModel coefficientsModel = createLogisticCoefficientsModel(classifier, data, maxFractionDigits);
        ComponentModel signAttributesModel = createSignAttributesModel(data, maxFractionDigits);
        return Arrays.asList(coefficientsModel, signAttributesModel);
    }

    private ComponentModel createLogisticCoefficientsModel(Logistic classifier, Instances data, int maxFractionDigits)
            throws Exception {
        LogisticCoefficientsTable logisticCoefficientsTable =
                new LogisticCoefficientsTable(classifier, data, maxFractionDigits);
        JScrollPane coefficientsPane = new JScrollPane(logisticCoefficientsTable);
        return new ComponentModel(LOGISTIC_COEFFICIENTS_TAB_TITLE, coefficientsPane);
    }

    private ComponentModel createSignAttributesModel(Instances data, int maxFractionDigits) throws Exception {
        AttributesSelection attributesSelection = new AttributesSelection(data);
        attributesSelection.setAucThresholdValue(APPLICATION_CONFIG.getAucThresholdValue());
        attributesSelection.calculate();
        SignificantAttributesTable signTable
                = new SignificantAttributesTable(attributesSelection, maxFractionDigits);
        JScrollPane signAttributesPane = new JScrollPane(signTable);
        return new ComponentModel(SIGNIFICANT_ATTRIBUTES_TAB_TITLE, signAttributesPane);
    }
}
