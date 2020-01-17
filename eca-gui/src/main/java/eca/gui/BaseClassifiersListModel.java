package eca.gui;

import eca.dictionary.ClassifiersNamesDictionary;
import eca.gui.dialogs.ClassifierOptionsDialogBase;
import eca.gui.dialogs.DecisionTreeOptionsDialog;
import eca.gui.dialogs.J48OptionsDialog;
import eca.gui.dialogs.KNNOptionDialog;
import eca.gui.dialogs.LogisticOptionsDialogBase;
import eca.gui.dialogs.NetworkOptionsDialog;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import eca.trees.J48;
import lombok.RequiredArgsConstructor;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Roman Batygin
 */

public class BaseClassifiersListModel extends DefaultListModel<String> {

    private static List<ClassifierConditionRule> classifierConditionRules;

    private ArrayList<ClassifierOptionsDialogBase> frames = new ArrayList<>();

    private Instances data;

    private Window parent;

    private int digits;

    static {
        classifierConditionRules = newArrayList();
        classifierConditionRules.add(new C45ConditionRule());
        classifierConditionRules.add(new ID3ConditionRule());
        classifierConditionRules.add(new CartConditionRule());
        classifierConditionRules.add(new ChaidConditionRule());
        classifierConditionRules.add(new LogisticConditionRule());
        classifierConditionRules.add(new NeuralNetworkConditionRule());
        classifierConditionRules.add(new KnnConditionRule());
        classifierConditionRules.add(new J48ConditionRule());
    }

    public BaseClassifiersListModel(Instances data, Window parent, int digits) {
        this.data = data;
        this.parent = parent;
        this.digits = digits;
    }

    public List<ClassifierOptionsDialogBase> getFrames() {
        return frames;
    }

    public ClassifierOptionsDialogBase getWindow(int i) {
        return frames.get(i);
    }

    @Override
    public void clear() {
        frames.clear();
        super.clear();
    }

    @Override
    public boolean isEmpty() {
        return frames.isEmpty();
    }

    public void addClassifier(Classifier classifier) {
        ClassifierConditionRule classifierConditionRule = classifierConditionRules.stream()
                .filter(rule -> rule.matches(classifier))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't handle %s classifier", classifier.getClass().getSimpleName())));
        ClassifierOptionsDialogBase classifierOptionsDialogBase =
                classifierConditionRule.createDialog(parent, classifier, data);
        frames.add(classifierOptionsDialogBase);
        super.addElement(classifierOptionsDialogBase.getTitle());
    }

    @Override
    public void addElement(String classifier) {
        switch (classifier) {
            case ClassifiersNamesDictionary.ID3:
                frames.add(new DecisionTreeOptionsDialog(parent,
                        ClassifiersNamesDictionary.ID3, new ID3(), data));
                break;

            case ClassifiersNamesDictionary.C45:
                frames.add(new DecisionTreeOptionsDialog(parent,
                        ClassifiersNamesDictionary.C45, new C45(), data));
                break;

            case ClassifiersNamesDictionary.CART:
                frames.add(new DecisionTreeOptionsDialog(parent,
                        ClassifiersNamesDictionary.CART, new CART(), data));
                break;

            case ClassifiersNamesDictionary.CHAID:
                frames.add(new DecisionTreeOptionsDialog(parent,
                        ClassifiersNamesDictionary.CHAID, new CHAID(), data));
                break;

            case ClassifiersNamesDictionary.NEURAL_NETWORK:
                NeuralNetwork neuralNetwork = new NeuralNetwork(data);
                neuralNetwork.getDecimalFormat().setMaximumFractionDigits(digits);
                frames.add(new NetworkOptionsDialog(parent,
                        ClassifiersNamesDictionary.NEURAL_NETWORK, neuralNetwork, data));
                break;

            case ClassifiersNamesDictionary.LOGISTIC:
                frames.add(new LogisticOptionsDialogBase(parent,
                        ClassifiersNamesDictionary.LOGISTIC, new Logistic(), data));
                break;

            case ClassifiersNamesDictionary.KNN:
                KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
                kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(digits);
                frames.add(new KNNOptionDialog(parent,
                        ClassifiersNamesDictionary.KNN, kNearestNeighbours, data));
                break;
            case ClassifiersNamesDictionary.J48:
                frames.add(new J48OptionsDialog(parent,
                        ClassifiersNamesDictionary.J48, new J48(), data));
                break;
            default:
                throw new IllegalStateException(String.format("Can't handle %s classifier", classifier));
        }
        super.addElement(classifier);
    }

