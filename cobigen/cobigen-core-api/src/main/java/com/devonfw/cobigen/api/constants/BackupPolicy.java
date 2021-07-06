package com.devonfw.cobigen.api.constants;

/**
 * This enumeration states if a backup is necessary or not.
 */
public enum BackupPolicy {
    /**
     * if no backup is necessary
     */
    NO_BACKUP,
    /**
     * if a backup should be made if it is possible to do so and otherwise continue without backup
     */
    BACKUP_IF_POSSIBLE,
    /**
     * if the backup is necessary and in case the backup cannot be created an exception will be thrown
     */
    ENFORCE_BACKUP
}
