package eca.config.sql;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Ansi SQL 2003 keywords.
 *
 * @author Roman Batygin
 */
@Slf4j
public class AnsiSqlKeywords {

    private static final String SQL2003_KEYWORDS_TXT = "sql2003-keywords.txt";

    private static AnsiSqlKeywords ansiSqlKeywords;

    private List<String> sql2003Keywords;

    /**
     * Creates singleton instance.
     *
     * @return Ansi SQL 2003 keywords object
     */
    public static AnsiSqlKeywords getAnsiSqlKeywords() {
        if (ansiSqlKeywords == null) {
            ansiSqlKeywords = new AnsiSqlKeywords();
        }
        return ansiSqlKeywords;
    }

    /**
     * Gets Ansi SQL 2003 keywords list.
     *
     * @return Ansi SQL 2003 keywords list
     */
    public List<String> getSql2003Keywords() {
        if (sql2003Keywords == null) {
            sql2003Keywords = loadSql2003Keywords();
        }
        return sql2003Keywords;
    }

    private List<String> loadSql2003Keywords() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SQL2003_KEYWORDS_TXT)) {
            List<String> words = IOUtils.readLines(inputStream, Charsets.UTF_8);
            return Collections.unmodifiableList(words);
        } catch (Exception ex) {
            log.error("There was an error while loading data from file {}: {}", SQL2003_KEYWORDS_TXT, ex.getMessage());
        }
        return Collections.emptyList();
    }

}
