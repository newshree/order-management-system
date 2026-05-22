package com.ecom.api.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.ecom.api.gateway.enums.ErrorCode;
import com.ecom.api.gateway.exception.BadRequestException;
import com.ecom.api.gateway.util.JwtUtil;

import reactor.core.publisher.Mono;

/**
 * Gateway filter that validates authentication for protected paths.
 *
 * <p>Requests to public endpoints bypass authentication checks.</p>
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	private final JwtUtil jwtUtil;

	/**
	 * Constructs the authentication filter factory.
	 */
	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		super(Config.class);
		this.jwtUtil = jwtUtil;
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
		return new GatewayFilter() {
                    @Override
                    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                        String path = exchange.getRequest()
                                .getURI()
                                .getPath();
                        
                        // Skip public APIs that do not require authentication
                        if (isPublicPath(path)) {
                            return chain.filter(exchange);
                        }
                        
                        // Get Authorization Header
                        String authHeader = exchange.getRequest()
                                .getHeaders()
                                .getFirst("Authorization");
                        
                        // Validate Header
                        if (authHeader == null ||
                                !authHeader.startsWith("Bearer ")) {
                            
                            throw new BadRequestException(
                                    ErrorCode.UNAUTHORIZED,
                                    "Missing or invalid Authorization header"
                            );
                        }
                        
                        // Extract Token
                        String token = authHeader.substring(7);
                        
                        // JwtUtil itself handles exceptions
                        jwtUtil.validateToken(token);
                        
						// Extract Claims
						String email = jwtUtil.extractEmail(token);
						String userId = jwtUtil.extractUserId(token);
						String role = jwtUtil.extractRole(token);

						// Add headers to downstream services
						ServerHttpRequest mutatedRequest =
								exchange.getRequest()
										.mutate()
										.header("X-User-Email", email)
										.header("X-User-Id", userId)
										.header("X-User-Role", role)
										.build();
                        
                        return chain.filter(
                                exchange.mutate()
                                        .request(mutatedRequest)
                                        .build()
                        );      }
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
				// path.startsWith("/api/products/") ||
				path.startsWith("/health") ||
				path.contains("/swagger-ui") ||
            	path.contains("/v3/api-docs");
	}

	/**
	 * Placeholder filter configuration type required by Spring Cloud Gateway.
	 */
	public static class Config {
	}
}
