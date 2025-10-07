package eca.gui.backgroundtasks;

import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BackgroundTasksManager {

    private static final int POPUP_MARGIN_RIGHT = 25;
    private static final int POPUP_MARGIN_TOP = 50;

    private static final Dimension TASKS_LIST_DIMENSION = new Dimension(500, 200);
    private static final String BACKGROUND_TASKS_LABEL_TEXT = "Фоновые процессы";

    private final PopupFactory popupFactory = new PopupFactory();

    private Popup tasksListPopup;

    private JPanel infoPanel;
    private final Component component;


    private final BackgroundTasksListModel backgroundTasksListModel = new BackgroundTasksListModel();

    public BackgroundTasksManager(Component component) {
        this.component = component;
        createTasksListInfoPanel();
    }

    public void addTask(BackgroundTaskInfo taskInfo) {
        backgroundTasksListModel.add(taskInfo);
    }

    public void removeTask(String id) {
        backgroundTasksListModel.removeItem(id);
    }

    public void show() {
        int x = component.getX() + POPUP_MARGIN_RIGHT;
        int y = component.getY() + POPUP_MARGIN_TOP;
        tasksListPopup = popupFactory.getPopup(component, infoPanel, x, y);
        tasksListPopup.show();
    }

    public void hide() {
        tasksListPopup.hide();
    }

    private void createTasksListInfoPanel() {
        infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(PanelBorderUtils.createEtchedBorder());

        JLabel messageLabel = new JLabel(BACKGROUND_TASKS_LABEL_TEXT);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD));
        JPanel tasksListPanel = createTasksListScrollPanel();
        JButton closeButton = ButtonUtils.createCloseButton();

        infoPanel.add(messageLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.CENTER,
                new Insets(0, 0, 0, 0), 0, 0));
        infoPanel.add(tasksListPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        infoPanel.add(closeButton, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(4, 0, 4, 0), 0, 0));
        closeButton.addActionListener(e -> hide());
    }

    private JPanel createTasksListScrollPanel() {
        JPanel tasksListPanel = new JPanel();
        JList<String> tasksList = createTasksList();
        JScrollPane tasksPane = new JScrollPane(tasksList);
        tasksPane.setPreferredSize(TASKS_LIST_DIMENSION);
        tasksPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tasksPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tasksListPanel.add(tasksPane);
        return tasksListPanel;
    }

    private JList<String> createTasksList() {
        JList<String> tasksList = new JList<>(backgroundTasksListModel);
        tasksList.setMinimumSize(TASKS_LIST_DIMENSION);
        tasksList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tasksList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !backgroundTasksListModel.isEmpty()) {
                    int i = tasksList.locationToIndex(e.getPoint());
                    backgroundTasksListModel.getTask(i).showInfo();
                }
            }

        });
        return tasksList;
    }
}
