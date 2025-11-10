package eca.gui.backgroundtasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

/**
 * Background task info.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
@AllArgsConstructor
public class BackgroundTaskInfo {

    private String id;

    private String title;

    private JDialog dialog;

    /**
     * Shows info
     */
    public void showInfo() {
        dialog.setVisible(true);
    }
}
