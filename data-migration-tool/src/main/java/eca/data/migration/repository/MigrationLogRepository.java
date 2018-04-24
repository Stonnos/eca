package eca.data.migration.repository;

import eca.data.migration.model.entity.MigrationLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link MigrationLog} persistence entity.
 *
 * @author Roman Batygin
 */
public interface MigrationLogRepository extends JpaRepository<MigrationLog, Long> {
}
