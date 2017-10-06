/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Implements objects serialization.
 *
 * @author Roman Batygin
 */
public class SerializationUtils {

    /**
     * Writes serialized object to file.
     *
     * @param obj      object
     * @param fileName file name
     * @throws Exception
     */
    public static void serialize(Object obj, String fileName) throws Exception {
        try (ObjectOutputStream stream =
                     new ObjectOutputStream(new FileOutputStream(fileName))) {
            stream.writeObject(obj);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Reads object from file.
     *
     * @param fileName file name
     * @return object
     * @throws Exception
     */
    public static Object deserialize(String fileName) throws Exception {
        Object obj;
        try (ObjectInputStream stream =
                     new ObjectInputStream(new FileInputStream(fileName))) {
            obj = stream.readObject();
        } catch (Exception e) {
            throw new Exception(e);
        }
        return obj;
    }

}
