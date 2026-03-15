package com.ecom.ordersystem.orderservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ecom.ordersystem.orderservice.dto.request.OrderItemRequest;
import com.ecom.ordersystem.orderservice.dto.request.OrderSearchCriteria;
import com.ecom.ordersystem.orderservice.dto.request.UpdateShippingAddressRequest;
import com.ecom.ordersystem.orderservice.enums.ErrorCode;
import com.ecom.ordersystem.orderservice.enums.OrderStatus;
import com.ecom.ordersystem.orderservice.exception.BadRequestException;
import com.ecom.ordersystem.orderservice.exception.ResourceNotFoundException;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.ordersystem.orderservice.dto.request.CreateOrderRequestParam;
import com.ecom.ordersystem.orderservice.dto.request.UpdateOrderStatusRequest;
import com.ecom.ordersystem.orderservice.entity.Order;
import com.ecom.ordersystem.orderservice.entity.OrderAddress;
import com.ecom.ordersystem.orderservice.entity.OrderItem;
import com.ecom.ordersystem.orderservice.entity.OrderStatusHistory;
import com.ecom.ordersystem.orderservice.repository.OrderRepository;
import com.ecom.ordersystem.orderservice.specification.OrderSpecification;
import com.ecom.ordersystem.orderservice.util.OrderNumberGenerator;

import jakarta.persistence.EntityNotFoundException;

