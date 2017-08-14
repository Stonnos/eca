/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.InstancesHandler;
import eca.filter.MissingValuesFilter;
import org.apache.commons.lang3.StringUtils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Implements stacking algorithm.
 * <p>
 * Valid options are: <p>
 * <p>
 * Set meta classifier. <p>
 * <p>
 * Set number of folds for k - cross validation. (Default: 10) <p>
 * <p>
 * Use k - cross validation method for creating meta data. <p>
 * <p>
 * Set individual classifiers collection  <p>
 *
 * @author Рома
 */
public class StackingClassifier extends AbstractClassifier
        implements EnsembleClassifier, InstancesHandler {

    /**
     * Initial training set
     **/
    private Instances initialData;

    /**
     * Meta data
     **/
    private Instances metaSet;

    /**
     * Meta classifier
     **/
    private Classifier metaClassifier;

    /**
     * Classifiers set
     **/
    private ClassifiersSet classifiers;

    /**
     * Filtered training set
     **/
    private Instances filteredData;

    /**
     * Use k - cross validation method?
     **/
    private boolean use_Cross_Validation;

    /**
     * Number of folds
     **/
    private int numFolds = 10;

    private final MissingValuesFilter filter = new MissingValuesFilter();

    /**
     * Creates <tt>StackingClassifier</tt> object.
     */
    public StackingClassifier() {
    }

    /**
     * Creates <tt>StackingClassifier</tt> object.
     *
     * @param flag the value of use cross validation.
     */
    public StackingClassifier(boolean flag) {
        this.setUseCrossValidation(flag);
    }

    /**
     * Sets the value of use cross validation.
     *
     * @param flag the value of use cross validation
     */
    public final void setUseCrossValidation(boolean flag) {
        this.use_Cross_Validation = flag;
    }

    /**
     * Returns the value of use cross validation.
     *
     * @return the value of use cross validation
     */
    public final boolean getUseCrossValidation() {
        return use_Cross_Validation;
    }

    /**
     * Sets the number of folds.
     *
     * @param numFolds the number of folds
     */
    public void setNumFolds(int numFolds) {
        this.numFolds = numFolds;
    }

    /**
     * Returns the number of folds.
     *
     * @return the number of folds
     */
    public int getNumFolds() {
        return numFolds;
    }

    /**
     * Returns classifiers collection.
     *
     * @return classifiers collection
     */
    public ClassifiersSet getClassifiers() {
        return classifiers;
    }

    /**
     * Sets classifiers collection.
     *
     * @param classifiers classifiers collection
     */
    public void setClassifiers(ClassifiersSet classifiers) {
        this.classifiers = classifiers;
    }

    @Override
    public double[] distributionForInstance(Instance obj) throws Exception {
        Instance filtered = createInstance(filter.filterInstance(obj));
        return metaClassifier.distributionForInstance(filtered);
    }

    /**
     * Returns the number of classifiers.
     *
     * @return the number of classifiers
     */
    public int numClassifiers() {
        return classifiers.size() + 1;
    }

    @Override
    public Instances getData() {
        return initialData;
    }

    @Override
    public void buildClassifier(Instances dataSet) throws Exception {
        initialData = dataSet;
        filteredData = filter.filterInstances(initialData);
        createMetaFormat();
        if (getUseCrossValidation()) {
            Instances newData = new Instances(filteredData);
            newData.stratify(numFolds);
            ClassifiersSet copies = classifiers.clone();
            for (int i = 0; i < numFolds; i++) {
                Instances train = newData.trainCV(numFolds, i);
                for (int j = 0; j < classifiers.size(); j++) {
                    classifiers.setClassifier(j, copies.getClassifier(j));
                    classifiers.getClassifier(j).buildClassifier(train);
                }

                Instances test = newData.testCV(numFolds, i);
                addInstances(test);
                classifiers = copies;
            }
            //Rebuilt all classifiers
            for (Classifier classifier : classifiers) {
                classifier.buildClassifier(filteredData);
            }
        } else {
            for (Classifier classifier : classifiers) {
                classifier.buildClassifier(filteredData);
            }
            addInstances(filteredData);
        }
        createMetaClassifier();
    }

    /**
     * Sets meta classifier.
     *
     * @param classifier the number of classifiers
     */
    public void setMetaClassifier(Classifier classifier) {
        this.metaClassifier = classifier;
    }

    /**
     * Returns meta classifier.
     *
     * @return meta classifier
     */
    public Classifier getMetaClassifier() {
        return metaClassifier;
    }

    @Override
    public double classifyInstance(Instance obj) throws Exception {
        Instance filtered = createInstance(filter.filterInstance(obj));
        return metaClassifier.classifyInstance(filtered);
    }

    @Override
    public ArrayList<Classifier> getStructure() throws Exception {
        ArrayList<Classifier> copies = getClassifiers().clone().toList();
        copies.add(AbstractClassifier.makeCopy(metaClassifier));
        return copies;
    }

    @Override
    public String[] getOptions() {
        String[] options = new String[(classifiers.size() + 2) * 2];
        int k = 0;
        options[k++] = "Мета-классификатор:";
        options[k++] = String.valueOf(metaClassifier.getClass().getSimpleName());
        options[k++] = "Метод формирования мета-признаков:";
        options[k++] = getUseCrossValidation() ? numFolds + " - блочная кросс-проверка"
                : "Использование обучающего множества";
        for (int i = k++, j = 0; i < options.length; i += 2, j++) {
            options[i] = "Базовый классификатор " + j + ":";
            options[i + 1] = classifiers.getClassifier(j).getClass().getSimpleName();
        }
        return options;
    }

    private void addInstances(Instances set) throws Exception {
        for (int i = 0; i < set.numInstances(); i++) {
            metaSet.add(createInstance(set.instance(i)));
        }
    }

    private void createMetaFormat() {
        ArrayList<Attribute> attr = new ArrayList<>(classifiers.size() + 1);
        ArrayList<String> values = new ArrayList<>(filteredData.numClasses());
        Attribute classAttr = filteredData.classAttribute();

        for (int k = 0; k < classAttr.numValues(); k++) {
            values.add(classAttr.value(k));
        }

        for (int k = 0; k < classifiers.size(); k++) {
            attr.add(new Attribute(classifiers.getClassifier(k).getClass().getSimpleName() +
                    StringUtils.SPACE + String.valueOf(k), (ArrayList<String>) values.clone()));
        }

        attr.add((Attribute) classAttr.copy());

        metaSet = new Instances("MetaSet", attr, filteredData.numInstances());
        metaSet.setClassIndex(metaSet.numAttributes() - 1);
    }

    private Instance createInstance(Instance o) throws Exception {
        DenseInstance obj = new DenseInstance(metaSet.numAttributes());
        obj.setDataset(metaSet);
        for (int j = 0; j < classifiers.size(); j++) {
            double c = classifiers.getClassifier(j).classifyInstance(o);
            obj.setValue(j, c);
        }
        obj.setClassValue(o.classValue());
        return obj;
    }

    private void createMetaClassifier() throws Exception {
        metaClassifier.buildClassifier(metaSet);
    }

}
