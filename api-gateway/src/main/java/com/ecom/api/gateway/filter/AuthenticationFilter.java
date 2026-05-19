package com.ecom.api.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * Gateway filter that validates authentication for protected paths.
 *
 * <p>Requests to public endpoints bypass authentication checks.</p>
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	/**
	 * Constructs the authentication filter factory.
	 */
	public AuthenticationFilter() {
		super(Config.class);
	}

	/**
	 * Builds a gateway filter that rejects requests without an Authorization header
	 * for non-public paths.
	 *
	 * @param config filter configuration
	 * @return authentication gateway filter
	 */
	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			String path = exchange.getRequest().getURI().getPath();

			if (isPublicPath(path)) {
				return chain.filter(exchange);
			}

			String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

			if (authHeader == null || authHeader.isEmpty()) {
				exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}

			return chain.filter(exchange);
		};
	}

	/**
	 * Determines whether the request path is public and does not require auth.
	 *
	 * @param path request URI path
	 * @return {@code true} if path is public, otherwise {@code false}
	 */
	private boolean isPublicPath(String path) {
		return path.startsWith("/api/auth/") ||
				path.startsWith("/api/products/") ||
				path.startsWith("/health");
	}

	/**
	 * Placeholder filter configuration type required by Spring Cloud Gateway.
	 */
	public static class Config {
	}
}
