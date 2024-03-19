package eca.client;

import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
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
    private static final String EMAIL = "bat1238@yandex.ru";

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
        experimentRequestDto.setEmail(EMAIL);
        experimentRequestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        experimentRequestDto.setExperimentType(ExperimentType.KNN);
        return experimentRequestDto;
    }
}