/**
 * OrderService - Core business logic for order management operations.
 *
 * This service implements the business rules for:
 * - Order creation and management
 * - Order status tracking and transitions
 * - Shipping address management
 * - Order items management
 * - Order status history management
 *
 * Design Patterns Applied:
 * - Service Layer Pattern: Encapsulates business logic
 * - Repository Pattern: Data access abstraction via OrderRepository
 * - Transactional Pattern: Ensures data consistency with @Transactional
 *
 * SOLID Principles:
 * - Single Responsibility: Focuses solely on order business logic
 * - Open/Closed: Open for extension, closed for modification
 * - Liskov Substitution: Follows service contracts consistently
 * - Interface Segregation: Uses focused interfaces
 * - Dependency Inversion: Depends on abstractions (OrderRepository)
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderNumberGenerator orderNumberGenerator;

    /**
     * Retrieves all orders from the database.
     *
     * @return List of all Order entities
     */
    public List<Order> getOrdersList() {
        return orderRepository.findAll();
    }

    /**
     * Creates a new order with the provided details.
     *
     * Business Logic:
     * - Generates a unique order number
     * - Initializes order status to CREATED
     * - Creates associated order items from request
     * - Creates shipping address from request
     * - Creates initial status history entry
     *
     * @param request CreateOrderRequestParam containing order details
     * @return newly created Order entity
     */
    @Transactional
    public Order createOrder(CreateOrderRequestParam request) {
        // Initialize order with request data
        Order order = new Order();
        order.setOrderNumber(orderNumberGenerator.generateOrderNumber());
        order.setTenantId(request.getTenantId());
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setTotalAmount(calculateTotalAmount(request.getOrderItems()));

        // Create order items from request
        List<OrderItem> orderItems = createOrderItemsFromRequest(order, request);
        order.setOrderItems(orderItems);

        // Create shipping address from request
        OrderAddress orderAddress = createOrderAddressFromRequest(order, request);
        order.setOrderAddress(orderAddress);

        // Create initial status history entry
        List<OrderStatusHistory> statusHistories = initializeStatusHistory(order, request);
        order.setStatusHistory(statusHistories);

        // Persist the complete order with all related entities
        return orderRepository.save(order);
    }

    /**
     * Calculates total amount
     *
     * @param orderItems
     * @return
     */
    private BigDecimal calculateTotalAmount(@NotEmpty(message = "Order items cannot be empty") List<OrderItemRequest> orderItems) {
        return orderItems.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param orderId the UUID of the order
     * @return Order entity
     * @throws ResourceNotFoundException if order not founds
     */
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    ErrorCode.ORDER_NOT_FOUND,
                    "Order not found with id: " + orderId
                )
            );
    }

    /**
     * Retrieves an order by its human-readable order number.
     *
     * @param orderNumber the order number string
     * @return Order entity
     * @throws ResourceNotFoundException if order not found
     */
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    ErrorCode.ORDER_NOT_FOUND,
                    "Order not found with order number: " + orderNumber
                )
            );
    }

    /**
     * Retrieves paginated orders for a specific user.
     *
     * @param userId the UUID of the customer
     * @param pageable pagination information
     * @return Page of Order entities
     */
    public Page<Order> getUserOrders(UUID userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    /**
     * Cancels an existing order.
     *
     * Business Rules:
     * - Only orders in CREATED or PAYMENT_PENDING status can be cancelled
     * - Updates order status to CANCELLED
     * - Adds status history entry
     * - Updates the updatedAt timestamp
     *
     * @param orderId the UUID of the order to cancel
     * @return updated Order entity
     * @throws BadRequestException if order cannot be cancelled in current status
     */
    @Transactional
    public Order cancelOrder(UUID orderId) {
        Order order = getOrderById(orderId);

        // Validate order can be cancelled based on current status
        if (!canCancelOrder(order)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_ORDER_STATE,
                    "Order cannot be cancelled in status: " + order.getStatus()
            );
        }

        // Update order status
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        // Record status change in history
        addStatusHistory(order, OrderStatus.CANCELLED, order.getUserId().toString());

        return orderRepository.save(order);
    }

    /**
     * Updates the status of an order.
     *
     * Business Logic:
     * - Changes the order status to the provided value
     * - Records the status change in history with updatedBy information
     * - Updates the updatedAt timestamp
     *
     * @param orderId the UUID of the order
     * @param request UpdateOrderStatusRequest containing new status and updatedBy
     * @return updated Order entity
     */
    @Transactional
    public Order updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request) {
        Order order = getOrderById(orderId);

        // Validate status transition
        if (!isValidStatusTransition(order.getStatus(), request.getStatus())) {
            throw new BadRequestException(
                    ErrorCode.INVALID_ORDER_STATE,
                    "Invalid status transition from " + order.getStatus() + " to " + request.getStatus()
            );
        }

        // Update order status
        order.setStatus(request.getStatus());
        order.setUpdatedAt(LocalDateTime.now());

        // Record status change in history
        String updatedBy = request.getUpdatedBy() != null
                ? request.getUpdatedBy()
                : order.getUserId().toString();
        addStatusHistory(order, request.getStatus(), updatedBy);

        return orderRepository.save(order);
    }

    /**
     * Validates whether the status transition is allowed.
     */
    private boolean isValidStatusTransition(OrderStatus current, OrderStatus next) {

        return switch (current) {
            case CREATED -> next == OrderStatus.PAYMENT_PENDING || next == OrderStatus.CANCELLED;
            case PAYMENT_PENDING -> next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
            default -> false;
        };
    }

    /**
     * Retrieves all items in an order.
     *
     * @param orderId the UUID of the order
     * @return List of OrderItem entities
     */
    public List<OrderItem> getOrderItems(UUID orderId) {
        Order order = getOrderById(orderId);
        return order.getOrderItems();
    }

    /**
     * Retrieves the shipping address for an order.
     *
     * @param orderId the UUID of the order
     * @return OrderAddress entity
     * @throws ResourceNotFoundException if shipping address not found
     */
    public OrderAddress getOrderShipping(UUID orderId) {
        Order order = getOrderById(orderId);
        if (order.getOrderAddress() == null) {
            throw new ResourceNotFoundException(
                    ErrorCode.SHIPPING_ADDRESS_NOT_FOUND,
                    "Shipping address not found for order: " + orderId
            );
        }
        return order.getOrderAddress();
    }

    /**
     * Updates the shipping address for an order.
     *
     * @param orderId the UUID of the order
     * @param updatedAddress the new shipping address details
     * @return updated OrderAddress entity
     * @throws ResourceNotFoundException if order or shipping address not found
     */
    @Transactional
    public OrderAddress updateOrderShipping(UUID orderId, UpdateShippingAddressRequest updatedAddress) {
        Order order = getOrderById(orderId);
        OrderAddress orderAddress = order.getOrderAddress();

        if (orderAddress == null) {
            throw new ResourceNotFoundException(
                    ErrorCode.SHIPPING_ADDRESS_NOT_FOUND,
                    "Shipping address not found for order: " + orderId
            );
        }

        // Update address fields
        orderAddress.setReceiverName(updatedAddress.getReceiverName());
        orderAddress.setReceiverPhone(updatedAddress.getReceiverPhone());
        orderAddress.setAddressLine1(updatedAddress.getAddressLine1());
        orderAddress.setAddressLine2(updatedAddress.getAddressLine2());
        orderAddress.setCity(updatedAddress.getCity());
        orderAddress.setState(updatedAddress.getState());
        orderAddress.setCountry(updatedAddress.getCountry());
        orderAddress.setPostalCode(updatedAddress.getPostalCode());

        // Update order's last modified timestamp
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return orderAddress;
    }

    /**
     * Retrieves the status history for an order.
     *
     * @param orderId the UUID of the order
     * @return List of OrderStatusHistory entries
     */
    public List<OrderStatusHistory> getOrderStatusHistory(UUID orderId) {
        Order order = getOrderById(orderId);
        return order.getStatusHistory();
    }

    /**
     * Determines if an order can be cancelled based on its current status.
     *
     * Business Rule: Only CREATED and PAYMENT_PENDING orders can be cancelled.
     *
     * @param order the Order entity to check
     * @return true if order can be cancelled, false otherwise
     */
    private boolean canCancelOrder(Order order) {
        return order.getStatus() == OrderStatus.CREATED ||
               order.getStatus() == OrderStatus.PAYMENT_PENDING;
    }

    /**
     * Creates OrderItem entities from the request.
     *
     * @param order the parent Order entity
     * @param request the CreateOrderRequestParam containing item details
     * @return List of OrderItem entities
     */
    private List<OrderItem> createOrderItemsFromRequest(Order order, CreateOrderRequestParam request) {
        return request.getOrderItems().stream()
            .map(itemRequest -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(itemRequest.getProductId());
                orderItem.setProductName(itemRequest.getProductName());
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setUnitPrice(itemRequest.getUnitPrice());
                orderItem.setOrder(order);
                return orderItem;
            })
            .toList();
    }

    /**
     * Creates an OrderAddress entity from the request.
     *
     * @param order the parent Order entity
     * @param request the CreateOrderRequestParam containing address details
     * @return OrderAddress entity
     */
    private OrderAddress createOrderAddressFromRequest(Order order, CreateOrderRequestParam request) {
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setReceiverName(request.getShippingAddress().getReceiverName());
        orderAddress.setReceiverPhone(request.getShippingAddress().getReceiverPhone());
        orderAddress.setAddressLine1(request.getShippingAddress().getAddressLine1());
        orderAddress.setAddressLine2(request.getShippingAddress().getAddressLine2());
        orderAddress.setCity(request.getShippingAddress().getCity());
        orderAddress.setState(request.getShippingAddress().getState());
        orderAddress.setCountry(request.getShippingAddress().getCountry());
        orderAddress.setPostalCode(request.getShippingAddress().getPostalCode());
        orderAddress.setOrder(order);
        return orderAddress;
    }

    /**
     * Initializes the status history with the CREATED status.
     *
     * @param order the Order entity to initialize history for
     * @return List containing the initial status history entry
     */
    private List<OrderStatusHistory> initializeStatusHistory(Order order, CreateOrderRequestParam request) {
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setStatus(OrderStatus.CREATED);
        statusHistory.setUpdatedBy(request.getUserId().toString());
        statusHistory.setUpdatedAt(LocalDateTime.now());
        statusHistory.setOrder(order);
        return new ArrayList<>(List.of(statusHistory));
    }

    /**
     * Adds a status history entry to an order.
     *
     * @param order the Order entity
     * @param status the new OrderStatus
     * @param updatedBy the user or system that updated the status
     */
    private void addStatusHistory(Order order, OrderStatus status, String updatedBy) {
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setStatus(status);
        statusHistory.setUpdatedBy(updatedBy);
        statusHistory.setUpdatedAt(LocalDateTime.now());
        statusHistory.setOrder(order);

        // Add to existing history
        List<OrderStatusHistory> histories = order.getStatusHistory();
        histories.add(statusHistory);
        order.setStatusHistory(histories);
    }

    /**
     * Searches and filters orders based on provided criteria.
     *
     * Admin functionality for advanced order search with multiple filter options:
     * - Status filtering
     * - User/Customer ID filtering
     * - Order number search (partial match)
     * - Date range filtering (creation date)
     *
     * All filters are optional and can be combined.
     * When multiple filters are specified, ALL must match (AND logic).
     *
     * Implementation:
     * - Uses Specification pattern for dynamic query building
     * - Avoids method explosion for filter combinations
     * - Database-side filtering for performance
     *
     * Examples:
     * - Search by status: status=SHIPPED
     * - Search by date range: startDate=2026-03-01&endDate=2026-03-10
     * - Combined filters: status=PAID&userId=123&page=0&size=20
     *
     * @param criteria OrderSearchCriteria containing filter parameters (all optional)
     * @param pageable pagination information (page number, size, sorting)
     * @return Page of Order entities matching all specified criteria
     */
    public Page<Order> adminSearchOrders(OrderSearchCriteria criteria, Pageable pageable) {
        if (criteria.getStartDate() != null && criteria.getEndDate() != null
                && criteria.getStartDate().isAfter(criteria.getEndDate())) {

            throw new BadRequestException(
                    ErrorCode.INVALID_DATE_RANGE,
                    "Start date cannot be after end date"
            );
        }
        // Build dynamic specification from criteria
        Specification<Order> spec = Specification.allOf();

        // Add status filter if specified
        if (criteria.getStatus() != null) {
            spec = spec.and(OrderSpecification.byStatus(criteria.getStatus()));
        }

        // Add user ID filter if specified
        if (criteria.getUserId() != null) {
            spec = spec.and(OrderSpecification.byUserId(criteria.getUserId()));
        }

        // Add order number filter if specified
        if (criteria.getOrderNumber() != null && !criteria.getOrderNumber().isBlank()) {
            spec = spec.and(OrderSpecification.byOrderNumber(criteria.getOrderNumber()));
        }

        // Add date range filter if any date is specified
        if (criteria.getStartDate() != null || criteria.getEndDate() != null) {
            spec = spec.and(OrderSpecification.byDateRange(criteria.getStartDate(), criteria.getEndDate()));
        }

        // Execute query with all filters and pagination
        return orderRepository.findAll(spec, pageable);
    }

}
