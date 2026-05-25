package com.ecom.api.gateway.constant;

/**
 * Constants used by the API Gateway for route configuration, service URLs, timeouts,
 * and header names.
 */
public class GatewayConstants {

	private GatewayConstants() {
		throw new UnsupportedOperationException("Constant class");
	}

	// Service URLs
	public static final String USER_SERVICE_URL = "http://localhost:8001";
	// public static final String CART_SERVICE_URL = "http://localhost:8002"; // Use this for local development
	public static final String CART_SERVICE_URL = "http://cart-service:8002"; // Use service name for Docker networking
	public static final String PRODUCT_SERVICE_URL = "http://localhost:8003";
	public static final String INVENTORY_SERVICE_URL = "http://localhost:8004";
	public static final String ORDER_SERVICE_URL = "http://localhost:8005";
	public static final String PAYMENT_SERVICE_URL = "http://localhost:8006";
	// public static final String AUTH_SERVICE_URL = "http://localhost:8007"; // Use this for local development
	public static final String AUTH_SERVICE_URL = "http://auth-service:8007"; // Use service name for Docker networking

	// Route paths
	public static final String USER_ROUTE = "/api/users/**";
	public static final String CART_ROUTE = "/api/cart/**";
	public static final String PRODUCT_ROUTE = "/api/products/**";
	public static final String INVENTORY_ROUTE = "/api/inventory/**";
	public static final String ORDER_ROUTE = "/api/orders/**";
	public static final String PAYMENT_ROUTE = "/api/payments/**";
	public static final String AUTH_ROUTE = "/api/auth/**";

	// Timeout values (ms)
	public static final int CONNECT_TIMEOUT = 5000;
	public static final int RESPONSE_TIMEOUT = 30000;

	// Headers
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String CONTENT_TYPE_HEADER = "Content-Type";
	public static final String X_REQUEST_ID = "X-Request-Id";
}
