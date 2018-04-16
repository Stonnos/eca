/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.util;

/**
 * Class for storage pairs (key, value).
 *
 * @author Roman Batygin
 */
public class Entry implements java.io.Serializable {

    private String key;

    private String value;

    /**
     * Creates object.
     */
    public Entry() {
    }

    /**
     * Creates entry with specified key and value
     *
     * @param key   key
     * @param value value
     */
    public Entry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns key value.
     *
     * @return key value
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets key value.
     *
     * @param key key value
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
