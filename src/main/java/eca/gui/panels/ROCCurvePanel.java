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
 * @author Рома
 */
public class ROCCurvePanel extends JPanel {

    private static final String TITLE = "График ROC кривой";
    private static final String X_AXIS_TITLE = "100 - Специфичность (Specificity), %";
    private static final String Y_AXIS_TITLE = "Чувствительность (Sensitivity), %";

    private final RocCurve curve;
    private ChartPanel panel;
    private JFreeChart[] plots;
    private JFrame[] frames;
    private JComboBox<String> plotBox;
    private final JFrame frame;

    public ROCCurvePanel(RocCurve curve, JFrame frame, final int digits) {
        this.curve = curve;
        this.frame = frame;
        this.createPlots();
        this.createFrames();
        this.setLayout(new GridBagLayout());
        //---------------------------------
        plotBox = new JComboBox<>();
        Dimension dim = new Dimension(300, 25);
        plotBox.setPreferredSize(dim);
        plotBox.setMaximumSize(dim);
        plotBox.setMinimumSize(dim);
        Attribute classAttr = curve.getData().classAttribute();
        for (Enumeration i = classAttr.enumerateValues(); i.hasMoreElements(); ) {
            plotBox.addItem("Класс " + i.nextElement());
        }
        plotBox.addItem("Все классы");
        plotBox.setSelectedIndex(plots.length - 1);
        plotBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                panel.setChart(plots[plotBox.getSelectedIndex()]);
            }
        });
        //---------------------------------
        JMenuItem dataMenu = new JMenuItem("Показать данные");
        dataMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int i = plotBox.getSelectedIndex();
                if (i < plots.length - 1) {
                    if (frames[i] == null) {
                        frames[i] = new DataFrame(curve.getROCCurve(i), digits,
                                curve.getData().classAttribute().value(i));
                    }
                    frames[i].setVisible(true);
                }
            }
        });
        //-----------------------------------------
        panel.getPopupMenu().addPopupMenuListener(new PopupMenuListener() {

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
        panel.getPopupMenu().add(dataMenu);
        //--------------------------------
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                for (JFrame frame : frames) {
                    if (frame != null) {
                        frame.dispose();
                    }
                }
            }
        });
        //---------------------------------
        this.add(panel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        this.add(plotBox, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
    }

    private void createFrames() {
        frames = new DataFrame[curve.getData().numClasses()];
    }

    private void createPlots() {
        plots = new JFreeChart[curve.getData().numClasses() + 1];
        XYSeriesCollection allPlots = new XYSeriesCollection();

        for (int i = 0; i < curve.getData().numClasses(); i++) {
            Instances rocSet = curve.getROCCurve(i);
            XYSeriesCollection plot = new XYSeriesCollection();
            XYSeries points = new XYSeries(curve.getData().classAttribute().value(i));
            for (int j = 0; j < rocSet.numInstances(); j++) {
                Instance obj = rocSet.instance(j);
                points.add(obj.value(4) * 100, obj.value(5) * 100);
            }
            plot.addSeries(points);
            allPlots.addSeries(points);
            plots[i] = ChartFactory
                    .createXYLineChart(TITLE, X_AXIS_TITLE, Y_AXIS_TITLE,
                            plot,
                            PlotOrientation.VERTICAL,
                            true, true, false);
        }
        plots[plots.length - 1] = ChartFactory
                .createXYLineChart(TITLE, X_AXIS_TITLE, Y_AXIS_TITLE,
                        allPlots,
                        PlotOrientation.VERTICAL,
                        true, true, false);

        panel = new ChartPanel(plots[plots.length - 1]);
    }

    public Image createImage() {
        JFreeChart chart = panel.getChart();
        return chart.createBufferedImage(650, 500);
    }

    /**
     *
     */
    private class DataFrame extends JFrame {

        DataFrame(Instances data, int digits, String className) {
            this.setTitle("Данные ROC - кривой");
            this.setLayout(new GridBagLayout());
            this.setIconImage(frame.getIconImage());
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
            this.setLocationRelativeTo(frame);
            this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
    }

}
