package com.ecom.ordersystem.orderservice.controller;

import java.util.UUID;

import com.ecom.ordersystem.orderservice.dto.request.*;
import com.ecom.ordersystem.orderservice.dto.response.OrderAddressResponse;
import com.ecom.ordersystem.orderservice.dto.response.OrderResponse;
import com.ecom.ordersystem.orderservice.dto.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecom.ordersystem.orderservice.mapper.OrderMapper;
import com.ecom.ordersystem.orderservice.entity.Order;
import com.ecom.ordersystem.orderservice.entity.OrderAddress;
import com.ecom.ordersystem.orderservice.service.OrderService;

import jakarta.validation.Valid;

/**
 * OrderController - REST API endpoints for order management.
 * 
 * Provides HTTP endpoints for:
 * - Creating and retrieving orders
 * - Managing order status and cancellations
 * - Managing order items and shipping addresses
 * - Tracking order status history
 * 
 * Architecture:
 * - Follows MVC pattern with clear separation of concerns
 * - Delegates business logic to OrderService layer
 * - Uses OrderMapper for entity-to-DTO conversions
 * - Validates input using Jakarta validation annotations
 * 
 * SOLID Principles:
 * - Single Responsibility: Handles HTTP concerns only
 * - Dependency Inversion: Depends on abstraction (OrderService and OrderMapper)
 * - Interface Segregation: Uses focused service interfaces
 */
