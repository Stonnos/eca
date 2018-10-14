package eca.statistics.contingency;

import eca.core.InstancesHandler;
import eca.statistics.Statistics;
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

    private static final double ALPHA_MAX_VALUE = 1.0d;
    private static final double ALPHA_MIN_VALUE = 0.0d;

    private final Instances data;

    /**
     * Significant level for chi squared criteria
     */
    private double alpha = 0.05d;

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
                    matrix[attributeX.numValues()][attributeY.numValues()] += matrix[attrXVal][attributeY.numValues()];
                });
        return matrix;
    }

    public ChiValueResult calculateChiSquaredResult(int attrXIndex, int attrYIndex, double[][] contingencyMatrix) {
        Objects.requireNonNull(contingencyMatrix, "Contingency matrix isn't specified!");
        Attribute attributeX = data.attribute(attrXIndex);
        Attribute attributeY = data.attribute(attrYIndex);
        if (!attributeX.isNominal() || !attributeY.isNominal()) {
            throw new IllegalArgumentException("Attributes must be nominal!");
        }
        ChiValueResult chiValueResult = new ChiValueResult();
        chiValueResult.setChiSquaredValue(ContingencyTables.chiVal(contingencyMatrix, false));
        int df = (attributeX.numValues() - 1) * (attributeY.numValues() - 1);
        chiValueResult.setChiSquaredCriticalValue(Statistics.chiSquaredCriticalValue(alpha, df));
        chiValueResult.setDf(df);
        chiValueResult.setAlpha(alpha);
        chiValueResult.setSignificant(
                chiValueResult.getChiSquaredValue() > chiValueResult.getChiSquaredCriticalValue());
        return chiValueResult;
    }
}
