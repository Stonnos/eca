package eca.data.migration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class.
 *
 * @author Roman Batygin
 */
@SpringBootApplication
public class DataMigrationApplication {

    /**
     * Runs application.
     *
     * @param args - command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(DataMigrationApplication.class, args);
    }
}
