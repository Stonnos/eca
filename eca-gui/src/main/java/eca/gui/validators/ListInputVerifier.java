package eca.gui.validators;

import javax.swing.*;

/**
 * @author Roman Batygin
 */
public class ListInputVerifier extends TextFieldInputVerifier {

    @Override
    public boolean verify(JComponent input) {
        boolean verify = super.verify(input);

        JTextField textField = (JTextField) input;

        String text = textField.getText().trim();
        if (!text.isEmpty() && text.endsWith(",")) {
            textField.setText(text.substring(0, text.length() - 1));
        }

        return verify;
    }
}
