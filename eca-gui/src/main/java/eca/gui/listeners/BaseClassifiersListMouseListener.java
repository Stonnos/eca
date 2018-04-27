package eca.gui.listeners;

import eca.gui.BaseClassifiersListModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Roman Batygin
 */

public class BaseClassifiersListMouseListener extends MouseAdapter {

    private static final int CLICK_COUNT = 2;

    private JList<String> list;
    private BaseClassifiersListModel baseClassifiersListModel;

    public BaseClassifiersListMouseListener(JList<String> list, BaseClassifiersListModel baseClassifiersListModel) {
        this.list = list;
        this.baseClassifiersListModel = baseClassifiersListModel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == CLICK_COUNT && !baseClassifiersListModel.isEmpty()) {
            int i = list.locationToIndex(e.getPoint());
            if (baseClassifiersListModel.getWindow(i) != null) {
                baseClassifiersListModel.getWindow(i).showDialog();
            }
        }
    }
}
