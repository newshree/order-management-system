package com.ecom.ordersystem.orderservice.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecom.ordersystem.orderservice.dto.response.OrderAddressResponse;
import com.ecom.ordersystem.orderservice.dto.response.OrderItemResponse;
import com.ecom.ordersystem.orderservice.dto.response.OrderResponse;
import com.ecom.ordersystem.orderservice.dto.response.OrderStatusHistoryResponse;
import com.ecom.ordersystem.orderservice.entity.Order;
import com.ecom.ordersystem.orderservice.entity.OrderAddress;
import com.ecom.ordersystem.orderservice.entity.OrderItem;
import com.ecom.ordersystem.orderservice.entity.OrderStatusHistory;

/**
 * Implementation of OrderMapper interface.
 * 
 * Handles all data transformation between domain entities and Data Transfer Objects (DTOs).
 * This component ensures clear separation between business logic (service layer) and
 * presentation concerns (controller/API layer).
 * 
 * Design Pattern: Component/Converter Pattern
 * Principle Applied: 
 * - Single Responsibility Principle (SRP): Focused solely on data transformation
 * - Dependency Inversion Principle (DIP): Depends on abstraction (OrderMapper interface)
 */
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        return OrderResponse.builder()
            .orderId(order.getId())
            .orderNumber(order.getOrderNumber())
            .tenantId(order.getTenantId())
            .userId(order.getUserId())
            .status(order.getStatus())
            .totalAmount(order.getTotalAmount())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .orderItems(toOrderItemResponseList(order.getOrderItems()))
            .shippingAddress(order.getOrderAddress() != null ? 
                toOrderAddressResponse(order.getOrderAddress()) : null)
            .build();
    }

    @Override
    public List<OrderResponse> toOrderResponseList(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }
        return orders.stream()
            .map(this::toOrderResponse)
            .toList();
    }

    @Override
    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        return OrderItemResponse.builder()
            .itemId(orderItem.getId())
            .productId(orderItem.getProductId())
            .productName(orderItem.getProductName())
            .quantity(orderItem.getQuantity())
            .unitPrice(orderItem.getUnitPrice())
            .build();
    }

    @Override
    public List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return List.of();
        }
        return orderItems.stream()
            .map(this::toOrderItemResponse)
            .toList();
    }

    @Override
    public OrderAddressResponse toOrderAddressResponse(OrderAddress orderAddress) {
        if (orderAddress == null) {
            return null;
        }

        return OrderAddressResponse.builder()
            .addressId(orderAddress.getId())
            .receiverName(orderAddress.getReceiverName())
            .receiverPhone(orderAddress.getReceiverPhone())
            .addressLine1(orderAddress.getAddressLine1())
            .addressLine2(orderAddress.getAddressLine2())
            .city(orderAddress.getCity())
            .state(orderAddress.getState())
            .country(orderAddress.getCountry())
            .postalCode(orderAddress.getPostalCode())
            .build();
    }

    @Override
    public OrderStatusHistoryResponse toOrderStatusHistoryResponse(OrderStatusHistory statusHistory) {
        if (statusHistory == null) {
            return null;
        }

        return OrderStatusHistoryResponse.builder()
            .statusId(statusHistory.getId())
            .status(statusHistory.getStatus())
            .updatedBy(statusHistory.getUpdatedBy())
            .updatedAt(statusHistory.getUpdatedAt())
            .build();
    }

    @Override
    public List<OrderStatusHistoryResponse> toOrderStatusHistoryResponseList(List<OrderStatusHistory> statusHistories) {
        if (statusHistories == null || statusHistories.isEmpty()) {
            return List.of();
        }
        return statusHistories.stream()
            .map(this::toOrderStatusHistoryResponse)
            .toList();
    }
}

