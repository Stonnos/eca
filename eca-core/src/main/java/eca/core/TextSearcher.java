package eca.core;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Case sensitive text searcher.
 *
 * @author Roman Batygin
 */
public class TextSearcher {

    private static final int OUT_OF_RANGE = -1;

    @Getter
    private final String text;
    @Getter
    private final String searchTerm;

    private int cursor = 0;

    @Getter
    private int currentMatchStartPosition = -1;
    @Getter
    private int currentMatchEndPosition = -1;

    /**
     * Constructor with parameters.
     *
     * @param text       - full text
     * @param searchTerm - search term
     */
    public TextSearcher(String text, String searchTerm) {
        Objects.requireNonNull(text, "Text is not specified");
        Objects.requireNonNull(searchTerm, "Search term isn't specified");
        this.text = text;
        this.searchTerm = searchTerm;
    }

    /**
     * Find next match.
     *
     * @return {@code true} if match has been found, otherwise {@code false}
     */
    public boolean find() {
        if (cursor >= 0) {
            cursor = StringUtils.indexOfIgnoreCase(text, searchTerm, cursor);
            if (cursor >= 0) {
                currentMatchStartPosition = cursor;
                cursor = cursor + searchTerm.length();
                currentMatchEndPosition = cursor;
                return true;
            } else {
                cursor = OUT_OF_RANGE;
                currentMatchStartPosition = OUT_OF_RANGE;
                currentMatchEndPosition = OUT_OF_RANGE;
            }
        }
        return false;
    }
}
