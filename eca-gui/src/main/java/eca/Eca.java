package eca;

import com.formdev.flatlaf.FlatLightLaf;
import eca.config.ConfigurationService;
import eca.gui.frames.JMainFrame;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * Main class.
 *
 * @author Roman Batygin
 */
@Slf4j
public class Eca {

    private static final ConfigurationService CONFIG_SERVICE = ConfigurationService.getApplicationConfigService();

    public static void main(String[] args) throws Exception {
        // Sets Look and feel UI theme
        UIManager.setLookAndFeel(new FlatLightLaf());
        // Register FontAwesome icon fonts
        IconFontSwing.register(FontAwesome.getIconFont());
        EventQueue.invokeLater(() -> {
            JMainFrame mainFrame = new JMainFrame();
            mainFrame.setVisible(true);
            mainFrame.initializeMessageListenerContainer();
            mainFrame.initializeUploadInstancesClient();
            log.info("Eca application was started. Application version: {}",
                    CONFIG_SERVICE.getApplicationConfig().getProjectInfo().getVersion());
            MemoryMonitor.INSTANCE.start();
        });

    }
}
