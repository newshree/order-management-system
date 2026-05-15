package com.ecom.cart.enums;

/**
 * Enum representing the status of a cart.
 * 
 * States:
 * - ACTIVE: Cart is actively being used by user
 * - ABANDONED: Cart has been inactive for a certain period
 * - CONVERTED: Cart has been converted to an order
 * - EXPIRED: Cart has expired (TTL exceeded)
 */
public enum CartStatus {
    ACTIVE,
    ABANDONED,
    CONVERTED,
    EXPIRED
}
