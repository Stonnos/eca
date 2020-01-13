package eca.gui.frames;

import eca.gui.ButtonUtils;
import eca.gui.EvaluationResultsHistoryModel;
import eca.gui.frames.results.ClassificationResultsFrameBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Classification results history frame.
 *
 * @author Roman Batygin
 */

public class ClassificationResultHistoryFrame extends JFrame {

    private static final String TITLE_TEXT = "История классификаторов";

    private EvaluationResultsHistoryModel evaluationResultsHistory;

    private JList<String> historyList;

    public ClassificationResultHistoryFrame(JFrame parent, EvaluationResultsHistoryModel evaluationResultsHistory) {
        this.setIconImage(parent.getIconImage());
        this.evaluationResultsHistory = evaluationResultsHistory;
        this.createGUI();
        this.setLocationRelativeTo(parent);
    }

    public void addItem(ClassificationResultsFrameBase classificationResultsFrameBase) {
        this.evaluationResultsHistory.add(classificationResultsFrameBase);
    }

    private void createGUI() {
        this.setResizable(false);
        this.setTitle(TITLE_TEXT);
        this.setLayout(new GridBagLayout());
        JPanel infoPanel = new JPanel();
        Dimension dim = new Dimension(430, 300);
        historyList = new JList<>(evaluationResultsHistory);
        historyList.setMinimumSize(dim);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane historyPane = new JScrollPane(historyList);
        historyPane.setPreferredSize(dim);
        historyPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        historyPane.setCursor(new Cursor(Cursor.HAND_CURSOR));

        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !evaluationResultsHistory.isEmpty()) {
                    int i = historyList.locationToIndex(e.getPoint());
                    evaluationResultsHistory.getClassificationResultsFrame(i).setVisible(true);
                }
            }

        });

        infoPanel.add(historyPane);

        JButton closeButton = ButtonUtils.createCloseButton();

        closeButton.addActionListener(e -> setVisible(false));
        add(infoPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(closeButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        this.pack();
    }

}

