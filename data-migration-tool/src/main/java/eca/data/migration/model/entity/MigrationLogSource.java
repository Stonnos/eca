package eca.data.migration.model.entity;

/**
 * Migration source.
 *
 * @author Roman Batygin
 */
public enum MigrationLogSource {

    /**
     * Data has been received by job from directory specified in configs
     */
    JOB,

    /**
     * Data has been received from controller
     */
    MANUAL
}
