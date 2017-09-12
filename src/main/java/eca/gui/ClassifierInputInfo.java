/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;

/**
 * @author Roman Batygin
 */
public class ClassifierInputInfo {

    public static String getInputOptionsInfo(Classifier classifier) {
        StringBuilder info = new StringBuilder("<html><head><style>"
                + ".attr {font-weight: bold;} th {font-size: 14;}</style></head><body>");
        info.append("<table>");
        info.append("<tr>");
        info.append("<th colspan = '2'>Входные параметры классификатора</th>");
        info.append("</tr>");
        String[] option = ((AbstractClassifier) classifier).getOptions();
        for (int i = 0; i < option.length; i += 2) {
            info.append("<tr>");
            info.append("<td class = 'attr'>");
            info.append(option[i]);
            info.append("</td>");
            info.append("<td>");
            info.append(option[i + 1]);
            info.append("</td>");
            info.append("</tr>");
        }
        info.append("</table></body></html>");
        return info.toString();
    }
}
