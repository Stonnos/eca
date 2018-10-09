/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.util;

/**
 * Class for storage pairs (key, value).
 *
 * @param <K> - key generic type
 * @param <V> - value generic type
 * @author Roman Batygin
 */
public class Entry<K, V> implements java.io.Serializable {

    private K key;

    private V value;

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
    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns key value.
     *
     * @return key value
     */
    public K getKey() {
        return key;
    }

    /**
     * Sets key value.
     *
     * @param key key value
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Returns value.
     *
     * @return value
     */
    public V getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value value
     */
    public void setValue(V value) {
        this.value = value;
    }

}
