package eca.gui.tables;

import eca.gui.Cleanable;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;

public class PaginatedTable extends JPanel implements Cleanable {

    private static final int ICON_SIZE = 16;
    private static final String PAGE_NUMBER_TEXT_FORMAT = "Страница %d из %d";
    private static final String PREV_PAGE_TOOLTIP_TEXT = "Предыдущая";
    private static final String FIRST_PAGE_TOOLTIP_TEXT = "Первая";
    private static final String NEXT_PAGE_TOOLTIP_TEXT = "Следующая";
    private static final String LAST_PAGE_TOOLTIP_TEXT = "Последняя";
    private static final int FIRST_PAGE = 1;

    private JLabel paneNumberLabel;
    private PageableTable pageableTable;

    private JButton prevPageButton;
    private JButton nextPageButton;
    private JButton firstPageButton;
    private JButton lastPageButton;

    public PaginatedTable(JScrollPane tableScrollPane, PageableTable pageableTable) {
        this.setLayout(new GridBagLayout());
        this.pageableTable = pageableTable;
        JPanel paginatedControlPanel = createPaginatedControlPanel();

        this.add(tableScrollPane, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(paginatedControlPanel, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(4, 0, 4, 0), 0, 0));
    }

    private JPanel createPaginatedControlPanel() {
        JPanel paginatedControlPanel = new JPanel();
        paginatedControlPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        paneNumberLabel = new JLabel();
        paneNumberLabel.setFont(paneNumberLabel.getFont().deriveFont(Font.BOLD));
        prevPageButton = new JButton();
        prevPageButton.setIcon(IconFontSwing.buildIcon(FontAwesome.BACKWARD, ICON_SIZE));
        prevPageButton.setToolTipText(PREV_PAGE_TOOLTIP_TEXT);


        firstPageButton = new JButton();
        firstPageButton.setIcon(IconFontSwing.buildIcon(FontAwesome.FAST_BACKWARD, ICON_SIZE));
        firstPageButton.setToolTipText(FIRST_PAGE_TOOLTIP_TEXT);

        nextPageButton = new JButton();
        nextPageButton.setIcon(IconFontSwing.buildIcon(FontAwesome.FORWARD, ICON_SIZE));
        nextPageButton.setToolTipText(NEXT_PAGE_TOOLTIP_TEXT);

        lastPageButton = new JButton();
        lastPageButton.setIcon(IconFontSwing.buildIcon(FontAwesome.FAST_FORWARD, ICON_SIZE));
        lastPageButton.setToolTipText(LAST_PAGE_TOOLTIP_TEXT);

        prevPageButton.addActionListener(event -> {
            pageableTable.previousPage();
            updateControls(pageableTable.getPage(), pageableTable.totalPages());
        });
        firstPageButton.addActionListener(event -> {
            pageableTable.firstPage();
            updateControls(pageableTable.getPage(), pageableTable.totalPages());
        });
        nextPageButton.addActionListener(event -> {
            pageableTable.nextPage();
            updateControls(pageableTable.getPage(), pageableTable.totalPages());

        });
        lastPageButton.addActionListener(event -> {
            pageableTable.lastPage();
            updateControls(pageableTable.getPage(), pageableTable.totalPages());
        });
        pageableTable.addDataChangeActionListener(event -> updateControls(pageableTable.getPage(), pageableTable.totalPages()));

        paginatedControlPanel.add(firstPageButton);
        paginatedControlPanel.add(prevPageButton);
        paginatedControlPanel.add(paneNumberLabel);
        paginatedControlPanel.add(nextPageButton);
        paginatedControlPanel.add(lastPageButton);
        updateControls(pageableTable.getPage(), pageableTable.totalPages());
        return paginatedControlPanel;
    }

    private void updateControls(int page, int totalPages) {
        paneNumberLabel.setText(String.format(PAGE_NUMBER_TEXT_FORMAT, page, totalPages));
        lastPageButton.setEnabled(page < totalPages);
        nextPageButton.setEnabled(page < totalPages);
        prevPageButton.setEnabled(page > FIRST_PAGE);
        firstPageButton.setEnabled(page > FIRST_PAGE);
    }

    @Override
    public void clear() {
        pageableTable = null;
    }
}
