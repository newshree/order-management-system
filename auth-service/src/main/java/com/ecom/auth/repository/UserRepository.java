package com.ecom.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.auth.entity.User;

/**
 * Repository interface for User entity persistence operations.
 *
 * Provides database access methods for querying and managing User entities.
 * Extends JpaRepository to inherit standard CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	/**
	 * Finds a user by email address.
	 *
	 * @param email the email address to search for
	 * @return an Optional containing the User if found, empty otherwise
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Checks if a user with the given email already exists.
	 *
	 * @param email the email address to check
	 * @return true if a user with the email exists, false otherwise
	 */
	boolean existsByEmail(String email);
}
