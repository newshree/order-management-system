package com.ecom.api.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Entry point for the API Gateway Spring Boot application.
 *
 * <p>This class bootstraps the Spring context and exposes a shared {@link RestTemplate}
 * bean that can be used by services throughout the gateway.</p>
 */
@SpringBootApplication
public class ApiGatewayApplication {

	/**
	 * Main method that starts the Spring Boot application.
	 *
	 * @param args application arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	/**
	 * Creates a {@link RestTemplate} bean for outbound HTTP calls.
	 *
	 * @return configured {@link RestTemplate}
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
