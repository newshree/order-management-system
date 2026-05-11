package com.ecom.user.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for UserAddress.
 *
 * This DTO is returned when fetching user address information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddressResponse {

    /**
     * Unique identifier for the address.
     */
    private UUID id;

    /**
     * Full name for this address (recipient name).
     */
    private String fullName;

    /**
     * Phone number for this address.
     */
    private String phoneNumber;

    /**
     * First line of the address.
     */
    private String addressLine1;

    /**
     * Second line of the address.
     */
    private String addressLine2;

    /**
     * Landmark for the address.
     */
    private String landmark;

    /**
     * City of the address.
     */
    private String city;

    /**
     * State/Province of the address.
     */
    private String state;

    /**
     * Country of the address.
     */
    private String country;

    /**
     * Postal code/ZIP code of the address.
     */
    private String postalCode;

    /**
     * Indicates if this is the default address.
     */
    private Boolean isDefault;

    /**
     * Timestamp when the address was created.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
