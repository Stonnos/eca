package eca.gui.frames;

import eca.gui.ButtonUtils;
import eca.util.Utils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roman Batygin
 */
public class ScatterDiagramsFrame extends JFrame {

    private static final int DEFAULT_WIDTH = 875;
    private static final int DEFAULT_HEIGHT = 600;
    private static final Dimension SCATTER_DIAGRAM_DIMENSION = new Dimension(DEFAULT_WIDTH, 400);
    private static final String TITLE_TEXT = "Диаграммы рассеяния";
    private static final String SCATTER_DIAGRAM_TITLE = "Диаграмма рассеяния";
    private static final String X_LABEL = "X:";
    private static final String Y_LABEL = "Y:";
    private static final Dimension ATTR_BOX_SIZE = new Dimension(300, 25);

    private Instances data;

    private ChartPanel scatterChartPanel;

    public ScatterDiagramsFrame(Instances data, JFrame parent) {
        this.data = data;
        this.setTitle(TITLE_TEXT);
        this.setLayout(new GridBagLayout());
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        this.setIconImage(parent.getIconImage());
        this.init();
        this.setLocationRelativeTo(parent);
    }

    private void init() {
        scatterChartPanel = new ChartPanel(null);
        scatterChartPanel.setPreferredSize(SCATTER_DIAGRAM_DIMENSION);
        scatterChartPanel.setMinimumSize(SCATTER_DIAGRAM_DIMENSION);
        String[] attributes = Utils.getAttributeNames(data);
        JComboBox<String> xAttributeBox = new JComboBox<>(attributes);
        JComboBox<String> yAttributeBox = new JComboBox<>(attributes);
        xAttributeBox.addActionListener(event -> {
            Attribute xAttr = data.attribute(xAttributeBox.getSelectedIndex());
            Attribute yAttr = data.attribute(yAttributeBox.getSelectedIndex());
            scatterChartPanel.setChart(createScatterChart(xAttr, yAttr));
        });
        yAttributeBox.addActionListener(event -> {
            Attribute xAttr = data.attribute(xAttributeBox.getSelectedIndex());
            Attribute yAttr = data.attribute(yAttributeBox.getSelectedIndex());
            scatterChartPanel.setChart(createScatterChart(xAttr, yAttr));
        });
        xAttributeBox.setMinimumSize(ATTR_BOX_SIZE);
        xAttributeBox.setPreferredSize(ATTR_BOX_SIZE);
        yAttributeBox.setMinimumSize(ATTR_BOX_SIZE);
        yAttributeBox.setPreferredSize(ATTR_BOX_SIZE);
        xAttributeBox.setSelectedIndex(0);
        yAttributeBox.setSelectedIndex(0);

        JButton okButton = ButtonUtils.createOkButton();
        okButton.addActionListener(e -> setVisible(false));

        JPanel plotPanel = new JPanel(new GridBagLayout());
        plotPanel.add(scatterChartPanel, new GridBagConstraints(0, 0, 4, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 10, 5), 0, 0));

        JPanel optionsPanel = new JPanel(new FlowLayout());
        optionsPanel.add(new JLabel(X_LABEL));
        optionsPanel.add(xAttributeBox);
        optionsPanel.add(new JLabel(Y_LABEL));
        optionsPanel.add(yAttributeBox);
        plotPanel.add(optionsPanel, new GridBagConstraints(0, 1, 4, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
        this.add(plotPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 0, 10, 0), 0, 0));
        this.getRootPane().setDefaultButton(okButton);
    }

    private JFreeChart createScatterChart(Attribute xAttr, Attribute yAttr) {
        XYSeriesCollection xySeriesCollection = createAndFillXYSeriesCollection();
        data.forEach(instance -> {
            XYSeries xySeries = xySeriesCollection.getSeries((int) instance.classValue());
            xySeries.add(instance.value(xAttr), instance.value(yAttr));
        });

        ValueAxis xAxis = createAxis(xAttr);
        ValueAxis yAxis = createAxis(yAttr);
        XYPlot plot = new XYPlot(xySeriesCollection, xAxis, yAxis, null);
        XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);
        plot.setRenderer(renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return new JFreeChart(SCATTER_DIAGRAM_TITLE, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    private XYSeriesCollection createAndFillXYSeriesCollection() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        for (int i = 0; i < data.numClasses(); i++) {
            XYSeries xySeries = new XYSeries(data.classAttribute().value(i));
            xySeriesCollection.addSeries(xySeries);
        }
        return xySeriesCollection;
    }

    private ValueAxis createAxis(Attribute attribute) {
        if (attribute.isNumeric()) {
            NumberAxis axis = new NumberAxis(attribute.name());
            axis.setAutoRangeIncludesZero(false);
            return axis;
        } else {
            return new SymbolAxis(attribute.name(), Utils.getAttributeValuesAsArray(attribute));
        }
    }
}
