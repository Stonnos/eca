package eca.gui.frames;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.config.registry.SingletonRegistry;
import eca.data.file.FileDataSaver;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.choosers.SaveDataFileChooser;
import eca.gui.logging.LoggerUtils;
import eca.gui.tables.ResultInstancesTable;
import lombok.extern.slf4j.Slf4j;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static eca.gui.GuiUtils.removeComponents;

/**
 * @author Roman Batygin
 */
@Slf4j
public class InstancesFrame extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String DATA_FORMAT = "Данные: %s";
    private static final String SAVE_FILE_MENU_TEXT = "Сохранить...";
    private static final String FILE_MENU_TEXT = "Файл";

    private Instances data;

    public InstancesFrame(Instances data, Window parent) {
        this.data = data;
        this.setTitle(String.format(DATA_FORMAT, data.relationName()));
        this.setLayout(new GridBagLayout());
        this.createMenuBar();
        GuiUtils.setIcon(this, CONFIG_SERVICE.getIconUrl(IconType.MAIN_ICON));
        JScrollPane scrollPanel = new JScrollPane(new ResultInstancesTable(data));
        JButton closeButton = ButtonUtils.createCloseButton();
        closeButton.addActionListener(e -> setVisible(false));
        this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(closeButton, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(4, 0, 4, 0), 0, 0));
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    @Override
    public void dispose() {
        data = null;
        removeComponents(this);
        super.dispose();
    }

    public Instances getData() {
        return data;
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(FILE_MENU_TEXT);
        addSaveDataMenuItem(fileMenu);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);
    }

    private void addSaveDataMenuItem(JMenu menu) {
        JMenuItem saveFileMenu = new JMenuItem(SAVE_FILE_MENU_TEXT);
        saveFileMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.SAVE_ICON)));
        saveFileMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));

        saveFileMenu.addActionListener(event -> {
            try {
                SaveDataFileChooser fileChooser = SingletonRegistry.getSingleton(SaveDataFileChooser.class);
                fileChooser.setSelectedFile(new File(getData().relationName()));
                File file = fileChooser.getSelectedFile(InstancesFrame.this);
                if (file != null) {
                    FileDataSaver dataSaver = new FileDataSaver();
                    dataSaver.setDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());
                    dataSaver.saveData(file, getData());
                }
            } catch (Exception e) {
                LoggerUtils.error(log, e);
                JOptionPane.showMessageDialog(InstancesFrame.this, e.getMessage(),
                        null, JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(saveFileMenu);
    }
}
