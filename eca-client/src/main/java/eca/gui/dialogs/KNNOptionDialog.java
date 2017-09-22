/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.DoubleDocument;
import eca.gui.text.IntegerDocument;
import eca.gui.validators.TextFieldInputVerifier;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.ChebyshevDistance;
import eca.metrics.distances.EuclidDistance;
import eca.metrics.distances.ManhattanDistance;
import eca.metrics.distances.SquareEuclidDistance;
import eca.text.NumericFormat;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */
public class KNNOptionDialog extends BaseOptionsDialog<KNearestNeighbours> {

    private static final String OPTIONS_MESSAGE = "Параметры алгоритма";

    private static final String NUM_NEIGHBOURS_MESSAGE = "Число ближайших соседей:";
    private static final String WEIGHT_MESSAGE = "Вес ближайшего соседа:";
    private static final String DISTANCE_MESSAGE = "Функция расстояния:";

    private static final String[] METRICS_TEXT = {"Евкилидово расстояние", "Квадрат Евклидова расстояния",
            "Манхеттенское расстояние", "Расстояние Чебышева"};
    private static final String NUMBER_OF_NEIGHBOURS_EXCEEDED_ERROR_FORMAT =
            "Число ближайших соседей должно быть не больше %d";

    private final JTextField numNeighboursText;
    private final JTextField weightText;
    private final JComboBox<String> metric;

    private DecimalFormat estimateFormat = NumericFormat.getInstance();

    public KNNOptionDialog(Window parent, String title,
                           KNearestNeighbours knn, Instances data) {
        super(parent, title, knn, data);
        this.data = data;
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        //-------------------------------------------
        estimateFormat.setMaximumFractionDigits(INT_FIELD_LENGTH);
        estimateFormat.setGroupingUsed(false);
        //-------------------------------------------
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(OPTIONS_MESSAGE));
        numNeighboursText = new JTextField(TEXT_FIELD_LENGTH);
        numNeighboursText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        numNeighboursText.setInputVerifier(new TextFieldInputVerifier());
        weightText = new JTextField(TEXT_FIELD_LENGTH);
        weightText.setDocument(new DoubleDocument(INT_FIELD_LENGTH));
        weightText.setInputVerifier(new TextFieldInputVerifier());
        //--------------------------------------------
        metric = new JComboBox(METRICS_TEXT);
        optionPanel.add(new JLabel(NUM_NEIGHBOURS_MESSAGE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numNeighboursText, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(WEIGHT_MESSAGE), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(weightText, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(DISTANCE_MESSAGE), new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 10), 0, 0));
        optionPanel.add(metric, new GridBagConstraints(0, 3, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //------------------------------------
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dialogResult = false;
                setVisible(false);
            }
        });
        //-----------------------------------------------
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JTextField text = GuiUtils.searchFirstEmptyField(numNeighboursText, weightText);
                if (text != null) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(KNNOptionDialog.this, text);
                } else if (Integer.parseInt(numNeighboursText.getText()) > data.numInstances()) {
                    JOptionPane.showMessageDialog(KNNOptionDialog.this,
                            String.format(NUMBER_OF_NEIGHBOURS_EXCEEDED_ERROR_FORMAT, data.numInstances()),
                            INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                    numNeighboursText.requestFocusInWindow();
                } else {
                    JTextField focus = weightText;
                    try {
                        classifier.setWeight(estimateFormat.parse(weightText.getText()).doubleValue());
                        focus = numNeighboursText;
                        classifier.setNumNeighbours(Integer.parseInt(numNeighboursText.getText()));
                        switch (metric.getSelectedIndex()) {
                            case 0:
                                classifier.setDistance(new EuclidDistance());
                                break;
                            case 1:
                                classifier.setDistance(new SquareEuclidDistance());
                                break;
                            case 2:
                                classifier.setDistance(new ManhattanDistance());
                                break;
                            case 3:
                                classifier.setDistance(new ChebyshevDistance());
                                break;
                        }
                        dialogResult = true;
                        setVisible(false);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(KNNOptionDialog.this,
                                e.getMessage(), INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                        focus.requestFocusInWindow();
                    }
                }
            }
        });
        //------------------------------------
        this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));

        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        this.setLocationRelativeTo(parent);
        numNeighboursText.requestFocusInWindow();
    }

    @Override
    public final void showDialog() {
        this.setOptions();
        super.showDialog();
        numNeighboursText.requestFocusInWindow();
    }

    private void setOptions() {
        numNeighboursText.setText(String.valueOf(classifier.getNumNeighbours()));
        weightText.setText(estimateFormat.format(classifier.getWeight()));
        switch (classifier.distance().getClass().getSimpleName()) {
            case "EuclidDistance":
                metric.setSelectedIndex(0);
                break;

            case "SquareEuclidDistance":
                metric.setSelectedIndex(1);
                break;

            case "ManhattanDistance":
                metric.setSelectedIndex(2);
                break;

            case "ChebyshevDistance":
                metric.setSelectedIndex(3);
                break;
        }
    }

}
