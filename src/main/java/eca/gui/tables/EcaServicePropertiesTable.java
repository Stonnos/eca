package eca.gui.tables;

import eca.gui.tables.models.ClassifyInstanceTableModel;
import eca.gui.tables.models.EcaServiceOptionsTableModel;
import eca.gui.text.DoubleDocument;
import eca.gui.text.LengthDocument;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author Roman Batygin
 */

public class EcaServicePropertiesTable extends JDataTableBase {

    private static final int FIELD_LENGTH = 15;

    private static final int MAX_FIELD_LENGTH = 255;

    public EcaServicePropertiesTable(EcaServiceOptionsTableModel model) {
        super(model);

        TableColumn column = this.getColumnModel().getColumn(1);
        JTextField text = new JTextField(FIELD_LENGTH);
        text.setDocument(new LengthDocument(MAX_FIELD_LENGTH));
        column.setCellEditor(new DefaultCellEditor(text));

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                Point p = new Point(evt.getX(), evt.getY());
                int i = rowAtPoint(p);
                int j = columnAtPoint(p);
                changeSelection(i, j, false, false);
            }
        });
        this.setAutoResizeOff(false);
    }
}
