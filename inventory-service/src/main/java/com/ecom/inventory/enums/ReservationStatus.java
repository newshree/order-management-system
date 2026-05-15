package com.ecom.inventory.enums;

/**
 * Enumeration for reservation statuses.
 */
public enum ReservationStatus {

    /**
     * Stock is reserved for the order.
     */
    RESERVED,

    /**
     * Reservation is committed (payment processed).
     */
    COMMITTED,

    /**
     * Reservation is released (order cancelled/failed).
     */
    RELEASED
}
