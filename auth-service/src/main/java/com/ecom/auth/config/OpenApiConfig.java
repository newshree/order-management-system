package com.ecom.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * OpenAPI/Swagger configuration for API documentation.
 *
 * Customizes the Swagger UI appearance and adds security scheme for JWT authentication.
 */
@Configuration
public class OpenApiConfig {

	/**
	 * Configures the OpenAPI specification with custom information and security details.
	 *
	 * @return configured OpenAPI bean
	 */
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Auth Service API")
						.version("1.0.0")
						.description("Comprehensive authentication and authorization service for e-commerce platform. Provides user registration, login, JWT token management, and token validation.")
						.contact(new Contact()
								.name("Development Team")
								.email("dev@ecom.com")
								.url("https://ecom.com"))
						.license(new License()
								.name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0.html")))
				.addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
				.components(new io.swagger.v3.oas.models.Components()
						.addSecuritySchemes("bearer-jwt",
								new SecurityScheme()
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
										.description("JWT Token for API authentication")));
	}
}
