package eca.data.migration.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Migration log persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "migration_log")
public class MigrationLog {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Data file name
     */
    @Column(name = "source_file_name")
    private String sourceFileName;

    /**
     * Table name in database
     */
    @Column(name = "table_name")
    private String tableName;

    /**
     * Last migrated table index
     */
    @Column(name = "last_table_index")
    private long lastTableIndex;

    /**
     * Migration start date
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * Migration end date
     */
    @Column(name = "finish_date")
    private LocalDateTime finishDate;

    /**
     * Migration status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "migration_status")
    private MigrationStatus migrationStatus;

    /**
     * Detail message
     */
    @Lob
    private String details;

    /**
     * Migration log source
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "migration_log_source")
    private MigrationLogSource migrationLogSource;
}
