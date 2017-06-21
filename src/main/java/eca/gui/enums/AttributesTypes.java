/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.enums;

import weka.core.Attribute;

/**
 *
 * @author Рома
 */
public class AttributesTypes {
    public static final String NOMINAL = "Категориальный";
    public static final String NUMERIC = "Числовой";
    public static final String DATE = "Дата и время";

    public static String getType(Attribute a) {
        String type;
        if (a.isDate()) {
            type = AttributesTypes.DATE;
        }
        else if (a.isNumeric()) {
            type = AttributesTypes.NUMERIC;
        }
        else type = AttributesTypes.NOMINAL;
        return type;
    }
}
