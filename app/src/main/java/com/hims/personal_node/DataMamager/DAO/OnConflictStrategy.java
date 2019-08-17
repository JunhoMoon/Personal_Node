package com.hims.personal_node.DataMamager.DAO;

public @interface OnConflictStrategy {
    /**
     * OnConflict strategy constant to replace the old data and continue the transaction.
     */
    int REPLACE = 1;
    /**
     * OnConflict strategy constant to rollback the transaction.
     */
    int ROLLBACK = 2;
    /**
     * OnConflict strategy constant to abort the transaction.
     */
    int ABORT = 3;
    /**
     * OnConflict strategy constant to fail the transaction.
     */
    int FAIL = 4;
    /**
     * OnConflict strategy constant to ignore the conflict.
     */
    int IGNORE = 5;
}
