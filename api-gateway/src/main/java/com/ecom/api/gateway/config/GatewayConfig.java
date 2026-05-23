package com.ecom.api.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecom.api.gateway.filter.ErrorHandlingFilter;
import com.ecom.api.gateway.filter.JwtAuthenticationFilter;
import com.ecom.api.gateway.filter.RequestLoggingFilter;

/**
 * Spring Cloud Gateway configuration defining route mappings for downstream services.
 */
@Configuration
public class GatewayConfig {

	private final JwtAuthenticationFilter authenticationFilter;
    private final RequestLoggingFilter requestLoggingFilter;
    private final ErrorHandlingFilter errorHandlingFilter;

    public GatewayConfig(
            JwtAuthenticationFilter authenticationFilter,
            RequestLoggingFilter requestLoggingFilter,
            ErrorHandlingFilter errorHandlingFilter) {

        this.authenticationFilter = authenticationFilter;
        this.requestLoggingFilter = requestLoggingFilter;
        this.errorHandlingFilter = errorHandlingFilter;
    }

	/**
	 * Builds the route locator for the configured downstream services.
	 *
	 * @param builder route locator builder provided by Spring Cloud Gateway
	 * @return configured {@link RouteLocator}
	 */
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("user-service",
					r -> r.path("/api/users/**")
							.filters(f -> f
									.filter(requestLoggingFilter.apply(
											new RequestLoggingFilter.Config()))
									.filter(authenticationFilter.apply(
											new JwtAuthenticationFilter.Config()))
									.filter(errorHandlingFilter.apply(
											new ErrorHandlingFilter.Config()))
							)
							.uri("http://localhost:8001"))
				.route("cart-service",
					r -> r.path("/api/cart/**")
							.filters(f -> f
									.filter(requestLoggingFilter.apply(
											new RequestLoggingFilter.Config()))
									.filter(authenticationFilter.apply(
											new JwtAuthenticationFilter.Config()))
									.filter(errorHandlingFilter.apply(
											new ErrorHandlingFilter.Config()))
							)
							.uri("http://localhost:8002"))
				.route("product-service",
					r -> r.path("/api/products/**")
							.filters(f -> f
									.filter(requestLoggingFilter.apply(
											new RequestLoggingFilter.Config()))
									.filter(authenticationFilter.apply(
											new JwtAuthenticationFilter.Config()))
									.filter(errorHandlingFilter.apply(
											new ErrorHandlingFilter.Config()))
							)
							.uri("http://localhost:8003"))
				.route("inventory-service",
					r -> r.path("/api/inventory/**")
							.filters(f -> f
									.filter(requestLoggingFilter.apply(
											new RequestLoggingFilter.Config()))
									.filter(authenticationFilter.apply(
											new JwtAuthenticationFilter.Config()))
									.filter(errorHandlingFilter.apply(
											new ErrorHandlingFilter.Config()))
							)
							.uri("http://localhost:8004"))
				.route("order-service",
					r -> r.path("/api/orders/**")
							.filters(f -> f
									.filter(requestLoggingFilter.apply(
											new RequestLoggingFilter.Config()))
									.filter(authenticationFilter.apply(
											new JwtAuthenticationFilter.Config()))
									.filter(errorHandlingFilter.apply(
											new ErrorHandlingFilter.Config()))
							)
							.uri("http://localhost:8005"))
				.route("payment-service",
					r -> r.path("/api/payments/**")
							.filters(f -> f
									.filter(requestLoggingFilter.apply(
											new RequestLoggingFilter.Config()))
									.filter(authenticationFilter.apply(
											new JwtAuthenticationFilter.Config()))
									.filter(errorHandlingFilter.apply(
											new ErrorHandlingFilter.Config()))
							)
							.uri("http://localhost:8006"))
				.route("auth-service",
					r -> r.path("/api/auth/**")
							.filters(f -> f
									.filter(requestLoggingFilter.apply(
											new RequestLoggingFilter.Config()))
									.filter(errorHandlingFilter.apply(
											new ErrorHandlingFilter.Config()))
							)
							.uri("http://localhost:8007"))
				.build();
	}
}
