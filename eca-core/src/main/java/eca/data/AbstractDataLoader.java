package eca.data;

import java.util.Objects;

/**
 * Abstract data loader class.
 *
 * @param <S> - data source type
 * @author Roman Batygin
 */
public abstract class AbstractDataLoader<S> implements DataLoader {

    /**
     * Date format
     */
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Data source
     */
    private S source;

    /**
     * Returns date format.
     *
     * @return date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets date format.
     *
     * @param dateFormat date format
     */
    public void setDateFormat(String dateFormat) {
        Objects.requireNonNull(dateFormat, "Date format is not specified!");
        this.dateFormat = dateFormat;
    }

    /**
     * Returns data source value.
     *
     * @return data source value
     */
    public S getSource() {
        return source;
    }

    /**
     * Sets data source value
     *
     * @param source - data source value
     */
    public void setSource(S source) {
        validateSource(source);
        this.source = source;
    }

    protected void validateSource(S source) {
        Objects.requireNonNull(source, "Source is not specified!");
    }
}
