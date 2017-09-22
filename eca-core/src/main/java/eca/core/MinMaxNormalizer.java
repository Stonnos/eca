/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Implement mini-max normalization.
 * @author Roman Batygin
 */
public class MinMaxNormalizer implements java.io.Serializable {

    private final Instances data;
    private final double[] maXs;
    private final double[] miNs;

    /**
     * Creates <tt>MinMaxNormalizer</tt> object
     * @param data {@link Instances} object
     */
    public MinMaxNormalizer(Instances data) {
        this.data = data;
        this.maXs = new double[data.numAttributes() - 1];
        this.miNs = new double[data.numAttributes() - 1];
    }

    /**
     * Normalize instances object.
     * @return {@link Instances} object
     */
    public Instances normalizeInstances() {
        Instances set = new Instances(data);
        for (int i = 0; i < data.numAttributes() - 1; i++) {
            int z = i >= data.classIndex() ? i + 1 : i;
            computeMinAndMax(i, z);
            for (int j = 0; j < data.numInstances(); j++) {
                set.instance(j).setValue(i, computeNormalizedValue(data.instance(j).value(z),
                        miNs[i], maXs[i]));
            }
        }
        return set;
    }

    /**
     * Normalize instance.
     * @param obj {@link Instance} object
     * @return normalized instance
     */
    public Instance normalizeInstance(Instance obj) {
        Instance x = new DenseInstance(data.numAttributes());
        x.setDataset(data);
        for (int i = 0; i < data.numAttributes() - 1; i++) {
            int j = i >= data.classIndex() ? i + 1 : i;
            x.setValue(i, computeNormalizedValue(obj.value(j), miNs[i], maXs[i]));
        }
        return x;
    }

    /**
     * Normalized input attributes values.
     * @return normalized data as array
     */
    public double[][] normalizeInputValues() {
        double[][] x = new double[data.numInstances()][data.numAttributes() - 1];
        for (int i = 0; i < data.numAttributes() - 1; i++) {
            int z = i >= data.classIndex() ? i + 1 : i;
            computeMinAndMax(i, z);
            for (int j = 0; j < data.numInstances(); j++) {
                x[j][i] = computeNormalizedValue(data.instance(j).value(z), miNs[i], maXs[i]);
            }
        }
        return x;
    }

    /**
     * Normalized class attribute.
     * @return normalized data as array
     */
    public double[][] normalizeOutputValues() {
        double[][] y = new double[data.numInstances()][data.numClasses()];
        for (int j = 0; j < data.numInstances(); j++) {
            int classIndex = (int) data.instance(j).classValue();
            y[j][classIndex] = 1.0;
        }
        return y;
    }

    /**
     * Gets minimum values array.
     * @return minimum values array
     */
    public double[] getMiNs() {
        return miNs;
    }

    /**
     * Gets maximum values array.
     * @return maximum values array
     */
    public double[] getMaXs() {
        return maXs;
    }


    /**
     * Normalized instance.
     * @param obj {@link Instance} object
     * @return normalized object as array
     */
    public double[] normalizeObject(Instance obj) {
        double[] x = new double[data.numAttributes() - 1];
        for (int i = 0; i < data.numAttributes() - 1; i++) {
            int j = i >= data.classIndex() ? i + 1 : i;
            x[i] = computeNormalizedValue(obj.value(j), miNs[i], maXs[i]);
        }
        return x;
    }

    private void computeMinAndMax(int i, int j) {
        double max = -Double.MAX_VALUE;
        double min = -max;
        for (int k = 0; k < data.numInstances(); k++) {
            Instance o = data.instance(k);
            max = Math.max(o.value(j), max);
            min = Math.min(o.value(j), min);
        }
        maXs[i] = max;
        miNs[i] = min;
    }

    private double computeNormalizedValue(double x, double min, double max) {
        return max != min ? (x - min) / (max - min) : 0;
    }

}
