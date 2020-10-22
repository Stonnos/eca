package eca.statistics.contingency;

import eca.core.InstancesHandler;
import eca.statistics.Statistics;
import eca.statistics.contingency.model.ChiSquareTestResult;
import weka.core.Attribute;
import weka.core.ContingencyTables;
import weka.core.Instances;

import java.util.Objects;

/**
 * Implements contingency table calculation.
 *
 * @author Roman Batygin
 */
public class ContingencyTable implements InstancesHandler {

    private static final double ALPHA_MIN_VALUE = 0.0d;
    private static final double ALPHA_MAX_VALUE = 1.0d;

    private final Instances data;

    /**
     * Significant level for chi squared criteria
     */
    private double alpha = 0.05d;

    /**
     * Use Yates correction?
     */
    private boolean useYates;

    /**
     * Creates contingency table with specified instances.
     *
     * @param data - instances
     */
    public ContingencyTable(Instances data) {
        Objects.requireNonNull(data, "Instances isn't specified!");
        this.data = data;
    }

    @Override
    public Instances getData() {
        return data;
    }

    /**
     * Returns significant level value for chi squared criteria.
     *
     * @return significant level value
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Sets significant level value for chi squared criteria.
     *
     * @param alpha - significant level value
     */
    public void setAlpha(double alpha) {
        if (alpha <= ALPHA_MIN_VALUE || alpha >= ALPHA_MAX_VALUE) {
            throw new IllegalArgumentException(
                    String.format("Significant level value must lies in (%.1f, %.1f)", ALPHA_MIN_VALUE,
                            ALPHA_MAX_VALUE));
        }
        this.alpha = alpha;
    }

    /**
     * Use Yates correction?
     *
     * @return {@code true} if Yates correction is used
     */
    public boolean isUseYates() {
        return useYates;
    }

    /**
     * Sets use Yates correction.
     *
     * @param useYates - use Yates correction?
     */
    public void setUseYates(boolean useYates) {
        this.useYates = useYates;
    }

    /**
     * Computes contingency table for specified attributes.
     *
     * @param attrXIndex - first attribute
     * @param attrYIndex - second attribute
     * @return contingency table
     */
    public double[][] computeContingencyMatrix(int attrXIndex, int attrYIndex) {
        Attribute attributeX = data.attribute(attrXIndex);
        Attribute attributeY = data.attribute(attrYIndex);
        if (!attributeX.isNominal() || !attributeY.isNominal()) {
            throw new IllegalArgumentException("Attributes must be nominal!");
        }
        double[][] matrix = new double[attributeX.numValues() + 1][attributeY.numValues() + 1];
        data.stream().filter(instance -> !instance.isMissing(attributeX) && !instance.isMissing(attributeY)).forEach(
                instance -> {
                    int attrXVal = (int) instance.value(attrXIndex);
                    int attrYVal = (int) instance.value(attrYIndex);
                    matrix[attrXVal][attrYVal]++;
                    matrix[attrXVal][attributeY.numValues()]++;
                    matrix[attributeX.numValues()][attrYVal]++;
                    matrix[attributeX.numValues()][attributeY.numValues()]++;
                });
        return matrix;
    }

    /**
     * Calculates chi-square test for estimating statistical significance between specified attributes.
     *
     * @param rowAttrIndex      - row attribute index
     * @param colAttrIndex      - column attribute index
     * @param contingencyMatrix - calculated contingency table for attributes
     * @return chi-square test result
     */
    public ChiSquareTestResult calculateChiSquaredResult(int rowAttrIndex, int colAttrIndex, double[][] contingencyMatrix) {
        Objects.requireNonNull(contingencyMatrix, "Contingency matrix isn't specified!");
        Attribute rowAttribute = data.attribute(rowAttrIndex);
        Attribute colAttribute = data.attribute(colAttrIndex);
        if (!rowAttribute.isNominal() || !colAttribute.isNominal()) {
            throw new IllegalArgumentException("Attributes must be nominal!");
        }
        ChiSquareTestResult chiSquareTestResult = new ChiSquareTestResult();
        chiSquareTestResult.setChiSquaredValue(ContingencyTables.chiVal(contingencyMatrix, useYates));
        int df = (rowAttribute.numValues() - 1) * (colAttribute.numValues() - 1);
        chiSquareTestResult.setChiSquaredCriticalValue(Statistics.chiSquaredCriticalValue(alpha, df));
        chiSquareTestResult.setDf(df);
        chiSquareTestResult.setAlpha(alpha);
        chiSquareTestResult.setSignificant(
                chiSquareTestResult.getChiSquaredValue() > chiSquareTestResult.getChiSquaredCriticalValue());
        return chiSquareTestResult;
    }
}
