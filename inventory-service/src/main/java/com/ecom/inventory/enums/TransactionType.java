package com.ecom.inventory.enums;

/**
 * Contains all the Transaction Types for inventory operations.
 *
 *
 */
public enum TransactionType {

    /**
     * Stock reservation.
     */
    RESERVE,

    /**
     * Stock commitment (payment processed).
     */
    COMMIT,

    /**
     * Stock release (order cancelled/failed).
     */
    RELEASE,

    /**
     * Initial inventory creation.
     */
    CREATE,

    /**
     * Inventory update.
     */
    UPDATE,

}
