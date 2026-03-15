package com.ecom.ordersystem.orderservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ecom.ordersystem.orderservice.entity.Order;
import com.ecom.ordersystem.orderservice.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderResponse - Data Transfer Object for order data in API responses.
 * 
 * Represents complete order information to be sent to client.
 * Includes order metadata, items, and shipping address.
 * Built from Order entity using builder pattern.
 * 
 * Usage:
 * - Used in API responses for order retrieval endpoints
 * - Contains all order information relevant to clients
 * - Hides internal implementation details
 * 
 * Design Pattern:
 * - Data Transfer Object (DTO) Pattern
 * - Builder Pattern (via Lombok @Builder)
 * - Decouples API layer from domain entities
 * 
 * SOLID Principles:
 * - Single Responsibility: Represents order data in API
 * - Interface Segregation: Contains only public-facing fields
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    /**
     * Unique identifier of the order.
     * 
     * - Type: UUID
     * - Generated: Automatically by system
     * - Immutable: Cannot be changed
     */
    private UUID orderId;
    
    /**
     * Human-readable order number.
     * 
     * Format: ORD-{YEAR}-{SEQUENCE}
     * Example: ORD-2026-000001
     * 
     * - Type: String
     * - Unique: Yes
     * - Immutable: Set at creation
     */
    private String orderNumber;
    
    /**
     * Tenant identifier for multi-tenant system.
     * 
     * - Type: UUID
     */
    private UUID tenantId;
    
    /**
     * Customer/User identifier.
     * 
     * - Type: UUID
     */
    private UUID userId;
    
    /**
     * Current order status.
     * 
     * Values: CREATED, PAYMENT_PENDING, PAID, PACKED, SHIPPED, DELIVERED, CANCELLED, FAILED
     * 
     * - Type: OrderStatus enum
     */
    private OrderStatus status;
    
    /**
     * Total order amount.
     * 
     * - Type: BigDecimal
     * - Precision: 2 decimal places (cents)
     */
    private BigDecimal totalAmount;
    
    /**
     * Timestamp when order was created.
     * 
     * - Type: LocalDateTime
     * - Format: ISO-8601
     * - Immutable: Set at creation
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp of last update.
     * 
     * - Type: LocalDateTime
     * - Format: ISO-8601
     * - Updated: When any order property changes
     */
    private LocalDateTime updatedAt;
    
    /**
     * List of items ordered.
     * 
     * Complete list of products with quantities and prices.
     * 
     * - Type: List of OrderItemResponse
     * 
     * @see OrderItemResponse
     */
    private List<OrderItemResponse> orderItems;
    
    /**
     * Shipping/Delivery address.
     * 
     * Complete address where order will be delivered.
     * 
     * - Type: OrderAddressResponse
     * 
     * @see OrderAddressResponse
     */
    private OrderAddressResponse shippingAddress;

    /**
     * Converts an Order entity to OrderResponse DTO.
     * 
     * Builds complete response from all order details including
     * nested items and address information.
     * 
     * @param order the Order entity to convert
     * @return OrderResponse DTO built from entity
     */
    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
            .orderId(order.getId())
            .orderNumber(order.getOrderNumber())
            .tenantId(order.getTenantId())
            .userId(order.getUserId())
            .status(order.getStatus())
            .totalAmount(order.getTotalAmount())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .orderItems(order.getOrderItems() != null ? 
                order.getOrderItems().stream().map(OrderItemResponse::from).toList() : null)
            .shippingAddress(order.getOrderAddress() != null ? 
                OrderAddressResponse.from(order.getOrderAddress()) : null)
            .build();
    }
}
