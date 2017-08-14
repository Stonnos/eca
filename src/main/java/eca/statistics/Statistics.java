/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.statistics;

/**
 * @author Roman93
 */
public class Statistics {

    public static double studentCriticalValue(int df, double p) {
        return Math.sqrt(FCriticalValue(p, 1, df));
    }

    public static double studentConfidenceInterval(int n, double p, double s) {
        return s * studentCriticalValue(n - 1, p) / Math.sqrt(n);
    }

    public static double FCriticalValue(double p, int df1, int df2) {
        double maxf = 99999.0;
        double minf = .000001;
        if (p <= 0.0 || p >= 1.0) {
            return 0.0;
        }
        double fval = 1.0 / p;
        while (Math.abs(maxf - minf) > .000001) {
            if (weka.core.Statistics.FProbability(fval, df1, df2) < p) {
                maxf = fval;
            } else {
                minf = fval;
            }
            fval = (maxf + minf) * 0.5;
        }
        return fval;
    }

    public static double chiSquaredCriticalValue(double p, int df) {
        double maxf = 99999.0;
        double minf = .000001;
        if (p <= 0.0 || p >= 1.0) {
            return 0.0;
        }
        double fval = 1.0 / p;
        while (Math.abs(maxf - minf) > .000001) {
            if (weka.core.Statistics.chiSquaredProbability(fval, df) < p) {
                maxf = fval;
            } else {
                minf = fval;
            }
            fval = (maxf + minf) * 0.5;
        }
        return fval;
    }

}
