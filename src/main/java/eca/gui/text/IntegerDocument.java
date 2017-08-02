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
 *
 * @author Рома
 */
public class IntegerDocument extends LengthDocument {

    public static final String INT_FORMAT = "^[0-9]*$";

    public IntegerDocument(int length) {
        super(length);
    }

    @Override
    public boolean format(String str) {
        return Pattern.compile(INT_FORMAT).matcher(str).matches();
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (format(str)) {
            super.insertString(offs, str, a);
        }
    }

}
