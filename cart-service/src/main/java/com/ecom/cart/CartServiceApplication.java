package com.ecom.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * CartServiceApplication - Main entry point for Cart Service.
 *
 * This is a Spring Boot microservice for handling shopping cart operations.
 *
 * Features:
 * - Cart management (add, update, remove items)
 * - Redis caching for fast access (TTL: 7 days)
 * - PostgreSQL persistence
 * - Checkout and validation APIs
 * - Microservice integration with Product Service
 *
 * Technologies:
 * - Spring Boot 3.5.14
 * - Spring Data JPA (PostgreSQL)
 * - Spring Data Redis
 * - Spring Web MVC
 * - Jakarta Validation
 *
 * Port: 8002
 * Context Path: /
 *
 * Dependencies:
 * - PostgreSQL database (localhost:5432)
 * - Redis cache (localhost:6379)
 * - Product Service (localhost:8003)
 *
 * Database:
 * - Database: ecom_cart_service
 * - Schema: Auto-created via Hibernate
 *
 * Redis:
 * - Database: 0
 * - TTL: 7 days (604800 seconds) for carts
 */
@Slf4j
@SpringBootApplication
public class CartServiceApplication {

	public static void main(String[] args) {
		log.info("Starting Cart Service application");
		SpringApplication.run(CartServiceApplication.class, args);
		log.info("Cart Service application started successfully");
	}

	/**
	 * Creates a RestTemplate bean for making HTTP calls to other services.
	 *
	 * Used for:
	 * - Calling Product Service to fetch product details
	 * - Validating product availability
	 * - Getting current prices
	 *
	 * In production, consider using Spring Cloud OpenFeign
	 * or a dedicated service client instead of RestTemplate.
	 *
	 * @return configured RestTemplate bean
	 */
	@Bean
	public RestTemplate restTemplate() {
		log.debug("Configuring RestTemplate bean");
		return new RestTemplate();
	}
}
