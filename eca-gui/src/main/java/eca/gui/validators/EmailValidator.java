package eca.gui.validators;

/**
 * E-mail validator.
 *
 * @author Roman Batygin
 */
public class EmailValidator extends Validator {

    private static final String EMAIL_REGEX =
            "^|([_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))$";

    public EmailValidator() {
        super(EMAIL_REGEX);
    }

}
