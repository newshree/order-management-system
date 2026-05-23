package com.ecom.api.gateway.service.impl;

import org.springframework.stereotype.Service;

import com.ecom.api.gateway.constant.GatewayConstants;
import com.ecom.api.gateway.service.RouteService;
import com.ecom.api.gateway.util.RouteUtils;

/**
 * Implementation of {@link RouteService} responsible for route validation and
 * service URL resolution.
 */
@Service
public class RouteServiceImpl implements RouteService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getServiceUrl(String serviceName) {
		return switch (serviceName) {
		case "user-service" -> GatewayConstants.USER_SERVICE_URL;
		case "cart-service" -> GatewayConstants.CART_SERVICE_URL;
		case "product-service" -> GatewayConstants.PRODUCT_SERVICE_URL;
		case "inventory-service" -> GatewayConstants.INVENTORY_SERVICE_URL;
		case "order-service" -> GatewayConstants.ORDER_SERVICE_URL;
		case "payment-service" -> GatewayConstants.PAYMENT_SERVICE_URL;
		case "auth-service" -> GatewayConstants.AUTH_SERVICE_URL;
		default -> "";
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validateRoute(String path) {
		return path.startsWith("/api/users/") ||
				path.startsWith("/api/cart/") ||
				path.startsWith("/api/products/") ||
				path.startsWith("/api/inventory/") ||
				path.startsWith("/api/orders/") ||
				path.startsWith("/api/payments/") ||
				path.startsWith("/api/auth/") ||
				path.startsWith("/api/gateway/");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String extractServiceName(String path) {
		return RouteUtils.getServiceName(path);
	}
}
