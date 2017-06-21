/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui;


import java.awt.Color;
import java.awt.Font;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
/**
 *
 * @author Рома
 */
public class PanelBorderUtils {
    
    private static final Font BORDER_FONT = new Font("Arial", 1, 14);
    private static final Color COLOR = new Color(133,133,133);
    
    public static final TitledBorder createTitledBorder(String title) {
        TitledBorder border = new TitledBorder(title);
        border.setBorder(new EtchedBorder(COLOR, null));
        border.setTitlePosition(TitledBorder.CENTER);
        border.setTitleFont(BORDER_FONT);
        border.setTitleJustification(TitledBorder.TOP);
        return border;
    }
    
}
