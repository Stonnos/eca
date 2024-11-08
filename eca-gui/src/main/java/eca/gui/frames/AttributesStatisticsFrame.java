package eca.gui.frames;

import eca.dictionary.AttributesTypesDictionary;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.diagram.DiagramType;
import eca.gui.diagram.DiagramTypeVisitor;
import eca.gui.diagram.FrequencyDiagramModel;
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
import eca.util.EnumUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.List;

import static eca.gui.GuiUtils.removeComponents;

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
    private static final int DEFAULT_WIDTH = 950;
    private static final int DEFAULT_HEIGHT = 450;
    private static final Dimension FREQUENCY_DIAGRAM_DIMENSION = new Dimension(400, DEFAULT_HEIGHT);
    private static final Dimension ATTR_BOX_DIMENSION = new Dimension(200, 25);
    private static final String FREQUENCY_DIAGRAM_TITLE = "Гистограмма частот";
    private static final String FREQUENCY_Y_LABEL = "Частота";
    private static final String X_LABEL_FORMAT = "Значения атрибута '%s'";
    private static final String SHOW_DATA_MENU_TEXT = "Показать данные";
    private static final String FREQUENCY_DIAGRAM_DATA_TITLE = "Данные гистограммы";
    private static final String PLOT_TYPE_LABEL_TEXT = "Тип графика:";
    private static final String PIE_DIAGRAM_TITLE = "Круговая диаграмма";
    private static final String PIE_DIAGRAM_TITLE_3D = "Круговая диаграмма 3D";
    private static final String FIRST_INTERVAL_FORMAT = "[%s; %s]";
    private static final String INTERVAL_FORMAT = "(%s; %s]";
    private static final Color FREQUENCY_DIAGRAM_BORDER_COLOR = Color.GRAY;
    private static final BasicStroke FREQUENCY_DIAGRAM_STROKE =
            new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
    private static final int START_ANGLE = 290;
    private static final float FOREGROUND_ALPHA = 0.70f;
    private static final int STATISTICS_TABLE_NAME_COLUMN_WIDTH = 275;
    private static final int DATA_INFO_TABLE_NAME_COLUMN_WIDTH = 175;
    private static final int DEFAULT_STATISTICS_TABLE_NAME_COLUMN_WIDTH = 25;

    private static PieSectionLabelGenerator pieSectionLabelGenerator;

    private JFrame parentFrame;

    private JDataTableBase statisticsTable;

    private JComboBox<String> attributesBox;

    private JComboBox<String> plotBox;

    private AttributeTableModel[] attributesStatisticsTableModels;

    private JLabel attributeTypeLabel;

    private ChartPanel attributeChartPanel;

    private FrequencyDiagramModel[] frequencyDiagramModels;

    private JFrame[] dataFrames;

    private FrequencyDiagramBuilder frequencyDiagramBuilder;

    static {
        pieSectionLabelGenerator = new StandardPieSectionLabelGenerator("{0} : {1} ({2})", new DecimalFormat("0"),
                new DecimalFormat("0.00%"));
    }

    public AttributesStatisticsFrame(Instances data, JFrame parent, int digits) {
        this.parentFrame = parent;
        this.attributesStatisticsTableModels = new AttributeTableModel[data.numAttributes()];
        this.frequencyDiagramModels = new FrequencyDiagramModel[data.numAttributes()];
        this.dataFrames = new DataFrame[data.numInstances()];
        this.statisticsTable = new JDataTableBase();
        this.statisticsTable.setAutoResizeOff(false);
        DecimalFormat decimalFormat = NumericFormatFactory.getInstance();
        decimalFormat.setMaximumFractionDigits(digits);
        this.frequencyDiagramBuilder = new FrequencyDiagramBuilder(new AttributeStatistics(data, decimalFormat));
        this.createGUI(data);
        this.setLocationRelativeTo(parent);
    }

    @Override
    public void dispose() {
        clear();
        removeComponents(this);
        super.dispose();
    }

    private void clear() {
        for (var frequencyDiagramModel : frequencyDiagramModels) {
            if (frequencyDiagramModel != null) {
                frequencyDiagramModel.getFrequencyDataList().clear();
                frequencyDiagramModel.getDiagramMap().clear();
                frequencyDiagramModel.setCurrentChart(null);
            }
        }
        for (JFrame frame : dataFrames) {
            if (frame != null) {
                frame.dispose();
            }
        }
        attributeChartPanel = null;
    }

    private void createGUI(Instances data) {
        this.setTitle(TITLE_TEXT);
        this.setLayout(new GridBagLayout());
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setIconImage(parentFrame.getIconImage());

        JPanel dataInfoPanel = new JPanel(new GridBagLayout());
        dataInfoPanel.setBorder(PanelBorderUtils.createTitledBorder(DATA_INFO_TITLE));
        JDataTableBase dataInfoTable = new JDataTableBase(new DataInfoTableModel(data));
        dataInfoTable.getColumnModel().getColumn(0).setPreferredWidth(DATA_INFO_TABLE_NAME_COLUMN_WIDTH);
        dataInfoTable.setAutoResizeOff(false);
        JScrollPane tablePane = new JScrollPane(dataInfoTable);

        dataInfoPanel.add(tablePane, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        createAttributeBox(data);
        createPlotBox();
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

        JButton closeButton = ButtonUtils.createCloseButton();

        closeButton.addActionListener(e -> setVisible(false));

        attributesBox.setSelectedIndex(0);
        showFrequencyDiagramPlot(0);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                for (JFrame frame : dataFrames) {
                    if (frame != null) {
                        frame.setVisible(false);
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

        JPanel plotPanel = new JPanel(new GridBagLayout());
        plotPanel.add(attributeChartPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 10, 5), 0, 0));
        plotPanel.add(new JLabel(PLOT_TYPE_LABEL_TEXT), new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 10, 5), 0, 0));
        plotPanel.add(plotBox, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 10, 5), 0, 0));

        this.add(leftCPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(plotPanel, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        this.add(closeButton, new GridBagConstraints(0, 1, 2, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 0, 10, 0), 0, 0));
    }

    private void createPlotBox() {
        plotBox = new JComboBox<>(DiagramType.getDescriptions());
        plotBox.addActionListener(event -> {
            int attrIndex = attributesBox.getSelectedIndex();
            String selectedDiagram = plotBox.getSelectedItem().toString();
            DiagramType diagramType = EnumUtils.fromDescription(selectedDiagram, DiagramType.class);
            FrequencyDiagramModel frequencyDiagramModel = frequencyDiagramModels[attrIndex];
            frequencyDiagramModel.setCurrentDiagramType(diagramType);
            if (!frequencyDiagramModel.getDiagramMap().containsKey(diagramType)) {
                JFreeChart chart = createChart(frequencyDiagramModel);
                frequencyDiagramModel.getDiagramMap().put(diagramType, chart);
                frequencyDiagramModel.setCurrentChart(chart);
            } else {
                JFreeChart chart = frequencyDiagramModel.getDiagramMap().get(diagramType);
                frequencyDiagramModel.setCurrentChart(chart);
            }
            attributeChartPanel.setChart(frequencyDiagramModel.getCurrentChart());
        });
    }

    private JFreeChart createChart(FrequencyDiagramModel frequencyDiagramModel) {
        return frequencyDiagramModel.getCurrentDiagramType().handle(
                new DiagramTypeVisitor<JFreeChart>() {
                    @Override
                    public JFreeChart caseFrequencyDiagram() {
                        if (frequencyDiagramModel.getAttribute().isNumeric()) {
                            return createFrequencyDiagramChartForNumericAttribute(frequencyDiagramModel);
                        } else {
                            return createFrequencyDiagramChartForNominalAttribute(frequencyDiagramModel);
                        }
                    }

                    @Override
                    public JFreeChart casePieDiagram() {
                        DefaultPieDataset pieDataSet = createPieDataSet(frequencyDiagramModel);
                        JFreeChart chart =
                                ChartFactory.createPieChart(PIE_DIAGRAM_TITLE, pieDataSet, true, false, false);
                        ((PiePlot) chart.getPlot()).setLabelGenerator(pieSectionLabelGenerator);
                        return chart;
                    }

                    @Override
                    public JFreeChart casePie3dDiagram() {
                        DefaultPieDataset pieDataSet = createPieDataSet(frequencyDiagramModel);
                        JFreeChart chart =
                                ChartFactory.createPieChart3D(PIE_DIAGRAM_TITLE_3D, pieDataSet, true, false, false);
                        ((PiePlot) chart.getPlot()).setLabelGenerator(pieSectionLabelGenerator);
                        PiePlot3D plot = (PiePlot3D) chart.getPlot();
                        plot.setStartAngle(START_ANGLE);
                        plot.setForegroundAlpha(FOREGROUND_ALPHA);
                        return chart;
                    }
                });
    }

    private DefaultPieDataset createPieDataSet(FrequencyDiagramModel frequencyDiagramModel) {
        DefaultPieDataset pieDataSet = new DefaultPieDataset();
        for (int i = 0; i < frequencyDiagramModel.getFrequencyDataList().size(); i++) {
            FrequencyData frequencyData = frequencyDiagramModel.getFrequencyDataList().get(i);
            if (frequencyDiagramModel.getAttribute().isNumeric()) {
                DecimalFormat decimalFormat = frequencyDiagramBuilder.getAttributeStatistics()
                        .getDecimalFormat();
                String intervalFormat = i == 0 ? FIRST_INTERVAL_FORMAT : INTERVAL_FORMAT;
                pieDataSet.setValue(String.format(intervalFormat, decimalFormat.format(frequencyData.getLowerBound()),
                        decimalFormat.format(frequencyData.getUpperBound())), frequencyData.getFrequency());
            } else {
                pieDataSet.setValue(frequencyDiagramModel.getAttribute().value((int) frequencyData.getLowerBound()),
                        frequencyData.getFrequency());
            }
        }
        return pieDataSet;
    }

    private void createAttributeBox(Instances data) {
        attributesBox = new JComboBox<>();
        attributesBox.setMinimumSize(ATTR_BOX_DIMENSION);
        attributesBox.setPreferredSize(ATTR_BOX_DIMENSION);
        for (int i = 0; i < data.numAttributes(); i++) {
            attributesBox.addItem(data.attribute(i).name());
        }
        attributesBox.addActionListener(e -> {
            int i = attributesBox.getSelectedIndex();
            Attribute a = data.attribute(i);
            int firstColumnWidth = DEFAULT_STATISTICS_TABLE_NAME_COLUMN_WIDTH;
            if (attributesStatisticsTableModels[i] == null) {
                if (a.isNominal()) {
                    attributesStatisticsTableModels[i]
                            = new NominalAttributeTableModel(a, frequencyDiagramBuilder.getAttributeStatistics());
                } else {
                    firstColumnWidth = STATISTICS_TABLE_NAME_COLUMN_WIDTH;
                    attributesStatisticsTableModels[i] =
                            new NumericAttributeTableModel(a, frequencyDiagramBuilder.getAttributeStatistics());
                }
            }
            statisticsTable.setModel(attributesStatisticsTableModels[i]);
            this.statisticsTable.getColumnModel().getColumn(0).setPreferredWidth(firstColumnWidth);
            attributeTypeLabel.setText(AttributesTypesDictionary.getType(a));
            showFrequencyDiagramPlot(i);
        });
    }

    private void showFrequencyDiagramPlot(int attrIndex) {
        if (frequencyDiagramModels[attrIndex] == null) {
            Instances instances = frequencyDiagramBuilder.getData();
            Attribute attribute = instances.attribute(attrIndex);
            if (attribute.isNumeric()) {
                frequencyDiagramModels[attrIndex] = createFrequencyModelForNumericAttribute(attribute);
            } else {
                frequencyDiagramModels[attrIndex] = createFrequencyModelForNominalAttribute(attribute);
            }
        }

        if (attributeChartPanel == null) {
            attributeChartPanel = new ChartPanel(frequencyDiagramModels[attrIndex].getCurrentChart());
            attributeChartPanel.setPreferredSize(FREQUENCY_DIAGRAM_DIMENSION);
            attributeChartPanel.setMinimumSize(FREQUENCY_DIAGRAM_DIMENSION);
            JMenuItem dataMenu = new JMenuItem(SHOW_DATA_MENU_TEXT);
            dataMenu.addActionListener(e -> {
                int selectedIndex = attributesBox.getSelectedIndex();
                if (dataFrames[selectedIndex] == null) {
                    dataFrames[selectedIndex] = new DataFrame(frequencyDiagramModels[selectedIndex]
                            .getFrequencyDataList(), frequencyDiagramBuilder.getAttributeStatistics()
                            .getDecimalFormat().getMaximumFractionDigits());
                }
                dataFrames[selectedIndex].setVisible(true);
            });
            attributeChartPanel.getPopupMenu().add(dataMenu);
        } else {
            plotBox.setSelectedItem(frequencyDiagramModels[attrIndex].getCurrentDiagramType().getDescription());
        }
    }

    private FrequencyDiagramModel createFrequencyModelForNominalAttribute(Attribute attribute) {
        List<FrequencyData> frequencyDataList =
                frequencyDiagramBuilder.calculateFrequencyDiagramDataForNominalAttribute(attribute);
        FrequencyDiagramModel frequencyDiagramModel = createFrequencyDiagramModel(attribute, frequencyDataList);
        JFreeChart chart = createFrequencyDiagramChartForNominalAttribute(frequencyDiagramModel);
        frequencyDiagramModel.getDiagramMap().put(DiagramType.FREQUENCY_DIAGRAM, chart);
        frequencyDiagramModel.setCurrentDiagramType(DiagramType.FREQUENCY_DIAGRAM);
        frequencyDiagramModel.setCurrentChart(chart);
        return frequencyDiagramModel;
    }

    private JFreeChart createFrequencyDiagramChartForNominalAttribute(FrequencyDiagramModel frequencyDiagramModel) {
        DefaultCategoryDataset categoryDataSet = new DefaultCategoryDataset();
        for (FrequencyData frequencyData : frequencyDiagramModel.getFrequencyDataList()) {
            categoryDataSet.addValue(frequencyData.getFrequency(),
                    frequencyDiagramModel.getAttribute().value((int) frequencyData.getLowerBound()),
                    frequencyDiagramModel.getAttribute().value((int) frequencyData.getLowerBound()));
        }
        CategoryAxis domainAxis =
                new CategoryAxis(String.format(X_LABEL_FORMAT, frequencyDiagramModel.getAttribute().name()));
        NumberAxis rangeAxis = new NumberAxis(FREQUENCY_Y_LABEL);
        BarRenderer barRenderer = new BarRenderer();
        JFreeChart chart = new JFreeChart(FREQUENCY_DIAGRAM_TITLE,
                new CategoryPlot(categoryDataSet, domainAxis, rangeAxis, barRenderer));
        chart.setBorderVisible(true);
        chart.setBorderPaint(FREQUENCY_DIAGRAM_BORDER_COLOR);
        chart.setBorderStroke(FREQUENCY_DIAGRAM_STROKE);
        return chart;
    }

    private JFreeChart createFrequencyDiagramChartForNumericAttribute(FrequencyDiagramModel frequencyDiagramModel) {
        XYSeries series = new XYSeries(FREQUENCY_DIAGRAM_TITLE);
        for (FrequencyData frequencyData : frequencyDiagramModel.getFrequencyDataList()) {
            series.add((frequencyData.getUpperBound() + frequencyData.getLowerBound()) / 2.0,
                    frequencyData.getFrequency());
        }
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(series);
        FrequencyData frequencyData = frequencyDiagramModel.getFrequencyDataList().get(0);
        xySeriesCollection.setIntervalWidth(frequencyData.getUpperBound() - frequencyData.getLowerBound());
        JFreeChart chart = ChartFactory.createXYBarChart(FREQUENCY_DIAGRAM_TITLE,
                String.format(X_LABEL_FORMAT, frequencyDiagramModel.getAttribute().name()), false,
                FREQUENCY_Y_LABEL, xySeriesCollection, PlotOrientation.VERTICAL, true, false, false
        );
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        xyPlot.getRenderer().setSeriesVisibleInLegend(0, true);
        chart.setBorderVisible(true);
        chart.setBorderPaint(FREQUENCY_DIAGRAM_BORDER_COLOR);
        chart.setBorderStroke(FREQUENCY_DIAGRAM_STROKE);
        return chart;
    }

    private FrequencyDiagramModel createFrequencyModelForNumericAttribute(Attribute attribute) {
        List<FrequencyData> frequencyDataList =
                frequencyDiagramBuilder.calculateFrequencyDiagramDataForNumericAttribute(attribute);
        FrequencyDiagramModel frequencyDiagramModel = createFrequencyDiagramModel(attribute, frequencyDataList);
        JFreeChart chart = createFrequencyDiagramChartForNumericAttribute(frequencyDiagramModel);
        frequencyDiagramModel.getDiagramMap().put(DiagramType.FREQUENCY_DIAGRAM, chart);
        frequencyDiagramModel.setCurrentDiagramType(DiagramType.FREQUENCY_DIAGRAM);
        frequencyDiagramModel.setCurrentChart(chart);
        return frequencyDiagramModel;
    }

    private FrequencyDiagramModel createFrequencyDiagramModel(Attribute attribute,
                                                              List<FrequencyData> frequencyDataList) {
        FrequencyDiagramModel frequencyDiagramModel = new FrequencyDiagramModel();
        frequencyDiagramModel.setAttribute(attribute);
        frequencyDiagramModel.setDiagramMap(new EnumMap<>(DiagramType.class));
        frequencyDiagramModel.setFrequencyDataList(frequencyDataList);
        return frequencyDiagramModel;
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
            JButton closeButton = ButtonUtils.createCloseButton();

            closeButton.addActionListener(e -> setVisible(false));
            this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(closeButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(4, 0, 4, 0), 0, 0));
            this.pack();
            this.setLocationRelativeTo(parentFrame);
            this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        }

        @Override
        public void dispose() {
            removeComponents(this);
            super.dispose();
        }
    }

}
