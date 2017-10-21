package eca.gui.validators;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author Roman Batygin
 */
public class TextFieldInputVerifier extends InputVerifier {

    private static final Color ERROR_COLOR = Color.RED;
    private static final int THICKNESS = 2;

    private Border defaultBorder;

    @Override
    public boolean verify(JComponent input) {
        JTextField textField = (JTextField) input;
        if (defaultBorder == null) {
            defaultBorder = textField.getBorder();
        }

        if (textField.getText().trim().isEmpty()) {
            textField.setText(StringUtils.EMPTY);
            textField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, THICKNESS));
            return false;
        } else {
            textField.setBorder(defaultBorder);
        }
        return true;
    }

    public boolean shouldYieldFocus(JComponent input) {
        verify(input);
        return true;
    }
}
