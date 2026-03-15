package com.ecom.ordersystem.orderservice.mapper;

import java.util.List;

import com.ecom.ordersystem.orderservice.dto.response.OrderAddressResponse;
import com.ecom.ordersystem.orderservice.dto.response.OrderItemResponse;
import com.ecom.ordersystem.orderservice.dto.response.OrderResponse;
import com.ecom.ordersystem.orderservice.dto.response.OrderStatusHistoryResponse;
import com.ecom.ordersystem.orderservice.entity.Order;
import com.ecom.ordersystem.orderservice.entity.OrderAddress;
import com.ecom.ordersystem.orderservice.entity.OrderItem;
import com.ecom.ordersystem.orderservice.entity.OrderStatusHistory;

/**
 * Mapper interface for converting between domain models and DTOs.
 * 
 * Follows the Mapper/Converter design pattern to maintain separation of concerns.
 * This interface ensures that domain entities remain independent of presentation layer concerns.
 * 
 * Principle Applied: Single Responsibility Principle (SRP)
 * - Mapper focuses solely on data transformation
 */
public interface OrderMapper {

    /**
     * Converts an Order entity to an OrderResponse DTO.
     * 
     * @param order the Order entity to convert
     * @return OrderResponse containing order details
     */
    OrderResponse toOrderResponse(Order order);

    /**
     * Converts a list of Order entities to a list of OrderResponse DTOs.
     * 
     * @param orders the list of Order entities
     * @return List of OrderResponse DTOs
     */
    List<OrderResponse> toOrderResponseList(List<Order> orders);

    /**
     * Converts an OrderItem entity to an OrderItemResponse DTO.
     * 
     * @param orderItem the OrderItem entity
     * @return OrderItemResponse containing item details
     */
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    /**
     * Converts a list of OrderItem entities to a list of OrderItemResponse DTOs.
     * 
     * @param orderItems the list of OrderItem entities
     * @return List of OrderItemResponse DTOs
     */
    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems);

    /**
     * Converts an OrderAddress entity to an OrderAddressResponse DTO.
     * 
     * @param orderAddress the OrderAddress entity
     * @return OrderAddressResponse containing address details
     */
    OrderAddressResponse toOrderAddressResponse(OrderAddress orderAddress);

    /**
     * Converts an OrderStatusHistory entity to an OrderStatusHistoryResponse DTO.
     * 
     * @param statusHistory the OrderStatusHistory entity
     * @return OrderStatusHistoryResponse containing status history details
     */
    OrderStatusHistoryResponse toOrderStatusHistoryResponse(OrderStatusHistory statusHistory);

    /**
     * Converts a list of OrderStatusHistory entities to a list of OrderStatusHistoryResponse DTOs.
     * 
     * @param statusHistories the list of OrderStatusHistory entities
     * @return List of OrderStatusHistoryResponse DTOs
     */
    List<OrderStatusHistoryResponse> toOrderStatusHistoryResponseList(List<OrderStatusHistory> statusHistories);

}

