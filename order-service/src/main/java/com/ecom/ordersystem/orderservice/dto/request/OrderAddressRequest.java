package com.ecom.ordersystem.orderservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddressRequest {
    
    @NotNull(message = "Receiver name is required")
    private String receiverName;
    
    @NotNull(message = "Receiver phone is required")
    private String receiverPhone;
    
    @NotNull(message = "Address line 1 is required")
    private String addressLine1;
    
    private String addressLine2;
    
    @NotNull(message = "City is required")
    private String city;
    
    @NotNull(message = "State is required")
    private String state;
    
    @NotNull(message = "Country is required")
    private String country;
    
    @NotNull(message = "Postal code is required")
    private String postalCode;
}

