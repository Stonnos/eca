package eca.gui.tables;

import lombok.experimental.UtilityClass;

import javax.swing.*;

/**
 * @author Roman Batygin
 */
@UtilityClass
public class JTableConverter {

    public static String convertToString(JTable table, boolean copyHeaders) {
        StringBuilder str = new StringBuilder();

        if (copyHeaders) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                str.append(table.getColumnName(i));
                if (i < table.getColumnCount() - 1) {
                    str.append("\t");
                }
            }
            str.append("\n");
        }

        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                str.append(table.getValueAt(i, j));
                if (j < table.getColumnCount() - 1) {
                    str.append("\t");
                }
            }
            str.append("\n");
        }

        return str.toString();
    }
}
