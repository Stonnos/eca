package eca.gui.validators;

/**
 * First name validator.
 *
 * @author Roman Batygin
 */
public class FirstNameValidator extends Validator {

    private static final String FIRST_NAME_REGEX = "^([A-Z][a-z]+)|([А-Я][а-я]+)$";

    public FirstNameValidator() {
        super(FIRST_NAME_REGEX);
    }
}
