package eca.gui.frames;

import eca.dictionary.AttributesTypesDictionary;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.tables.FrequencyDiagramTable;
import eca.gui.tables.JDataTableBase;
import eca.gui.tables.models.AttributeTableModel;
import eca.gui.tables.models.DataInfoTableModel;
import eca.gui.tables.models.NominalAttributeTableModel;
import eca.gui.tables.models.NumericAttributeTableModel;
import eca.statistics.AttributeStatistics;
import eca.statistics.diagram.FrequencyData;
import eca.statistics.diagram.FrequencyDiagramBuilder;
import eca.text.NumericFormatFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.List;

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
    private static final int DEFAULT_WIDTH = 875;
    private static final int DEFAULT_HEIGHT = 450;
    private static final Dimension FREQUENCY_DIAGRAM_DIMENSION = new Dimension(400, DEFAULT_HEIGHT);
    private static final Dimension ATTR_BOX_DIMENSION = new Dimension(200, 25);
    private static final String FREQUENCY_DIAGRAM_TITLE = "Гистограмма частот";
    private static final String FREQUENCY_Y_LABEL = "Частота";
    private static final String X_LABEL_FORMAT = "Значения атрибута '%s'";
    private static final String SHOW_DATA_MENU_TEXT = "Показать данные";
    private static final String FREQUENCY_DIAGRAM_DATA_TITLE = "Данные гистограммы";
    private static final Color FREQUENCY_DIAGRAM_BORDER_COLOR = Color.GRAY;
    private static final BasicStroke FREQUENCY_DIAGRAM_STROKE =
            new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);

    private JFrame parentFrame;

    private JDataTableBase statisticsTable;

    private JComboBox<String> attributesBox;

    private AttributeTableModel[] attributesStatisticsTableModels;

    private JLabel attributeTypeLabel;

    private ChartPanel attributeChartPanel;

    private FrequencyDiagramModel[] frequencyDiagramModels;

    private JFrame[] dataFrames;

    private FrequencyDiagramBuilder frequencyDiagramBuilder;

    public AttributesStatisticsFrame(Instances data, JFrame parent, int digits) {
        DecimalFormat decimalFormat = NumericFormatFactory.getInstance();
        decimalFormat.setMaximumFractionDigits(digits);
        this.parentFrame = parent;
        this.attributesStatisticsTableModels = new AttributeTableModel[data.numAttributes()];
        this.frequencyDiagramModels = new FrequencyDiagramModel[data.numAttributes()];
        this.dataFrames = new DataFrame[data.numInstances()];
        this.statisticsTable = new JDataTableBase();
        this.frequencyDiagramBuilder = new FrequencyDiagramBuilder(new AttributeStatistics(data, decimalFormat));
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
        attributesBox.setMinimumSize(ATTR_BOX_DIMENSION);
        attributesBox.setPreferredSize(ATTR_BOX_DIMENSION);

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
                        attributesStatisticsTableModels[i]
                                = new NominalAttributeTableModel(a, frequencyDiagramBuilder.getAttributeStatistics());
                    } else {
                        attributesStatisticsTableModels[i] =
                                new NumericAttributeTableModel(a, frequencyDiagramBuilder.getAttributeStatistics());
                    }
                }
                statisticsTable.setModel(attributesStatisticsTableModels[i]);
                attributeTypeLabel.setText(AttributesTypesDictionary.getType(a));
                showFrequencyDiagramPlot(i);
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

        attributesBox.setSelectedIndex(0);
        showFrequencyDiagramPlot(0);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                for (JFrame frame : dataFrames) {
                    if (frame != null) {
                        frame.dispose();
                    }
                }
            }
        });

        JPanel leftCPanel = new JPanel(new GridBagLayout());
        leftCPanel.add(dataInfoPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0.4,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 0, 0), 0, 0));
        leftCPanel.add(content, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 0), 0, 0));
        leftCPanel.add(scrollPane, new GridBagConstraints(0, 2, 1, 1, 1, 0.6,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 0, 0), 0, 0));
        this.add(leftCPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(attributeChartPanel, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 2, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 0, 4, 0), 0, 0));

        this.getRootPane().setDefaultButton(okButton);
        this.setLocationRelativeTo(parent);
    }

    private void showFrequencyDiagramPlot(int attrIndex) {
        if (frequencyDiagramModels[attrIndex] == null) {
            Instances data = frequencyDiagramBuilder.getData();
            Attribute attribute = data.attribute(attrIndex);
            FrequencyDiagramModel frequencyDiagramModel;
            if (attribute.isNumeric()) {
                frequencyDiagramModel = createFrequencyModelForNumericAttribute(attribute);
            } else {
                frequencyDiagramModel = createFrequencyModelForNominalAttribute(attribute);
            }
            frequencyDiagramModel.getChart().setBorderVisible(true);
            frequencyDiagramModel.getChart().setBorderPaint(FREQUENCY_DIAGRAM_BORDER_COLOR);
            frequencyDiagramModel.getChart().setBorderStroke(FREQUENCY_DIAGRAM_STROKE);
            frequencyDiagramModels[attrIndex] = frequencyDiagramModel;
        }

        if (attributeChartPanel == null) {
            attributeChartPanel = new ChartPanel(frequencyDiagramModels[attrIndex].getChart());
            attributeChartPanel.setPreferredSize(FREQUENCY_DIAGRAM_DIMENSION);
            attributeChartPanel.setMinimumSize(FREQUENCY_DIAGRAM_DIMENSION);

            JMenuItem dataMenu = new JMenuItem(SHOW_DATA_MENU_TEXT);
            dataMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    int selectedIndex = attributesBox.getSelectedIndex();
                    if (dataFrames[selectedIndex] == null) {
                        dataFrames[selectedIndex] = new DataFrame(frequencyDiagramModels[selectedIndex]
                                .getFrequencyDataList(), frequencyDiagramBuilder.getAttributeStatistics()
                                .getDecimalFormat().getMaximumFractionDigits());
                    }
                    dataFrames[selectedIndex].setVisible(true);
                }
            });

            attributeChartPanel.getPopupMenu().add(dataMenu);
        } else {
            attributeChartPanel.setChart(frequencyDiagramModels[attrIndex].getChart());
        }

    }

    private FrequencyDiagramModel createFrequencyModelForNominalAttribute(Attribute attribute) {
        DefaultCategoryDataset categoryDataSet = new DefaultCategoryDataset();
        List<FrequencyData> frequencyDataList =
                frequencyDiagramBuilder.calculateFrequencyDiagramDataForNominalAttribute(attribute);
        for (FrequencyData frequencyData : frequencyDataList) {
            categoryDataSet.addValue(frequencyData.getFrequency(), attribute.value((int) frequencyData.getLowerBound()),
                    attribute.value((int) frequencyData.getLowerBound()));
        }
        CategoryAxis domainAxis = new CategoryAxis(String.format(X_LABEL_FORMAT, attribute.name()));
        NumberAxis rangeAxis = new NumberAxis(FREQUENCY_Y_LABEL);
        BarRenderer barRenderer = new BarRenderer();
        return new FrequencyDiagramModel(new JFreeChart(FREQUENCY_DIAGRAM_TITLE,
                new CategoryPlot(categoryDataSet, domainAxis, rangeAxis, barRenderer)), frequencyDataList);
    }

    private FrequencyDiagramModel createFrequencyModelForNumericAttribute(Attribute attribute) {
        XYSeries series = new XYSeries(FREQUENCY_DIAGRAM_TITLE);
        List<FrequencyData> frequencyDataList =
                frequencyDiagramBuilder.calculateFrequencyDiagramDataForNumericAttribute(attribute);
        for (FrequencyData frequencyData : frequencyDataList) {
            series.add(frequencyData.getLowerBound(), frequencyData.getFrequency());
        }
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(series);
        FrequencyData frequencyData = frequencyDataList.get(0);
        xySeriesCollection.setIntervalWidth(frequencyData.getUpperBound() - frequencyData.getLowerBound());
        JFreeChart chart = ChartFactory.createXYBarChart(FREQUENCY_DIAGRAM_TITLE,
                String.format(X_LABEL_FORMAT, attribute.name()), false,
                FREQUENCY_Y_LABEL, xySeriesCollection, PlotOrientation.VERTICAL, true, true, false
        );
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        xyPlot.getRenderer().setBaseSeriesVisibleInLegend(false);

        return new FrequencyDiagramModel(chart, frequencyDataList);
    }

    @Data
    @AllArgsConstructor
    private static class FrequencyDiagramModel {

        JFreeChart chart;

        List<FrequencyData> frequencyDataList;
    }

    /**
     * Frequency diagram data frame.
     */
    private class DataFrame extends JFrame {

        DataFrame(List<FrequencyData> frequencyDataList, int digits) {
            this.setTitle(FREQUENCY_DIAGRAM_DATA_TITLE);
            this.setLayout(new GridBagLayout());
            this.setIconImage(parentFrame.getIconImage());
            FrequencyDiagramTable table = new FrequencyDiagramTable(frequencyDataList, digits);
            JScrollPane scrollPanel = new JScrollPane(table);
            JButton okButton = ButtonUtils.createOkButton();

            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    setVisible(false);
                }
            });
            this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(4, 0, 4, 0), 0, 0));
            this.pack();
            this.setLocationRelativeTo(parentFrame);
            this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
    }

}
