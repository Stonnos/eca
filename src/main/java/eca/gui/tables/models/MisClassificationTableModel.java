/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import javax.swing.table.AbstractTableModel;
import weka.core.Instances;
import weka.classifiers.Classifier;
import eca.core.evaluation.Evaluation;

/**
 *
 * @author Рома
 */
public class MisClassificationTableModel extends AbstractTableModel {
    
    private Instances data;
    private Classifier classifier;
    private Evaluation ev;
    private String[] titles;
    private double[][] values;
    
    public MisClassificationTableModel(Instances data, Classifier classifier, Evaluation ev)
            throws Exception {
        this.data = data;
        this.classifier = classifier;
        this.ev = ev;
        this.makeTitles();
        this.makeMatrix();
    }
    
    @Override
    public int getColumnCount() {
        return titles.length;
    }
    
    @Override
    public int getRowCount() {
        return values.length;
    }   
    
    @Override
    public Object getValueAt(int row, int column) {
        return column == 0 ? row : (int)values[row][column - 1];
    }
    
    @Override
    public String getColumnName(int column) {
        return titles[column];
    }
    
    private void makeTitles() {
        titles = new String[data.numClasses() + 1];
        titles[0] = "Реальное";
        for (int i = 1; i < titles.length; i++) {
            titles[i] = i - 1 + " (Прогнозное)"; 
        }
    }
    
    private void makeMatrix() throws Exception {      
        values = ev.confusionMatrix();
    }
       
}
