package eca.client;

import eca.client.dto.EvaluationRequestDto;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.io.File;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String DATA_PATH = "iris.xls";
    private static final String FIRST_NAME = "Roman";
    private static final String EMAIL = "test@mail.ru";

    /**
     * Loads instances from file.
     *
     * @return instances object
     */
    @SneakyThrows
    public static Instances loadInstances() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        XLSLoader dataLoader = new XLSLoader();
        dataLoader.setSource(new FileResource(new File(classLoader.getResource(DATA_PATH).getFile())));
        return dataLoader.loadInstances();
    }

    /**
     * Creates experiment request dto.
     *
     * @return experiment request dto
     */
    public static ExperimentRequestDto createExperimentRequestDto() {
        ExperimentRequestDto experimentRequestDto = new ExperimentRequestDto();
        experimentRequestDto.setFirstName(FIRST_NAME);
        experimentRequestDto.setEmail(EMAIL);
        experimentRequestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        experimentRequestDto.setExperimentType(ExperimentType.ADA_BOOST);
        return experimentRequestDto;
    }

    /**
     * Creates evaluation request.
     *
     * @param classifier       - classifier
     * @param instances        - training data
     * @param evaluationMethod - evaluation method
     * @return evaluation request dto
     */
    public static EvaluationRequestDto createEvaluationRequestDto(AbstractClassifier classifier, Instances instances,
                                                                  EvaluationMethod evaluationMethod) {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(classifier);
        evaluationRequestDto.setData(instances);
        evaluationRequestDto.setEvaluationMethod(evaluationMethod);
        return evaluationRequestDto;
    }

}
