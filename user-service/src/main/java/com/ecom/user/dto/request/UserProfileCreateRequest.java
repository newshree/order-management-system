package com.ecom.user.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new user profile.
 *
 * This DTO is used to create a user profile from an identity service user ID.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileCreateRequest {

    /**
     * User ID from Identity Service (as a string).
     */
    @NotBlank(message = "Identity user ID is required")
    private String identityUserId;

    /**
     * User's first name.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * User's last name.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * User's email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * User's phone number (optional). Formats: +1-555-123-4567, 555-123-4567, etc.
     */
    @Pattern(regexp = "^[0-9+\\-() ]{7,20}$|^$", message = "Phone number must be 7-20 characters or empty")
    private String phoneNumber;

    /**
     * User's date of birth (optional).
     */
    private LocalDate dateOfBirth;
}
