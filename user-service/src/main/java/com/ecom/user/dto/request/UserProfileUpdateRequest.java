package com.ecom.user.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing user profile.
 *
 * This DTO is used to update user profile information.
 * All fields are optional to support partial updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateRequest {

    /**
     * User's first name.
     */
    private String firstName;

    /**
     * User's last name.
     */
    private String lastName;

    /**
     * User's phone number (optional). Formats: +1-555-123-4567, 555-123-4567, etc.
     */
    @Pattern(regexp = "^[0-9+\\-() ]{7,20}$|^$", message = "Phone number must be 7-20 characters or empty")
    private String phoneNumber;

    /**
     * User's date of birth.
     */
    private LocalDate dateOfBirth;
}
