package eca.config;

import lombok.Data;

/**
 * Project info config.
 *
 * @author Roman Batygin
 */
@Data
public class ProjectInfo {

    /**
     * Project title
     */
    private String title;
    /**
     * Project description
     */
    private String titleDescription;
    /**
     * Project version
     */
    private String version;
    /**
     * Project release date
     */
    private String releaseDate;
    /**
     * Project author
     */
    private String author;
    /**
     * Author email
     */
    private String authorEmail;
}
