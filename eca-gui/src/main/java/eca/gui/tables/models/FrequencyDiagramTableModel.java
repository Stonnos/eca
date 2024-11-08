package eca.gui.tables.models;

import eca.statistics.diagram.FrequencyData;
import eca.text.NumericFormatFactory;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author Roman Batygin
 */

public class FrequencyDiagramTableModel extends AbstractTableModel {

    private static final String[] TITLES = {"Интервал", "Частота"};
    private static final String FIRST_INTERVAL_FORMAT = "[%s; %s]";
    private static final String INTERVAL_FORMAT = "(%s; %s]";
    private final DecimalFormat decimalFormat = NumericFormatFactory.getInstance();
    private final List<FrequencyData> frequencyDataList;

    public FrequencyDiagramTableModel(List<FrequencyData> frequencyDataList, int digits) {
        this.frequencyDataList = frequencyDataList;
        decimalFormat.setMaximumFractionDigits(digits);
    }

    public void clear() {
        frequencyDataList.clear();
    }

    @Override
    public int getColumnCount() {
        return TITLES.length;
    }

    @Override
    public int getRowCount() {
        return frequencyDataList.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        FrequencyData frequencyData = frequencyDataList.get(row);
        if (column == 0) {
            String lowerBound = decimalFormat.format(frequencyData.getLowerBound());
            if (frequencyData.isNumeric()) {
                String upperBound = decimalFormat.format(frequencyData.getUpperBound());
                String intervalFormat = row == 0 ? FIRST_INTERVAL_FORMAT : INTERVAL_FORMAT;
                return String.format(intervalFormat, lowerBound, upperBound);
            } else {
                return lowerBound;
            }
        } else {
            return frequencyData.getFrequency();
        }
    }

    @Override
    public String getColumnName(int column) {
        return TITLES[column];
    }

}
