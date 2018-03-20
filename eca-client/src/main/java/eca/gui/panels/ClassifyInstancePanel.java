/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.panels;

import eca.gui.PanelBorderUtils;
import eca.gui.service.ClassifierInputOptionsService;
import eca.gui.tables.ClassifyInstanceTable;
import eca.statistics.AttributeStatistics;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import weka.classifiers.Classifier;
import weka.core.Instance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Roman Batygin
 */
@Slf4j
public class ClassifyInstancePanel extends JPanel {

    private static final String CLASSIFY_INFO = "Классифицировать новый пример";
    private static final String RESET_INFO = "Сбросить все значения в таблице";
    private static final String CLASSIFY_INSTANCE_TITLE = "Классификация примера";
    private static final String CLASSIFY_BUTTON_TEXT = "Классифицировать";
    private static final String RESET_BUTTON_TEXT = "Сброс значений";
    private static final String CLASS_NAME_FORMAT = "Значение класса %s";
    private static final String CLASS_CODE_TEXT = "Код класса: ";
    private static final String CLASS_VALUE_TEXT = "Значение класса: ";
    private static final String CLASS_PROBABILITY_TEXT = "Вероятность класса: ";
    private static final Dimension SCROLL_PANE_PREFERRED_SIZE = new Dimension(400, 300);

    private Classifier classifier;
    private final ClassifyInstanceTable table;

    private JTextArea classField;

    public ClassifyInstancePanel(ClassifyInstanceTable table, Classifier classifier) {
        this.setClassifier(classifier);
        this.table = table;
        this.setLayout(new GridBagLayout());
        this.setBorder(PanelBorderUtils.createTitledBorder(CLASSIFY_INSTANCE_TITLE));
        this.makeGUI();
    }

    public AttributeStatistics getAttributeStatistics() {
        return table.getAttributeStatistics();
    }

    public final Classifier classifier() {
        return classifier;
    }

    public final void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    private void makeGUI() {
        JScrollPane pane = new JScrollPane(table);
        pane.setPreferredSize(SCROLL_PANE_PREFERRED_SIZE);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        classField = new JTextArea(5, 5);
        classField.setWrapStyleWord(true);
        classField.setLineWrap(true);
        classField.setEditable(false);
        JScrollPane bottom = new JScrollPane(classField);
        bottom.setBorder(PanelBorderUtils
                .createTitledBorder(String.format(CLASS_NAME_FORMAT, table.data().classAttribute().name())));
        bottom.setToolTipText(ClassifierInputOptionsService.getAttributeStatisticsAsHtml(table.data().classAttribute(),
                table.getAttributeStatistics()));
        JPanel top = new JPanel(new GridBagLayout());

        JButton classifyButton = new JButton(CLASSIFY_BUTTON_TEXT);
        classifyButton.setToolTipText(CLASSIFY_INFO);
        classifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    classField.setText(buildClassificationResult());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClassifyInstancePanel.this.getParent(),
                            e.getMessage(), null,
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        JButton resetButton = new JButton(RESET_BUTTON_TEXT);
        resetButton.setToolTipText(RESET_INFO);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                table.reset();
                classField.setText(StringUtils.EMPTY);
            }
        });
        //-----------------------------------------------------
        top.add(pane, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
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
        Instance obj = table.instance();
        int i = (int) classifier.classifyInstance(obj);
        double probability = classifier.distributionForInstance(obj)[i];
        StringBuilder result = new StringBuilder();
        result.append(CLASS_CODE_TEXT).append(i)
                .append("\n").append(CLASS_VALUE_TEXT)
                .append(table.data().classAttribute().value(i))
                .append("\n").append(CLASS_PROBABILITY_TEXT)
                .append(table.getDecimalFormat().format(probability));
        return result.toString();
    }
}
