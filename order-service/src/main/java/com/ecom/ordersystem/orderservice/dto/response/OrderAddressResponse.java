package com.ecom.ordersystem.orderservice.dto.response;

import java.util.UUID;

import com.ecom.ordersystem.orderservice.entity.OrderAddress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAddressResponse {
    
    private UUID addressId;
    private String receiverName;
    private String receiverPhone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    public static OrderAddressResponse from(OrderAddress orderAddress) {
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
}

