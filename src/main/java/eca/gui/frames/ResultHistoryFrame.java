package eca.gui.frames;

import eca.gui.dialogs.BaseOptionsDialog;
import eca.gui.dialogs.DecisionTreeOptionsDialog;
import eca.gui.dialogs.EnsembleOptionsDialog;
import eca.gui.dialogs.KNNOptionDialog;
import eca.gui.dialogs.LogisticOptionsDialogBase;
import eca.gui.dialogs.NetworkOptionsDialog;
import eca.gui.enums.ClassifiersNames;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import weka.classifiers.Classifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * @author Roman Batygin
 */

public class ResultHistoryFrame extends JFrame {

    private JMainFrame.ResultsHistory historyModel;

    private JList<String> historyList;

    public ResultHistoryFrame(JFrame parent, JMainFrame.ResultsHistory historyModel) {
        this.setIconImage(parent.getIconImage());
        this.historyModel = historyModel;
        this.makeGUI();
        this.setLocationRelativeTo(parent);
    }


    private void makeGUI() {
        this.setResizable(false);
        this.setTitle("История классификаторов");
        this.setLayout(new GridBagLayout());

        JPanel infoPanel = new JPanel();

        Dimension dim = new Dimension(430, 300);
        historyList = new JList<>(historyModel);
        historyList.setMinimumSize(dim);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //-------------------------------------------------
        JScrollPane historyPane = new JScrollPane(historyList);
        historyPane.setPreferredSize(dim);
        historyPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        historyPane.setCursor(new Cursor(Cursor.HAND_CURSOR));

        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int i = historyList.locationToIndex(e.getPoint());
                    historyModel.getFrame(i).setVisible(true);
                }
            }

        });

        infoPanel.add(historyPane);

        JButton okButton = new JButton("Закрыть");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        add(infoPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //-----------------------------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
        this.pack();
    }

}

