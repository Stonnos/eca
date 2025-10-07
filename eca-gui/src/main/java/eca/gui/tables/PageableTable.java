package eca.gui.tables;

import java.awt.event.ActionListener;

public interface PageableTable {

    int getPage();

    int totalPages();

    int pageSize();

    void nextPage();

    void previousPage();

    void firstPage();

    void lastPage();

    void addDataChangeActionListener(ActionListener actionListener);
}
