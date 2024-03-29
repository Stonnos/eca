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
public class ListDocument extends LengthDocument {

    public ListDocument(int length) {
        super(length);
    }

    @Override
    public boolean format(String str) {
        return Pattern.compile("^([0-9],?)+$").matcher(str).matches();
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        Content c = this.getContent();
        StringBuilder stringBuilder = new StringBuilder(c.getString(0, c.length() - 1));
        String resultStr = stringBuilder.insert(offs, str).toString();
        if (format(resultStr)) {
            super.insertString(offs, str, a);
        }
    }
}
