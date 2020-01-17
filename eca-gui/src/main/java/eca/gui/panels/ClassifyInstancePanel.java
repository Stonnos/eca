/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.panels;

import eca.config.VelocityConfigService;
import eca.gui.PanelBorderUtils;
import eca.report.ReportGenerator;
import eca.gui.tables.ClassifyInstanceTable;
import eca.statistics.AttributeStatistics;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import weka.classifiers.Classifier;
import weka.core.Instance;

import javax.swing.*;
import java.awt.*;
import java.io.StringWriter;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

/**
 * @author Roman Batygin
 */
@Slf4j
public class ClassifyInstancePanel extends JPanel {

    private static final String VM_TEMPLATES_CLASSIFY_OBJECT_VM = "vm-templates/classifyObject.vm";

    private static final String CLASSIFY_INFO = "Классифицировать новый пример";
    private static final String RESET_INFO = "Сбросить все значения в таблице";
    private static final String CLASSIFY_INSTANCE_TITLE = "Классификация примера";
    private static final String CLASSIFY_BUTTON_TEXT = "Классифицировать";
    private static final String RESET_BUTTON_TEXT = "Сброс значений";
    private static final String CLASS_NAME_FORMAT = "Значение класса %s";
    private static final Dimension SCROLL_PANE_PREFERRED_SIZE = new Dimension(400, 300);
    private static final String CLASS_INDEX = "classIndex";
    private static final String CLASS_VALUE = "classValue";
    private static final String CLASS_PROBABILITY = "classProbability";
    private static final String CONTENT_TYPE = "text/html";

    private Classifier classifier;
    private final ClassifyInstanceTable classifyInstanceTable;

    private Template template;
    private VelocityContext velocityContext;

    public ClassifyInstancePanel(ClassifyInstanceTable table, Classifier classifier) {
        this.setClassifier(classifier);
        this.classifyInstanceTable = table;
        this.setLayout(new GridBagLayout());
        this.setBorder(PanelBorderUtils.createTitledBorder(CLASSIFY_INSTANCE_TITLE));
        this.createGUI();
    }

    public AttributeStatistics getAttributeStatistics() {
        return classifyInstanceTable.getAttributeStatistics();
    }

    public final Classifier classifier() {
        return classifier;
    }

    public final void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    private void createGUI() {
        JScrollPane pane = new JScrollPane(classifyInstanceTable);
        pane.setPreferredSize(SCROLL_PANE_PREFERRED_SIZE);
        pane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        JTextPane classField = new JTextPane();
        classField.setPreferredSize(new Dimension(300,100));
        classField.setEditable(false);
        classField.setContentType(CONTENT_TYPE);
        JScrollPane bottom = new JScrollPane(classField);
        bottom.setBorder(PanelBorderUtils
                .createTitledBorder(String.format(CLASS_NAME_FORMAT, classifyInstanceTable.data().classAttribute().name())));
        bottom.setToolTipText(ReportGenerator.getAttributeStatisticsAsHtml(classifyInstanceTable.data().classAttribute(),
                classifyInstanceTable.getAttributeStatistics()));
        JPanel top = new JPanel(new GridBagLayout());

        JButton classifyButton = new JButton(CLASSIFY_BUTTON_TEXT);
        classifyButton.setToolTipText(CLASSIFY_INFO);
        classifyButton.addActionListener(event -> {
            try {
                classField.setText(buildClassificationResult());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ClassifyInstancePanel.this.getParent(),
                        e.getMessage(), null,
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        JButton resetButton = new JButton(RESET_BUTTON_TEXT);
        resetButton.setToolTipText(RESET_INFO);
        resetButton.addActionListener(e -> {
            classifyInstanceTable.reset();
            classField.setText(StringUtils.EMPTY);
        });
        //-----------------------------------------------------
        top.add(pane, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(15, 0, 10, 0), 0, 0));
        top.add(classifyButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 10, 3), 0, 0));
        top.add(resetButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 10, 0), 0, 0));
        this.add(top, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(bottom, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
    }

    private String buildClassificationResult() throws Exception {
        Instance obj = classifyInstanceTable.buildInstance();
        int i = (int) classifier.classifyInstance(obj);
        double probability = classifier.distributionForInstance(obj)[i];
        if (template == null) {
            template = VelocityConfigService.getTemplate(VM_TEMPLATES_CLASSIFY_OBJECT_VM);
        }
        if (velocityContext == null) {
            velocityContext = new VelocityContext();
        }
        velocityContext.put(CLASS_INDEX, i);
        velocityContext.put(CLASS_VALUE, classifyInstanceTable.data().classAttribute().value(i));
        velocityContext.put(CLASS_PROBABILITY, classifyInstanceTable.getDecimalFormat().format(probability));
        StringWriter stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);
        return stringWriter.toString();
    }
}
