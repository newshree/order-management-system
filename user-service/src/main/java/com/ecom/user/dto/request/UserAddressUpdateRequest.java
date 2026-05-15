package com.ecom.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing user address.
 *
 * This DTO is used to update address information.
 * All fields are optional to support partial updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddressUpdateRequest {

    /**
     * Full name for this address (recipient name).
     */
    private String fullName;

    /**
     * Phone number for this address.
     */
    @Pattern(regexp = "^[0-9+\\-() ]{7,20}$|^$", message = "Phone number should be valid")
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
}
