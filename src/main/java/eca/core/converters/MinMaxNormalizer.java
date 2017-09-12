/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core.converters;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public class MinMaxNormalizer implements java.io.Serializable {

    private final Instances data;
    private final double[] maXs;
    private final double[] miNs;

    public MinMaxNormalizer(Instances data) {
        this.data = data;
        maXs = new double[data.numAttributes() - 1];
        miNs = new double[data.numAttributes() - 1];
    }

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

    public Instance normalizeInstance(Instance obj) {
        Instance x = new DenseInstance(data.numAttributes());
        x.setDataset(data);
        for (int i = 0; i < data.numAttributes() - 1; i++) {
            int j = i >= data.classIndex() ? i + 1 : i;
            x.setValue(i, computeNormalizedValue(obj.value(j), miNs[i], maXs[i]));
        }
        return x;
    }

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

    public double[][] normalizeOutputValues() {
        double[][] y = new double[data.numInstances()][data.numClasses()];
        for (int j = 0; j < data.numInstances(); j++) {
            int classIndex = (int) data.instance(j).classValue();
            y[j][classIndex] = 1.0;
        }
        return y;
    }

    public double[] getMiNs() {
        return miNs;
    }

    public double[] getMaXs() {
        return maXs;
    }

    public Instances data() {
        return data;
    }

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
