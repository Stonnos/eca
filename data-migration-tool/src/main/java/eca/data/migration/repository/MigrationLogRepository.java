package eca.data.migration.repository;

import eca.data.migration.model.entity.MigrationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository to manage with {@link MigrationLog} persistence entity.
 *
 * @author Roman Batygin
 */
public interface MigrationLogRepository extends JpaRepository<MigrationLog, Long> {

    /**
     * Finds last migrated table unique index.
     *
     * @return last migrated table unique index
     */
    @Query("select coalesce(max(ml.lastTableIndex), 0) from MigrationLog ml")
    Long findLastTableIndex();
}
