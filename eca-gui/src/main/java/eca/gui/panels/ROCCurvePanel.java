/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.panels;

import eca.gui.ButtonUtils;
import eca.gui.tables.ROCThresholdTable;
import eca.roc.RocCurve;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

/**
 * @author Roman Batygin
 */
public class ROCCurvePanel extends JPanel {

    private static final String TITLE = "График ROC кривой";
    private static final String X_AXIS_TITLE = "100 - Специфичность (Specificity), %";
    private static final String Y_AXIS_TITLE = "Чувствительность (Sensitivity), %";
    private static final String ROC_CURVE_DATA_TITLE = "Данные ROC - кривой";
    private static final String SHOW_DATA_MENU_TEXT = "Показать данные";
    private static final String ALL_CLASSES_TEXT = "Все классы";
    private static final String CLASS_FORMAT = "Класс %s";
    private static final int IMAGE_WIDTH = 650;
    private static final int IMAGE_HEIGHT = 500;
    private static final Dimension PLOT_BOX_DIM = new Dimension(300, 25);

    private final RocCurve rocCurve;
    private ChartPanel chartPanel;
    private JFreeChart[] plots;
    private JFrame[] dataFrames;
    private JComboBox<String> plotBox;
    private final JFrame parentFrame;

    public ROCCurvePanel(RocCurve rocCurve, JFrame parentFrame, final int digits) {
        this.rocCurve = rocCurve;
        this.parentFrame = parentFrame;
        this.createPlots();
        this.createFrames();
        this.setLayout(new GridBagLayout());
        plotBox = new JComboBox<>();
        plotBox.setPreferredSize(PLOT_BOX_DIM);
        plotBox.setMaximumSize(PLOT_BOX_DIM);
        plotBox.setMinimumSize(PLOT_BOX_DIM);
        Attribute classAttr = rocCurve.getData().classAttribute();
        for (Enumeration i = classAttr.enumerateValues(); i.hasMoreElements(); ) {
            plotBox.addItem(String.format(CLASS_FORMAT, i.nextElement()));
        }
        plotBox.addItem(ALL_CLASSES_TEXT);
        plotBox.setSelectedIndex(plots.length - 1);
        plotBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                chartPanel.setChart(plots[plotBox.getSelectedIndex()]);
            }
        });

        JMenuItem dataMenu = new JMenuItem(SHOW_DATA_MENU_TEXT);
        dataMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int i = plotBox.getSelectedIndex();
                if (i < plots.length - 1) {
                    if (dataFrames[i] == null) {
                        dataFrames[i] = new DataFrame(rocCurve.getROCCurve(i), digits,
                                rocCurve.getData().classAttribute().value(i));
                    }
                    dataFrames[i].setVisible(true);
                }
            }
        });

        chartPanel.getPopupMenu().addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                dataMenu.setEnabled(plotBox.getSelectedIndex() < plots.length - 1);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        chartPanel.getPopupMenu().add(dataMenu);
        parentFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                for (JFrame frame : dataFrames) {
                    if (frame != null) {
                        frame.dispose();
                    }
                }
            }
        });
        this.add(chartPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        this.add(plotBox, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
    }

    private void createFrames() {
        dataFrames = new DataFrame[rocCurve.getData().numClasses()];
    }

    private void createPlots() {
        plots = new JFreeChart[rocCurve.getData().numClasses() + 1];
        XYSeriesCollection allPlots = new XYSeriesCollection();

        for (int i = 0; i < rocCurve.getData().numClasses(); i++) {
            Instances rocSet = rocCurve.getROCCurve(i);
            XYSeriesCollection plot = new XYSeriesCollection();
            XYSeries points = new XYSeries(rocCurve.getData().classAttribute().value(i));
            for (int j = 0; j < rocSet.numInstances(); j++) {
                Instance obj = rocSet.instance(j);
                points.add(obj.value(4) * 100, obj.value(5) * 100);
            }
            plot.addSeries(points);
            allPlots.addSeries(points);
            plots[i] = ChartFactory.createXYLineChart(TITLE, X_AXIS_TITLE, Y_AXIS_TITLE,
                    plot, PlotOrientation.VERTICAL, true, true, false);
        }
        plots[plots.length - 1] = ChartFactory.createXYLineChart(TITLE, X_AXIS_TITLE, Y_AXIS_TITLE,
                        allPlots, PlotOrientation.VERTICAL, true, true, false);
        chartPanel = new ChartPanel(plots[plots.length - 1]);
    }

    public Image createImage() {
        JFreeChart chart = chartPanel.getChart();
        return chart.createBufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    /**
     * Roc - curve data frame.
     */
    private class DataFrame extends JFrame {

        DataFrame(Instances data, int digits, String className) {
            this.setTitle(ROC_CURVE_DATA_TITLE);
            this.setLayout(new GridBagLayout());
            this.setIconImage(parentFrame.getIconImage());
            ROCThresholdTable table = new ROCThresholdTable(data, digits, className);
            JScrollPane scrollPanel = new JScrollPane(table);
            JButton okButton = ButtonUtils.createOkButton();

            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    setVisible(false);
                }
            });
            //----------------------------------------
            this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(4, 0, 4, 0), 0, 0));
            //----------------------------------------
            this.pack();
            this.setLocationRelativeTo(parentFrame);
            this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
    }

}
