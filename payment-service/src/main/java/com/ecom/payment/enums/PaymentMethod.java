package com.ecom.payment.enums;

/**
 * PaymentMethod enum representing available payment methods.
 *
 * Supported Methods:
 * - CARD: Credit/Debit card payment
 * - UPI: Unified Payments Interface (Indian digital payment)
 * - NET_BANKING: Direct bank transfer
 * - COD: Cash on Delivery
 * - WALLET: Digital wallet payment
 */
public enum PaymentMethod {
    CARD,
    UPI,
    NET_BANKING,
    COD,
    WALLET
}
