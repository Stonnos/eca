package eca.gui;

import eca.config.ConfigurationService;
import eca.gui.frames.results.ClassificationResultsFrameBase;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Evaluation results history model.
 *
 * @author Roman Batygin
 */
public class EvaluationResultsHistoryModel extends DefaultListModel<String> {

    private static final String HISTORY_FORMAT = "%s %s";

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            ConfigurationService.getApplicationConfigService().getApplicationConfig().getDateFormat());

    private ArrayList<ClassificationResultsFrameBase> resultsFrameBases = new ArrayList<>();

    public synchronized void add(ClassificationResultsFrameBase resultsFrameBase) {
        resultsFrameBases.add(resultsFrameBase);
        addElement(String.format(HISTORY_FORMAT, simpleDateFormat.format(resultsFrameBase.getCreationDate()),
                resultsFrameBase.classifier().getClass().getSimpleName()));
    }

    public ClassificationResultsFrameBase getClassificationResultsFrame(int i) {
        return resultsFrameBases.get(i);
    }
}
