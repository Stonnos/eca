package eca.config.registry;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Singleton registry class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class SingletonRegistry {

    private static final Map<Class<?>, Object> singletonMap = new WeakHashMap<>();

    /**
     * Gets or create singleton instance for specified class. Default constructor must be declared for class.
     *
     * @param instanceClazz - instance class
     * @param <T>           - instances generic type
     * @return singleton instance
     */
    public static <T> T getSingleton(Class<T> instanceClazz) {
        synchronized (singletonMap) {
            Object instance = singletonMap.get(instanceClazz);
            if (instance == null) {
                try {
                    instance = instanceClazz.newInstance();
                    singletonMap.put(instanceClazz, instance);
                } catch (Exception ex) {
                    throw new IllegalStateException(ex.getMessage());
                }
            }
            return instanceClazz.cast(instance);
        }
    }
}