@CrossOrigin(origins = "http://localhost:3000")//TODO: Change this when we have the frontend URL. This is just for development purpose.
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    //From here to below these API will be used publicly.

    /**
     * GET /api/orders - Retrieve list of all orders.
     * 
     * @return ResponseEntity with list of OrderResponse DTOs
     */
    @GetMapping("/orderList")
    public ResponseEntity<Object> getOrdersList() {
        // Fetch all orders from service layer
        var orders = orderService.getOrdersList();
        // Convert to DTOs using mapper
        var response = orderMapper.toOrderResponseList(orders);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/orders - Create a new order.
     * 
     * Accepts order creation request with items and shipping address.
     * Generates unique order number and initializes order in CREATED status.
     * 
     * @param request CreateOrderRequestParam containing order details (validated)
     * @return ResponseEntity with created OrderResponse DTO
     */
    @PostMapping("/createOrder")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequestParam request) {
        // Execute business logic to create order
        Order order = orderService.createOrder(request);
        // Convert to DTO for API response
        OrderResponse response = orderMapper.toOrderResponse(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/orders/{orderId} - Retrieve order by ID.
     * 
     * @param orderId UUID of the order to retrieve
     * @return ResponseEntity with OrderResponse DTO
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        // Fetch order by ID from service layer
        Order order = orderService.getOrderById(orderId);
        // Convert to DTO for API response
        OrderResponse response = orderMapper.toOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/number/{orderNumber} - Retrieve order by order number.
     * 
     * Alternative retrieval method using human-readable order number.
     * Useful for customer-facing operations.
     * 
     * @param orderNumber human-readable order number (e.g., ORD-2026-000001)
     * @return ResponseEntity with OrderResponse DTO
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        // Fetch order by order number from service layer
        Order order = orderService.getOrderByOrderNumber(orderNumber);
        // Convert to DTO for API response
        OrderResponse response = orderMapper.toOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/user/{userId} - Retrieve paginated orders for a user.
     * 
     * Supports pagination with default page size of 10.
     * Query Parameters:
     *   - page: Zero-based page number (default: 0)
     *   - size: Number of items per page (default: 10)
     * 
     * Example: GET /api/orders/user/{userId}?page=0&size=10
     * 
     * @param userId UUID of the customer
     * @param page zero-based page number
     * @param size number of items per page
     * @return ResponseEntity with paginated OrderResponse DTOs
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OrderResponse>> getUserOrders(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Create pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);
        // Fetch paginated orders from service layer
        Page<Order> orders = orderService.getUserOrders(userId, pageable);
        // Convert each order to DTO
        Page<OrderResponse> response = orders.map(orderMapper::toOrderResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/orders/{orderId}/cancel - Cancel an order.
     * 
     * Business Rules:
     * - Only orders in CREATED or PAYMENT_PENDING status can be cancelled
     * - Sets order status to CANCELLED
     * - Records status change in order history
     * 
     * @param orderId UUID of the order to cancel
     * @return ResponseEntity with updated OrderResponse DTO
     * @throws IllegalStateException if order cannot be cancelled in current status
     */
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID orderId) {
        // Execute cancellation business logic
        Order order = orderService.cancelOrder(orderId);
        // Convert to DTO for API response
        OrderResponse response = orderMapper.toOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    //From here to below these API will be used internally.

    /**
     * PATCH /api/orders/{orderId}/status - Update order status.
     * 
     * Allows changing order status to any valid OrderStatus value.
     * Records the status change with updatedBy information.
     * 
     * @param orderId UUID of the order
     * @param request UpdateOrderStatusRequest containing new status and updatedBy
     * @return ResponseEntity with updated OrderResponse DTO
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        // Execute status update business logic
        Order order = orderService.updateOrderStatus(orderId, request);
        // Convert to DTO for API response
        OrderResponse response = orderMapper.toOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/{orderId}/items - Retrieve all items in an order.
     * 
     * Returns the complete list of products ordered with quantities and prices.
     * 
     * @param orderId UUID of the order
     * @return ResponseEntity with list of OrderItemResponse DTOs
     */
    @GetMapping("/{orderId}/items")
    public ResponseEntity<Object> getOrderItems(@PathVariable UUID orderId) {
        // Fetch order items from service layer
        var items = orderService.getOrderItems(orderId);
        // Convert to DTOs using mapper
        var response = orderMapper.toOrderItemResponseList(items);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/{orderId}/shipping - Retrieve order shipping address.
     * 
     * Returns the delivery address for the order.
     * 
     * @param orderId UUID of the order
     * @return ResponseEntity with OrderAddressResponse DTO
     */
    @GetMapping("/{orderId}/shipping")
    public ResponseEntity<OrderAddressResponse> getOrderShipping(@PathVariable UUID orderId) {
        // Fetch shipping address from service layer
        OrderAddress address = orderService.getOrderShipping(orderId);
        // Convert to DTO using mapper
        OrderAddressResponse response = orderMapper.toOrderAddressResponse(address);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/orders/{orderId}/shipping - Update order shipping address.
     * 
     * Allows updating delivery address details.
     * Updates the order's lastModified timestamp.
     * 
     * @param orderId UUID of the order
     * @param updatedAddress OrderAddress containing updated address details
     * @return ResponseEntity with updated OrderAddressResponse DTO
     */
    @PutMapping("/{orderId}/shipping")
    public ResponseEntity<OrderAddressResponse> updateOrderShipping(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateShippingAddressRequest updatedAddress) {
        // Execute shipping address update business logic
        OrderAddress address = orderService.updateOrderShipping(orderId, updatedAddress);
        // Convert to DTO using mapper
        OrderAddressResponse response = orderMapper.toOrderAddressResponse(address);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/{orderId}/status-history - Retrieve order status history.
     * 
     * Returns chronological list of all status changes for the order,
     * including timestamp and user who made the change.
     * 
     * @param orderId UUID of the order
     * @return ResponseEntity with list of OrderStatusHistoryResponse DTOs
     */
    @GetMapping("/{orderId}/status-history")
    public ResponseEntity<Object> getOrderStatusHistory(@PathVariable UUID orderId) {
        // Fetch status history from service layer
        var history = orderService.getOrderStatusHistory(orderId);
        // Convert to DTOs using mapper
        var response = orderMapper.toOrderStatusHistoryResponseList(history);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/admin/search - Admin order search with filtering and pagination.
     * 
     * Advanced search endpoint for administrators to filter orders by multiple criteria.
     * 
     * Supported Filters:
     * - status: Order status (CREATED, PAYMENT_PENDING, PAID, PACKED, SHIPPED, DELIVERED, CANCELLED, FAILED)
     * - userId: Customer/User ID
     * - orderNumber: Order number (supports partial matching)
     * - startDate: Order creation start date (YYYY-MM-DD format)
     * - endDate: Order creation end date (YYYY-MM-DD format)
     * 
     * All filters are optional and can be combined.
     * When multiple filters are specified, ALL must match (AND logic).
     * 
     * Pagination:
     * - page: Zero-based page number (default: 0)
     * - size: Number of items per page (default: 10)
     * 
     * Examples:
     * - GET /api/orders/admin/search
     * - GET /api/orders/admin/search?status=SHIPPED
     * - GET /api/orders/admin/search?userId=550e8400-e29b-41d4-a716-446655440000
     * - GET /api/orders/admin/search?orderNumber=ORD-2026
     * - GET /api/orders/admin/search?status=SHIPPED&userId=550e8400-e29b-41d4-a716-446655440000
     * - GET /api/orders/admin/search?startDate=2026-03-01&endDate=2026-03-10
     * - GET /api/orders/admin/search?status=PAID&userId=550e8400-e29b-41d4-a716-446655440000&page=0&size=20
     *
     * @param request OrderSearchRequest containing filter criteria and pagination parameters
     * @return ResponseEntity with paginated OrderResponse DTOs matching filters
     */
//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search")
    public ResponseEntity<PageResponse<OrderResponse>> adminSearchOrders(@Valid OrderSearchRequest request) {

        // Build search criteria from request parameters
        OrderSearchCriteria criteria = OrderSearchCriteria.builder()
                .status(request.getStatus())
                .userId(request.getUserId())
                .orderNumber(request.getOrderNumber())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        // Create pagination object
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by("createdAt").descending());

        // Execute search with filters and pagination
        Page<Order> orders = orderService.adminSearchOrders(criteria, pageable);

        // Convert to DTOs using mapper
        PageResponse<OrderResponse> response = PageResponse.<OrderResponse>builder()
                .content(orders.getContent().stream()
                        .map(orderMapper::toOrderResponse)
                        .toList())
                .pageable(PageResponse.PageableInfo.builder()
                        .pageNumber(orders.getNumber())
                        .pageSize(orders.getSize())
                        .build())
                .totalElements(orders.getTotalElements())
                .totalPages(orders.getTotalPages())
                .build();

        return ResponseEntity.ok(response);
    }
}

