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
import eca.metrics.distances.DistanceBuilder;
import eca.metrics.distances.DistanceType;
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

    private static final String NUMBER_OF_NEIGHBOURS_EXCEEDED_ERROR_FORMAT =
            "Число ближайших соседей должно быть не больше %d";

    private final JTextField numNeighboursTextField;
    private final JTextField weightTextField;
    private final JComboBox<String> metricBox;

    private final DecimalFormat estimateFormat = NumericFormat.getInstance();
    private final DistanceBuilder distanceBuilder = new DistanceBuilder();

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
        numNeighboursTextField = new JTextField(TEXT_FIELD_LENGTH);
        numNeighboursTextField.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        numNeighboursTextField.setInputVerifier(new TextFieldInputVerifier());
        weightTextField = new JTextField(TEXT_FIELD_LENGTH);
        weightTextField.setDocument(new DoubleDocument(INT_FIELD_LENGTH));
        weightTextField.setInputVerifier(new TextFieldInputVerifier());
        //--------------------------------------------
        metricBox = new JComboBox(DistanceType.getDescriptions());
        optionPanel.add(new JLabel(NUM_NEIGHBOURS_MESSAGE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numNeighboursTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(WEIGHT_MESSAGE), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(weightTextField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(DISTANCE_MESSAGE), new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 10), 0, 0));
        optionPanel.add(metricBox, new GridBagConstraints(0, 3, 2, 1, 1, 1,
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
                JTextField text = GuiUtils.searchFirstEmptyField(numNeighboursTextField, weightTextField);
                if (text != null) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(KNNOptionDialog.this, text);
                } else if (Integer.parseInt(numNeighboursTextField.getText()) > data.numInstances()) {
                    JOptionPane.showMessageDialog(KNNOptionDialog.this,
                            String.format(NUMBER_OF_NEIGHBOURS_EXCEEDED_ERROR_FORMAT, data.numInstances()),
                            INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                    numNeighboursTextField.requestFocusInWindow();
                } else {
                    JTextField focus = weightTextField;
                    try {
                        classifier.setWeight(estimateFormat.parse(weightTextField.getText()).doubleValue());
                        focus = numNeighboursTextField;
                        classifier.setNumNeighbours(Integer.parseInt(numNeighboursTextField.getText()));

                        DistanceType distanceType =
                                DistanceType.findByDescription(metricBox.getSelectedItem().toString());

                        classifier.setDistance(distanceType.handle(distanceBuilder));

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
        numNeighboursTextField.requestFocusInWindow();
    }

    @Override
    public final void showDialog() {
        this.setOptions();
        super.showDialog();
        numNeighboursTextField.requestFocusInWindow();
    }

    private void setOptions() {
        numNeighboursTextField.setText(String.valueOf(classifier.getNumNeighbours()));
        weightTextField.setText(estimateFormat.format(classifier.getWeight()));
        DistanceType distanceType = classifier.distance().getDistanceType();
        metricBox.setSelectedItem(distanceType.getDescription());
    }

}
