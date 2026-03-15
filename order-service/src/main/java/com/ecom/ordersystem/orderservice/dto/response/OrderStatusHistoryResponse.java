package com.ecom.ordersystem.orderservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ecom.ordersystem.orderservice.entity.OrderStatusHistory;
import com.ecom.ordersystem.orderservice.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistoryResponse {
    
    private UUID statusId;
    private OrderStatus status;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public static OrderStatusHistoryResponse from(OrderStatusHistory statusHistory) {
        return OrderStatusHistoryResponse.builder()
            .statusId(statusHistory.getId())
            .status(statusHistory.getStatus())
            .updatedBy(statusHistory.getUpdatedBy())
            .updatedAt(statusHistory.getUpdatedAt())
            .build();
    }
}

