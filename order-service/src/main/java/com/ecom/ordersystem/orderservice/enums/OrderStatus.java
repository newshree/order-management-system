package com.ecom.ordersystem.orderservice.enums;

/**
 * OrderStatus - Enumeration of valid order statuses.
 * 
 * Defines all possible states an order can transition through during its lifecycle.
 * Ensures type-safe status management and prevents invalid state values.
 * 
 * Order Lifecycle:
 * 1. CREATED: Order initially created but not yet submitted for payment
 * 2. PAYMENT_PENDING: Awaiting payment confirmation
 * 3. PAID: Payment received and confirmed
 * 4. PACKED: Items assembled and packaged for shipment
 * 5. SHIPPED: Order dispatched and in transit
 * 6. DELIVERED: Order successfully delivered to customer
 * 7. CANCELLED: Order cancelled (allowed only from CREATED or PAYMENT_PENDING)
 * 8. FAILED: Order processing failed (e.g., payment failed, inventory issues)
 * 
 * Design Pattern:
 * - Enumeration Pattern: Type-safe status representation
 * - Finite State Machine: Restricts to valid statuses only
 * 
 * State Transition Rules:
 * - CREATED -> PAYMENT_PENDING, CANCELLED
 * - PAYMENT_PENDING -> PAID, FAILED, CANCELLED
 * - PAID -> PACKED, FAILED
 * - PACKED -> SHIPPED, FAILED
 * - SHIPPED -> DELIVERED, FAILED
 * - DELIVERED -> (terminal state)
 * - CANCELLED -> (terminal state)
 * - FAILED -> (terminal state)
 * 
 * Database Persistence:
 * - Stored as STRING in database (EnumType.STRING)
 * - Allows database-level state tracking
 * - Supports filtering and reporting queries
 */
public enum OrderStatus {
    
    /**
     * Order created but not yet submitted for payment.
     * 
     * Initial status when order is first created.
     * Customer can still modify order or cancel it.
     */
    CREATED,
    
    /**
     * Order submitted for payment but not yet confirmed.
     * 
     * Customer is in payment process.
     * Can transition to PAID upon successful payment.
     * Can still be cancelled if payment is not completed.
     */
    PAYMENT_PENDING,
    
    /**
     * Payment successfully received and confirmed.
     * 
     * Order is now locked and ready for fulfillment.
     * Cannot be cancelled in this status.
     * Moves to PACKED for warehouse processing.
     */
    PAID,
    
    /**
     * Order items assembled and packaged for shipment.
     * 
     * Items are picked from inventory and packed.
     * Ready for carrier pickup.
     * Moves to SHIPPED when handed to shipping carrier.
     */
    PACKED,
    
    /**
     * Order dispatched and in transit to customer.
     * 
     * Order has been picked up by shipping carrier.
     * Customer can track shipment.
     * Moves to DELIVERED upon successful delivery.
     */
    SHIPPED,
    
    /**
     * Order successfully delivered to customer.
     * 
     * Terminal status indicating successful fulfillment.
     * Order lifecycle complete.
     */
    DELIVERED,
    
    /**
     * Order cancelled by customer or system.
     * 
     * Terminal status.
     * Can only be set if order was in CREATED or PAYMENT_PENDING status.
     * Triggers refund processing if payment was received.
     */
    CANCELLED,
    
    /**
     * Order processing failed (payment declined, inventory unavailable, etc.).
     * 
     * Terminal status indicating process failure.
     * May trigger customer notification and support action.
     * Refund processing may be initiated.
     */
    FAILED
}

