package com.ecom.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Auth Service application.
 *
 * Bootstraps the Spring Boot application for handling user authentication,
 * registration, login, and token management functionalities.
 */
@SpringBootApplication
public class AuthServiceApplication {

	/**
	 * Main method to start the Auth Service application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
