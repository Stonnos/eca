package eca.gui.actions;

import eca.statistics.contingency.ChiValueResult;
import eca.statistics.contingency.ContingencyTable;

/**
 * @author Roman Batygin
 */
public class ContingencyTableAction extends AbstractCallback<ChiValueResult> {

    private final ContingencyTable contingencyTable;
    private final int rowAttrIndex;
    private final int colAttrIndex;

    private double[][] contingencyMatrix;

    public ContingencyTableAction(ContingencyTable contingencyTable, int rowAttrIndex, int colAttrIndex) {
        this.contingencyTable = contingencyTable;
        this.rowAttrIndex = rowAttrIndex;
        this.colAttrIndex = colAttrIndex;
    }

    @Override
    public void apply() {
        contingencyMatrix = contingencyTable.computeContingencyMatrix(rowAttrIndex, colAttrIndex);
        result = contingencyTable.calculateChiSquaredResult(rowAttrIndex, colAttrIndex, contingencyMatrix);
    }

    public double[][] getContingencyMatrix() {
        return contingencyMatrix;
    }
}
