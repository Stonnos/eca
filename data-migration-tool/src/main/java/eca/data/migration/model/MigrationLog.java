package eca.data.migration.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

    @Column(name = "source_file_name")
    private String sourceFileName;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "finish_date")
    private LocalDateTime finishDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "migration_status")
    private MigrationStatus migrationStatus;

    @Column(columnDefinition = "text")
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(name = "migration_log_source")
    private MigrationLogSource migrationLogSource;
}
