package com.ecom.ordersystem.orderservice.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreateOrderRequestParam - Data Transfer Object for order creation requests.
 * 
 * Carries order creation data from client to service layer.
 * Includes order items and shipping address in a single request.
 * 
 * Input Validation:
 * - All required fields are annotated with @NotNull
 * - Order items cannot be empty
 * - Validation is performed automatically by Spring Framework
 * 
 * SOLID Principles:
 * - Single Responsibility: Represents single API request structure
 * - Interface Segregation: Contains only necessary fields for creation
 * 
 * Design Pattern:
 * - Data Transfer Object (DTO) Pattern
 * - Decouples API layer from domain entities
 * - Provides clear input contract
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestParam {
    
    /**
     * Tenant identifier for multi-tenant system.
     * 
     * Identifies which tenant/organization this order belongs to.
     * Used for data isolation and segregation.
     * 
     * - Type: UUID
     * - Required: Yes
     * - Example: "550e8400-e29b-41d4-a716-446655440000"
     */
    @NotNull(message = "Tenant ID is required")
    private UUID tenantId;
    
    /**
     * User/Customer identifier who is placing the order.
     * 
     * Reference to the customer entity in user service.
     * Used for tracking customer orders and history.
     * 
     * - Type: UUID
     * - Required: Yes
     * - Example: "550e8400-e29b-41d4-a716-446655440001"
     */
    @NotNull(message = "User ID is required")
    private UUID userId;

    /**
     * List of items to be included in the order.
     * 
     * Must contain at least one item.
     * Each item includes product details and pricing.
     * 
     * - Type: List of OrderItemRequest
     * - Required: Yes
     * - Min Items: 1
     * - Max Items: Determined by business rules
     * 
     * @see OrderItemRequest
     */
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> orderItems;
    
    /**
     * Shipping/Delivery address for the order.
     * 
     * Complete delivery address including receiver details.
     * 
     * - Type: OrderAddressRequest
     * - Required: Yes
     * 
     * @see OrderAddressRequest
     */
    @NotNull(message = "Shipping address is required")
    private OrderAddressRequest shippingAddress;
}


