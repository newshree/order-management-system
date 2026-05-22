package com.ecom.api.gateway.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ecom.api.gateway.constant.GatewayConstants;
import com.ecom.api.gateway.service.HealthCheckService;

/**
 * Implementation of {@link HealthCheckService} that checks downstream service health
 * using HTTP requests.
 */
@Service
public class HealthCheckServiceImpl implements HealthCheckService {

	private static final Logger log = LoggerFactory.getLogger(HealthCheckServiceImpl.class);
	private final RestTemplate restTemplate;

	/**
	 * Constructs the health check service implementation.
	 *
	 * @param restTemplate HTTP client used to call downstream service endpoints
	 */
	public HealthCheckServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isServiceHealthy(String serviceName) {
		try {
			String healthUrl = getHealthUrl(serviceName);
			restTemplate.getForObject(healthUrl, String.class);
			return true;
		} catch (RestClientException e) {
			log.warn("Service {} health check failed: {}", serviceName, e.getMessage());
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getServiceStatus(String serviceName) {
		return isServiceHealthy(serviceName) ? "UP" : "DOWN";
	}

	/**
	 * Constructs the health endpoint URL for a given downstream service.
	 *
	 * @param serviceName name of the downstream service
	 * @return full health endpoint URL or empty string when the service is unknown
	 */
	private String getHealthUrl(String serviceName) {
		return switch (serviceName) {
		case "user-service" -> GatewayConstants.USER_SERVICE_URL + "/actuator/health";
		case "cart-service" -> GatewayConstants.CART_SERVICE_URL + "/actuator/health";
		case "product-service" -> GatewayConstants.PRODUCT_SERVICE_URL + "/actuator/health";
		case "inventory-service" -> GatewayConstants.INVENTORY_SERVICE_URL + "/actuator/health";
		case "order-service" -> GatewayConstants.ORDER_SERVICE_URL + "/actuator/health";
		case "payment-service" -> GatewayConstants.PAYMENT_SERVICE_URL + "/actuator/health";
		case "auth-service" -> GatewayConstants.AUTH_SERVICE_URL + "/actuator/health";
		default -> "";
		};
	}
}
