package eca;

import eca.beans.ClassifierDescriptor;
import eca.beans.InputData;
import eca.client.RestClient;
import eca.client.RestClientImpl;
import eca.core.TestMethod;
import eca.generators.SimpleDataGenerator;
import eca.gui.frames.JMainFrame;
import eca.trees.CART;
import weka.core.Instances;

import java.awt.EventQueue;

/**
 * Main class.
 * @author Roman Batygin
 */
public class Eca {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JMainFrame().setVisible(true);
            }
        });

       /* try {
            Instances data = new SimpleDataGenerator().generate();
            CART cart = new CART();
            InputData inputData = new InputData(cart, data);

            RestClient service = new RestClientImpl();
            ClassifierDescriptor classifierDescriptor = service.execute(inputData,
                    TestMethod.CROSS_VALIDATION, 10, 10);

            System.out.println(classifierDescriptor.getEvaluation().toSummaryString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
