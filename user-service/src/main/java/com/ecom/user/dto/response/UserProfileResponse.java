package com.ecom.user.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for UserProfile.
 *
 * This DTO is returned when fetching user profile information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    /**
     * Unique identifier for the user.
     */
    private UUID id;

    /**
     * User ID from Identity Service.
     */
    private UUID identityUserId;

    /**
     * User's first name.
     */
    private String firstName;

    /**
     * User's last name.
     */
    private String lastName;

    /**
     * User's email address.
     */
    private String email;

    /**
     * User's phone number.
     */
    private String phoneNumber;

    /**
     * User's date of birth.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    /**
     * Indicates if the user account is active.
     */
    private Boolean isActive;

    /**
     * Timestamp when the user profile was created.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user profile was last updated.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
