package eca.client.converter;

/**
 * Message converter interface.
 *
 * @author Roman Batygin
 */
public interface MessageConverter {

    /**
     * Converts object to message bytes array.
     *
     * @param object - message object.
     * @param <T>    - message generic type
     * @return message as bytes array
     */
    <T> byte[] toMessage(T object);

    /**
     * Converts message bytes to object.
     *
     * @param message - message bytes array
     * @param <T>     - message generic type
     * @return message object
     */
    <T> T fromMessage(byte[] message);
}
