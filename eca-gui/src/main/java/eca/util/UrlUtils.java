package eca.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Url utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class UrlUtils {

    /**
     * Verify url string.
     *
     * @param url - url string
     * @return {@code true} if url string is valid
     */
    public static boolean isValidUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        } else {
            try {
                new URL(url);
            } catch (MalformedURLException ex) {
                return false;
            }
            return true;
        }
    }
}
