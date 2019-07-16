package eca.gui.actions;

import eca.statistics.contingency.ChiSquareTestResult;
import eca.statistics.contingency.ContingencyTable;

/**
 * @author Roman Batygin
 */
public class ContingencyTableAction extends AbstractCallback<ChiSquareTestResult> {

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
    protected ChiSquareTestResult performAndGetResult() {
        contingencyMatrix = contingencyTable.computeContingencyMatrix(rowAttrIndex, colAttrIndex);
        return contingencyTable.calculateChiSquaredResult(rowAttrIndex, colAttrIndex, contingencyMatrix);
    }

    public double[][] getContingencyMatrix() {
        return contingencyMatrix;
    }
}
