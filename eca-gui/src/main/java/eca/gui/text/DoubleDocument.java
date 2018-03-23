/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.util.regex.Pattern;

/**
 * @author Roman Batygin
 */
public class DoubleDocument extends LengthDocument {

    public static final String DOUBLE_FORMAT = "^[-]?[0-9]*[,]?[0-9]*$";

    public DoubleDocument(int length) {
        super(length);
    }

    @Override
    public boolean format(String str) {
        return Pattern.compile(DOUBLE_FORMAT).matcher(str).matches();
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        Content c = this.getContent();
        if (format(c.getString(0, c.length() - 1) + str)) {
            super.insertString(offs, str, a);
        }
    }
}
