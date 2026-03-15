package com.ecom.ordersystem.orderservice.dto.request;

import com.ecom.ordersystem.orderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateOrderStatusRequest - Data Transfer Object for order status update requests.
 * 
 * Used when updating an order's status via PATCH endpoint.
 * Contains new status and information about who initiated the change.
 * 
 * Validation:
 * - Status must be a valid OrderStatus enum value
 * - updatedBy is optional (defaults to "SYSTEM" in service layer)
 * 
 * Usage:
 * - Sent with PATCH /api/orders/{orderId}/status
 * - Triggers status change and audit trail entry
 * 
 * Design Pattern:
 * - Data Transfer Object (DTO) Pattern
 * - Command Pattern: Represents request to change state
 * 
 * SOLID Principles:
 * - Single Responsibility: Represents single status update request
 * - Interface Segregation: Contains only fields needed for status update
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    /**
     * New status to transition to.
     * 
     * Must be a valid OrderStatus enum value.
     * 
     * Valid values: CREATED, PAYMENT_PENDING, PAID, PACKED, SHIPPED, DELIVERED, CANCELLED, FAILED
     * 
     * - Type: OrderStatus enum
     * - Required: Yes
     * - Example: OrderStatus.PAID
     * 
     * @see com.ecom.ordersystem.orderservice.enums.OrderStatus
     */
    private OrderStatus status;
    
    /**
     * User or system identifier who initiated this status change.
     * 
     * Identifies the actor making the change for audit trail purposes.
     * Used to track who made what changes and when.
     * 
     * - Type: String
     * - Required: No (optional)
     * - Default: "SYSTEM" (if not provided in request)
     * - Example: "admin@company.com", "payment-service", "warehouse-manager"
     * 
     * Common values:
     * - "SYSTEM": Automated system-triggered change
     * - User ID/Email: Manual change by user
     * - Service name: Change triggered by another microservice
     */
    private String updatedBy;
}

