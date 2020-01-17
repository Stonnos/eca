package eca.gui.frames;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.config.registry.SingletonRegistry;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.choosers.HtmlChooser;
import eca.gui.logging.LoggerUtils;
import eca.gui.tables.ContingencyJTable;
import eca.report.ReportGenerator;
import eca.report.contingency.ContingencyTableReportModel;
import eca.report.contingency.ContingencyTableReportService;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Implements contingency table results frame.
 *
 * @author Roman Batygin
 */
@Slf4j
public class ContingencyTableResultFrame extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String TITLE_TEXT = "Таблица сопряженности (Результаты)";
    private static final String SAVE_RESULTS_BUTTON_TEXT = "Сохранить";
    private static final String CONTENT_TYPE = "text/html";
    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 500;
    private static final Dimension TABLE_SCROLL_PANE_PREFERRED_SIZE = new Dimension(900, 300);
    private static final Dimension CHI_SQUARED_PANE_PREFERRED_SIZE = new Dimension(900, 125);
    private static final Dimension SAVE_BUTTON_DIM = new Dimension(150, 25);
    private static final String CONTINGENCY_TABLE_TITLE = "Таблица сопряженности";
    private static final String CHI_SQUARE_RESULTS_TEXT =
            "<html><body>Результаты теста &chi;&sup2;</body></html>";
    private static final String SELECTED_FILE_FORMAT = "%s%d";
    private static final String CONTINGENCY_TABLE_DEFAULT_FILE_PREFIX = "ContingencyTable";

    private ContingencyTableReportModel contingencyTableReportModel;

    /**
     * Constructor with params.
     *
     * @param parent                      - parent frame
     * @param contingencyTableReportModel - contingency table report model
     */
    public ContingencyTableResultFrame(JFrame parent,
                                       ContingencyTableReportModel contingencyTableReportModel) {
        this.contingencyTableReportModel = contingencyTableReportModel;
        this.setTitle(TITLE_TEXT);
        this.setLayout(new GridBagLayout());
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setIconImage(parent.getIconImage());
        this.init();
        this.setLocationRelativeTo(parent);
    }

    private void init() {
        JScrollPane contingencyTableScrollPanel = new JScrollPane(
                new ContingencyJTable(contingencyTableReportModel.getRowAttribute(),
                        contingencyTableReportModel.getColAttribute(),
                        contingencyTableReportModel.getContingencyMatrix()));
        contingencyTableScrollPanel.setBorder(PanelBorderUtils.createTitledBorder(CONTINGENCY_TABLE_TITLE));
        contingencyTableScrollPanel.setPreferredSize(TABLE_SCROLL_PANE_PREFERRED_SIZE);

        JTextPane chiSquaredResultPane = new JTextPane();
        chiSquaredResultPane.setContentType(CONTENT_TYPE);
        chiSquaredResultPane.setPreferredSize(CHI_SQUARED_PANE_PREFERRED_SIZE);
        chiSquaredResultPane.setEditable(false);
        chiSquaredResultPane.setText(ReportGenerator.getChiSquareTestResultAsHtml(contingencyTableReportModel));
        chiSquaredResultPane.setCaretPosition(0);
        JScrollPane chiSquaredResultScrollPanel = new JScrollPane(chiSquaredResultPane);
        chiSquaredResultScrollPanel.setMinimumSize(CHI_SQUARED_PANE_PREFERRED_SIZE);
        chiSquaredResultScrollPanel.setBorder(PanelBorderUtils.createTitledBorder(CHI_SQUARE_RESULTS_TEXT));

        JButton closeButton = ButtonUtils.createCloseButton();

        JButton saveButton = new JButton(SAVE_RESULTS_BUTTON_TEXT);
        saveButton.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.SAVE_ICON)));
        saveButton.setPreferredSize(SAVE_BUTTON_DIM);
        saveButton.setMinimumSize(SAVE_BUTTON_DIM);
        saveButton.setMaximumSize(SAVE_BUTTON_DIM);

        closeButton.addActionListener(evt -> setVisible(false));

        addSaveButtonListener(saveButton);

        this.add(contingencyTableScrollPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 10, 5), 0, 0));
        this.add(chiSquaredResultScrollPanel, new GridBagConstraints(0, 1, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 10, 5), 0, 0));
        this.add(saveButton, new GridBagConstraints(0, 2, 1, 1, 1, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(4, 0, 10, 3), 0, 0));
        this.add(closeButton, new GridBagConstraints(1, 2, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(4, 3, 10, 0), 0, 0));
        this.pack();
    }

    private void addSaveButtonListener(JButton saveButton) {
        saveButton.addActionListener(new ActionListener() {

            ContingencyTableReportService contingencyTableReportService;

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    HtmlChooser fileChooser = SingletonRegistry.getSingleton(HtmlChooser.class);
                    fileChooser.setSelectedFile(new File(
                            String.format(SELECTED_FILE_FORMAT, CONTINGENCY_TABLE_DEFAULT_FILE_PREFIX,
                                    System.currentTimeMillis())));
                    File file = fileChooser.getSelectedFile(ContingencyTableResultFrame.this);
                    if (file != null) {
                        if (contingencyTableReportService == null) {
                            contingencyTableReportService = new ContingencyTableReportService();
                            contingencyTableReportService.setFile(file);
                            contingencyTableReportService.setContingencyTableReportModel(contingencyTableReportModel);

                        }
                        contingencyTableReportService.saveReport();
                    }
                } catch (Exception ex) {
                    LoggerUtils.error(log, ex);
                    JOptionPane.showMessageDialog(ContingencyTableResultFrame.this, ex.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
