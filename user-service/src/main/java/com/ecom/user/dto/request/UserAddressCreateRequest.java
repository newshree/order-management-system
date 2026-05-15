package com.ecom.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new user address.
 *
 * This DTO is used to add a new address for a user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddressCreateRequest {

    /**
     * Full name for this address (recipient name).
     */
    @NotBlank(message = "Full name is required")
    private String fullName;

    /**
     * Phone number for this address (formats: +1-555-123-4567, 555-123-4567, etc.).
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\-() ]{7,20}$", message = "Phone number must be 7-20 characters, containing digits, +, -, (, ), or spaces")
    private String phoneNumber;

    /**
     * First line of the address.
     */
    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    /**
     * Second line of the address (optional).
     */
    private String addressLine2;

    /**
     * Landmark for the address (optional).
     */
    private String landmark;

    /**
     * City of the address.
     */
    @NotBlank(message = "City is required")
    private String city;

    /**
     * State/Province of the address.
     */
    @NotBlank(message = "State is required")
    private String state;

    /**
     * Country of the address.
     */
    @NotBlank(message = "Country is required")
    private String country;

    /**
     * Postal code/ZIP code of the address.
     */
    @NotBlank(message = "Postal code is required")
    private String postalCode;

    /**
     * Indicates if this should be the default address.
     */
    @Builder.Default
    private Boolean isDefault = false;
}