    @Override
    public String remove(int i) {
        ClassifierOptionsDialogBase frame = frames.remove(i);
        frame.dispose();
        return super.remove(i);
    }

    @RequiredArgsConstructor
    private static abstract class ClassifierConditionRule<T extends Classifier> {

        private final Class<T> clazz;

        public boolean matches(T classifier) {
            return clazz.isAssignableFrom(classifier.getClass());
        }

        public abstract ClassifierOptionsDialogBase createDialog(Window parent, T classifier, Instances data);
    }

    private static class C45ConditionRule extends ClassifierConditionRule<C45> {

        C45ConditionRule() {
            super(C45.class);
        }

        @Override
        public ClassifierOptionsDialogBase createDialog(Window parent, C45 classifier, Instances data) {
            return new DecisionTreeOptionsDialog(parent, ClassifiersNamesDictionary.C45, classifier, data);
        }
    }

    private static class ID3ConditionRule extends ClassifierConditionRule<ID3> {

        ID3ConditionRule() {
            super(ID3.class);
        }

        @Override
        public ClassifierOptionsDialogBase createDialog(Window parent, ID3 classifier, Instances data) {
            return new DecisionTreeOptionsDialog(parent, ClassifiersNamesDictionary.ID3, classifier, data);
        }
    }

    private static class CartConditionRule extends ClassifierConditionRule<CART> {

        CartConditionRule() {
            super(CART.class);
        }

        @Override
        public ClassifierOptionsDialogBase createDialog(Window parent, CART classifier, Instances data) {
            return new DecisionTreeOptionsDialog(parent, ClassifiersNamesDictionary.CART, classifier, data);
        }
    }

    private static class ChaidConditionRule extends ClassifierConditionRule<CHAID> {

        ChaidConditionRule() {
            super(CHAID.class);
        }

        @Override
        public ClassifierOptionsDialogBase createDialog(Window parent, CHAID classifier, Instances data) {
            return new DecisionTreeOptionsDialog(parent, ClassifiersNamesDictionary.CHAID, classifier, data);
        }
    }

    private static class LogisticConditionRule extends ClassifierConditionRule<Logistic> {

        LogisticConditionRule() {
            super(Logistic.class);
        }

        @Override
        public ClassifierOptionsDialogBase createDialog(Window parent, Logistic classifier, Instances data) {
            return new LogisticOptionsDialogBase(parent, ClassifiersNamesDictionary.LOGISTIC, classifier, data);
        }
    }

    private static class NeuralNetworkConditionRule extends ClassifierConditionRule<NeuralNetwork> {

        NeuralNetworkConditionRule() {
            super(NeuralNetwork.class);
        }

        @Override
        public ClassifierOptionsDialogBase createDialog(Window parent, NeuralNetwork classifier, Instances data) {
            return new NetworkOptionsDialog(parent, ClassifiersNamesDictionary.NEURAL_NETWORK, classifier, data);
        }
    }

    private static class KnnConditionRule extends ClassifierConditionRule<KNearestNeighbours> {

        KnnConditionRule() {
            super(KNearestNeighbours.class);
        }

        @Override
        public ClassifierOptionsDialogBase createDialog(Window parent, KNearestNeighbours classifier, Instances data) {
            return new KNNOptionDialog(parent, ClassifiersNamesDictionary.KNN, classifier, data);
        }
    }

    private static class J48ConditionRule extends ClassifierConditionRule<J48> {

        J48ConditionRule() {
            super(J48.class);
        }

        @Override
        public ClassifierOptionsDialogBase createDialog(Window parent, J48 classifier, Instances data) {
            return new J48OptionsDialog(parent, ClassifiersNamesDictionary.J48, classifier, data);
        }
    }
}
