/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author Рома
 */
public class LengthDocument extends PlainDocument {

    private int length;

    public LengthDocument(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be positive!");
        }
        this.length = length;
    }

    public int length() {
        return length;
    }

    public boolean format(String str) {
        return true;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (this.getContent().length() <= length()) {
            super.insertString(offs, str, a);
        }
    }

}
