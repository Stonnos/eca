package eca.gui;

import eca.gui.dialogs.BaseOptionsDialog;
import eca.gui.dialogs.DecisionTreeOptionsDialog;
import eca.gui.dialogs.KNNOptionDialog;
import eca.gui.dialogs.LogisticOptionsDialogBase;
import eca.gui.dialogs.NetworkOptionsDialog;
import eca.gui.dialogs.StackingOptionsDialog;
import eca.gui.enums.ClassifiersNames;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Roman Batygin
 */

public class BaseClassifiersListModel extends DefaultListModel<String> {

    private ArrayList<BaseOptionsDialog> frames = new ArrayList<>();

    private Instances data;

    private Window parent;

    public BaseClassifiersListModel(Instances data, Window parent) {
        this.data = data;
        this.parent = parent;
    }

    public ArrayList<BaseOptionsDialog> getFrames() {
        return frames;
    }

    public BaseOptionsDialog getWindow(int i) {
        return frames.get(i);
    }

    @Override
    public void clear() {
        frames.clear();
        super.clear();
    }

    public void addClassifier(Classifier classifier) {
        String name = null;
        if (classifier instanceof C45) {
            name = ClassifiersNames.C45;
            frames.add(new DecisionTreeOptionsDialog(parent,
                    name, (C45) classifier, data));
        } else if (classifier instanceof ID3) {
            name = ClassifiersNames.ID3;
            frames.add(new DecisionTreeOptionsDialog(parent,
                    name, (ID3) classifier, data));
        } else if (classifier instanceof CART) {
            name = ClassifiersNames.CART;
            frames.add(new DecisionTreeOptionsDialog(parent,
                    name, (CART) classifier, data));
        } else if (classifier instanceof CHAID) {
            name = ClassifiersNames.CHAID;
            frames.add(new DecisionTreeOptionsDialog(parent,
                    name, (CHAID) classifier, data));
        } else if (classifier instanceof Logistic) {
            name = ClassifiersNames.LOGISTIC;
            frames.add(new LogisticOptionsDialogBase(parent,
                    name, (Logistic) classifier, data));
        } else if (classifier instanceof NeuralNetwork) {
            name = ClassifiersNames.NEURAL_NETWORK;
            frames.add(new NetworkOptionsDialog(parent,
                    name, (NeuralNetwork) classifier, data));
        } else if (classifier instanceof KNearestNeighbours) {
            name = ClassifiersNames.KNN;
            frames.add(new KNNOptionDialog(parent,
                    name, (KNearestNeighbours) classifier, data));
        }
        super.addElement(name);
    }

    @Override
    public void addElement(String classifier) {
        try {
            switch (classifier) {
                case ClassifiersNames.ID3:
                    frames.add(new DecisionTreeOptionsDialog(parent,
                            ClassifiersNames.ID3, new ID3(), data));
                    break;

                case ClassifiersNames.C45:
                    frames.add(new DecisionTreeOptionsDialog(parent,
                            ClassifiersNames.C45, new C45(), data));
                    break;

                case ClassifiersNames.CART:
                    frames.add(new DecisionTreeOptionsDialog(parent,
                            ClassifiersNames.CART, new CART(), data));
                    break;

                case ClassifiersNames.CHAID:
                    frames.add(new DecisionTreeOptionsDialog(parent,
                            ClassifiersNames.CHAID, new CHAID(), data));
                    break;

                case ClassifiersNames.NEURAL_NETWORK:
                    frames.add(new NetworkOptionsDialog(parent,
                            ClassifiersNames.NEURAL_NETWORK, new NeuralNetwork(data), data));
                    break;

                case ClassifiersNames.LOGISTIC:
                    frames.add(new LogisticOptionsDialogBase(parent,
                            ClassifiersNames.LOGISTIC, new Logistic(), data));
                    break;

                case ClassifiersNames.KNN:
                    frames.add(new KNNOptionDialog(parent,
                            ClassifiersNames.KNN, new KNearestNeighbours(), data));
                    break;

            }
            super.addElement(classifier);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    e.getMessage(),
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public String remove(int i) {
        BaseOptionsDialog frame = frames.remove(i);
        frame.dispose();
        return super.remove(i);
    }

}
