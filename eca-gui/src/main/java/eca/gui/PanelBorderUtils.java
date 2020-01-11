/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui;


import lombok.experimental.UtilityClass;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Roman Batygin
 */
@UtilityClass
public class PanelBorderUtils {

    private static final Font BORDER_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Color COLOR = new Color(133, 133, 133);

    public static TitledBorder createTitledBorder(String title) {
        TitledBorder border = new TitledBorder(title);
        border.setBorder(new EtchedBorder(COLOR, null));
        border.setTitlePosition(TitledBorder.TOP);
        border.setTitleFont(BORDER_FONT);
        border.setTitleJustification(TitledBorder.CENTER);
        return border;
    }

}
