package eca.gui.dictionary;

import lombok.experimental.UtilityClass;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Eca service options dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class EcaServiceOptionsDictionary {

    private static final Map<String, String> OPTION_DESCRIPTION_MAP = newHashMap();

    static {
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.ECA_SERVICE_ENABLED, "Вкл./выкл. использование сервиса");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.RABBIT_HOST, "Хост брокера сообщений");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.RABBIT_PORT, "Порт брокера сообщений");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.RABBIT_USERNAME, "Имя пользователя для брокера сообщений");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.RABBIT_PASSWORD, "Пароль для брокера сообщений");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.EVALUATION_REQUEST_QUEUE, "Очередь для построения классификаторов");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.EVALUATION_OPTIMIZER_REQUEST_QUEUE,
                "Очередь для построения оптимальных классификаторов");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.EXPERIMENT_REQUEST_QUEUE, "Очередь для построения экспериментов");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.DATA_LOADER_URL, "Url сервиса загрузки обучающих выборок");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.TOKEN_URL, "Url сервиса авторизации");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.CLIENT_ID, "Идентифифактор клиента (client id)");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.CLIENT_SECRET, "Секрет клиента (client secret)");
    }

    /**
     * Gets option value by key.
     *
     * @param optionKey - option key
     * @return value
     */
    public static String getValue(String optionKey) {
        return OPTION_DESCRIPTION_MAP.get(optionKey);
    }
}
