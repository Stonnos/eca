package eca.gui.frames;

import eca.dictionary.AttributesTypesDictionary;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.tables.JDataTableBase;
import eca.gui.tables.models.AttributeTableModel;
import eca.gui.tables.models.DataInfoTableModel;
import eca.gui.tables.models.NominalAttributeTableModel;
import eca.gui.tables.models.NumericAttributeTableModel;
import eca.statistics.AttributeStatistics;
import eca.text.NumericFormat;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */

public class AttributesStatisticsFrame extends JFrame {

    private static final String TITLE_TEXT = "Статистика по атрибутам";
    private static final String DATA_INFO_TITLE = "Информация о данных";
    private static final String STATISTICS_TITLE = "Статистика";
    private static final String SELECTED_ATTRIBUTE_TITLE = "Выбранный атрибут";
    private static final String ATTR_LABEL_TEXT = "Атрибут:";
    private static final String ATTR_TYPE_TEXT = "Тип:";
    private static final int DEFAULT_WIDTH = 475;
    private static final int DEFAULT_HEIGHT = 450;

    private final DecimalFormat decimalFormat = NumericFormat.getInstance();

    private AttributeStatistics attributeStatistics;

    private JDataTableBase statisticsTable;

    private JComboBox<String> attributesBox;

    private AttributeTableModel[] attributesStatisticsTableModels;

    private JLabel attributeTypeLabel;

    public AttributesStatisticsFrame(Instances data, JFrame parent, int digits) {
        this.decimalFormat.setMaximumFractionDigits(digits);
        this.attributeStatistics = new AttributeStatistics(data, decimalFormat);
        this.attributesStatisticsTableModels = new AttributeTableModel[data.numAttributes()];
        this.statisticsTable = new JDataTableBase();
        this.statisticsTable.setAutoResizeOff(false);
        this.setTitle(TITLE_TEXT);
        this.setLayout(new GridBagLayout());
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setIconImage(parent.getIconImage());

        JPanel dataInfoPanel = new JPanel(new GridBagLayout());

        dataInfoPanel.setBorder(PanelBorderUtils.createTitledBorder(DATA_INFO_TITLE));

        JDataTableBase dataInfoTable = new JDataTableBase(new DataInfoTableModel(data));
        dataInfoTable.setAutoResizeOff(false);
        JScrollPane tablePane = new JScrollPane(dataInfoTable);

        dataInfoPanel.add(tablePane, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        attributesBox = new JComboBox<>();
        Dimension dim = new Dimension(200, 25);
        attributesBox.setMinimumSize(dim);
        attributesBox.setPreferredSize(dim);

        for (int i = 0; i < data.numAttributes(); i++) {
            attributesBox.addItem(data.attribute(i).name());
        }

        attributesBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int i = attributesBox.getSelectedIndex();
                Attribute a = data.attribute(i);
                if (attributesStatisticsTableModels[i] == null) {
                    if (a.isNominal()) {
                        attributesStatisticsTableModels[i] = new NominalAttributeTableModel(a,
                                attributeStatistics);
                    } else {
                        attributesStatisticsTableModels[i] = new NumericAttributeTableModel(a,
                                attributeStatistics);
                    }
                }
                statisticsTable.setModel(attributesStatisticsTableModels[i]);
                attributeTypeLabel.setText(AttributesTypesDictionary.getType(a));
            }
        });

        attributeTypeLabel = new JLabel();
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(PanelBorderUtils.createTitledBorder(SELECTED_ATTRIBUTE_TITLE));

        content.add(new JLabel(ATTR_LABEL_TEXT), new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 10, 0, 10), 0, 0));
        content.add(attributesBox, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 10), 0, 0));
        content.add(new JLabel(ATTR_TYPE_TEXT), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 10, 0, 10), 0, 0));
        content.add(attributeTypeLabel, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 10), 0, 0));

        JScrollPane scrollPane = new JScrollPane(statisticsTable);
        scrollPane.setBorder(PanelBorderUtils.createTitledBorder(STATISTICS_TITLE));

        JButton okButton = ButtonUtils.createOkButton();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });

        this.add(dataInfoPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0.4,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 0, 0), 0, 0));
        this.add(content, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 0), 0, 0));
        this.add(scrollPane, new GridBagConstraints(0, 2, 1, 1, 1, 0.6,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 0, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 3, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(4, 0, 4, 0), 0, 0));

        attributesBox.setSelectedIndex(0);
        this.getRootPane().setDefaultButton(okButton);
        this.setLocationRelativeTo(parent);
    }
}
