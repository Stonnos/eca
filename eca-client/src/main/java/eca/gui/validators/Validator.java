package eca.gui.validators;

import java.util.regex.Pattern;

/**
 * Validator interface.
 *
 * @author Roman batygin
 */
public class Validator {

    private Pattern pattern;

    public Validator(String regex) {
        pattern = Pattern.compile(regex);
    }

    /**
     * Validates string.
     *
     * @param val value string
     * @return <tt>true</tt> if specified value is true
     */
    public boolean validate(String val) {
        return pattern.matcher(val).matches();
    }
}
