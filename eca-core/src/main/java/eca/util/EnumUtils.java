package eca.util;

import eca.core.DescriptiveEnum;
import lombok.experimental.UtilityClass;

import java.util.stream.Stream;

/**
 * Enum utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class EnumUtils {

    /**
     * Gets enum constants by its description.
     *
     * @param description - enum description
     * @param enumClass   - enum class
     * @param <E>         - enum generic type
     * @return enum constant
     */
    public static <E extends Enum<E> & DescriptiveEnum> E fromDescription(String description, Class<E> enumClass) {
        return Stream.of(enumClass.getEnumConstants()).filter(
                e -> e.getDescription().equals(description)).findFirst().orElse(null);
    }

    /**
     * Gets enum constants descriptions.
     *
     * @param enumClass - enum class
     * @param <E>       - enum generic type
     * @return enum constants descriptions
     */
    public static <E extends Enum<E> & DescriptiveEnum> String[] getDescriptions(Class<E> enumClass) {
        return Stream.of(enumClass.getEnumConstants()).map(DescriptiveEnum.class::cast).map(
                DescriptiveEnum::getDescription).toArray(String[]::new);
    }
}
