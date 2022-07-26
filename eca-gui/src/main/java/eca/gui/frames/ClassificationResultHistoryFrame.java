package eca.gui.frames;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.gui.ButtonUtils;
import eca.gui.EvaluationResultsHistoryModel;
import eca.gui.frames.results.ClassificationResultsFrameBase;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Classification results history frame.
 *
 * @author Roman Batygin
 */
public class ClassificationResultHistoryFrame extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String TITLE_TEXT = "История классификаторов";
    private static final String DELETE_ATTR_MENU_TEXT = "Удалить";
    private static final String CLEAR_DATA_MENU_TEXT = "Очистка";
    private static final Dimension LIST_DIMENSION = new Dimension(600, 300);
    private static final Dimension POPUP_DIMENSION = new Dimension(150, 50);

    private EvaluationResultsHistoryModel evaluationResultsHistory;

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
        JList<String> historyList = createHistoryList();
        createPopMenu(historyList);
        JScrollPane historyPane = new JScrollPane(historyList);
        historyPane.setPreferredSize(LIST_DIMENSION);
        historyPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        historyPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
        infoPanel.add(historyPane);
        JButton closeButton = ButtonUtils.createCloseButton();
        closeButton.addActionListener(e -> setVisible(false));
        add(infoPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(closeButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        this.pack();
    }

    private JList<String> createHistoryList() {
        JList<String> historyList = new JList<>(evaluationResultsHistory);
        historyList.setMinimumSize(LIST_DIMENSION);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !evaluationResultsHistory.isEmpty()) {
                    int i = historyList.locationToIndex(e.getPoint());
                    evaluationResultsHistory.getClassificationResultsFrame(i).setVisible(true);
                }
            }

        });
        return historyList;
    }

    private void createPopMenu(JList<String> historyList) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setPopupSize(POPUP_DIMENSION);
        JMenuItem deleteMenu = new JMenuItem(DELETE_ATTR_MENU_TEXT);
        deleteMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.DELETE_ICON)));
        JMenuItem clearMenu = new JMenuItem(CLEAR_DATA_MENU_TEXT);
        clearMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.CLEAR_ICON)));
        popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                deleteMenu.setEnabled(historyList.getSelectedIndex() >= 0);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Not implemented
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Not implemented
            }
        });
        deleteMenu.addActionListener(e -> {
            int i = historyList.getSelectedIndex();
            evaluationResultsHistory.removeItem(i);
        });
        clearMenu.addActionListener(e -> evaluationResultsHistory.removeAllItems());
        popupMenu.add(deleteMenu);
        popupMenu.add(clearMenu);
        historyList.setComponentPopupMenu(popupMenu);
    }
}

