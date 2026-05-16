package com.ecom.payment.enums;

/**
 * PaymentStatus enum representing all possible states of a payment.
 *
 * States:
 * - PENDING: Payment initiated but not yet processed
 * - SUCCESS: Payment successfully processed
 * - FAILED: Payment processing failed
 * - REFUNDED: Payment has been refunded
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED
}
