package eca.data.migration.controller;

import eca.data.migration.model.MultipartFileResource;
import eca.data.migration.model.entity.MigrationLogSource;
import eca.data.migration.service.MigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;

/**
 * Migration controller.
 *
 * @author Roman Batygin
 */
@Slf4j
@RestController
@RequestMapping("/migration-tool")
public class MigrationController {

    private final MigrationService migrationService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param migrationService - migration service bean
     */
    @Inject
    public MigrationController(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    /**
     * Migrates training data into database.
     *
     * @param dataFile - multipart data file
     * @return response entity
     */
    @PostMapping(value = "/migrate")
    public ResponseEntity migrate(@RequestParam("dataFile") MultipartFile dataFile) {
        try {
            migrationService.migrateData(new MultipartFileResource(dataFile), MigrationLogSource.MANUAL);
        } catch (Exception ex) {
            log.error("There was an error while migration file '{}': {}", dataFile.getOriginalFilename(),
                    ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
