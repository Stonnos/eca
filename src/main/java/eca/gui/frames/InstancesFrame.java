package eca.gui.frames;

import eca.gui.ButtonUtils;
import eca.gui.tables.ResultInstancesTable;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Roman Batygin
 */

public class InstancesFrame extends JDialog {

    private Instances data;

    public InstancesFrame(Instances data, Window parent) {
        super(parent, "Данные: " + data.relationName());
        this.data = data;
        this.setLayout(new GridBagLayout());
        //----------------------------------------------
        JScrollPane scrollPanel = new JScrollPane(new ResultInstancesTable(data));
        JButton okButton = ButtonUtils.createOkButton();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        //----------------------------------------
        this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(4, 0, 4, 0), 0, 0));
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public Instances getData() {
        return data;
    }
}
