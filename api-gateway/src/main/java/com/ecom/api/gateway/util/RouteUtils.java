package com.ecom.api.gateway.util;

/**
 * Utility methods for route path parsing and endpoint classification.
 */
public class RouteUtils {

	private RouteUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Extracts the target service name from a request path.
	 *
	 * @param path request URI path
	 * @return resolved service name or {@code "unknown-service"}
	 */
	public static String getServiceName(String path) {
		if (path.startsWith("/api/users/")) {
			return "user-service";
		} else if (path.startsWith("/api/cart/")) {
			return "cart-service";
		} else if (path.startsWith("/api/products/")) {
			return "product-service";
		} else if (path.startsWith("/api/inventory/")) {
			return "inventory-service";
		} else if (path.startsWith("/api/orders/")) {
			return "order-service";
		} else if (path.startsWith("/api/payments/")) {
			return "payment-service";
		} else if (path.startsWith("/api/auth/")) {
			return "auth-service";
		}
		return "unknown-service";
	}
}
