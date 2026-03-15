package com.ecom.ordersystem.orderservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.ecom.ordersystem.orderservice.entity.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    
    private UUID itemId;
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;

    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
            .itemId(orderItem.getId())
            .productId(orderItem.getProductId())
            .productName(orderItem.getProductName())
            .quantity(orderItem.getQuantity())
            .unitPrice(orderItem.getUnitPrice())
            .build();
    }
}

