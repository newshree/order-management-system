package com.ecom.api.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.api.gateway.dto.response.ApiResponse;
import com.ecom.api.gateway.service.HealthCheckService;

/**
 * Controller responsible for health-check endpoints across downstream services.
 */
@RestController
@RequestMapping("/api/gateway/health-check")
public class HealthCheckController {

	@Autowired
	private HealthCheckService healthCheckService;

	/**
	 * Checks the health status of a single downstream service.
	 *
	 * @param serviceName name of the service to check
	 * @return health status response for the requested service
	 */
	@GetMapping("/service/{serviceName}")
	public ResponseEntity<ApiResponse<String>> checkServiceHealth(@PathVariable String serviceName) {
		String status = healthCheckService.getServiceStatus(serviceName);
		boolean isHealthy = "UP".equals(status);

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.success(isHealthy)
				.data(status)
				.message("Service " + serviceName + " status: " + status)
				.build());
	}

	/**
	 * Checks the health status of all registered downstream services.
	 *
	 * @return aggregated health status response for all services
	 */
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<String>> checkAllServices() {
		String[] services = {
				"user-service",
				"cart-service",
				"product-service",
				"inventory-service",
				"order-service",
				"payment-service",
				"auth-service"
		};

		StringBuilder status = new StringBuilder();
		boolean allHealthy = true;

		for (String service : services) {
			String serviceStatus = healthCheckService.getServiceStatus(service);
			status.append(service).append(": ").append(serviceStatus).append(" | ");
			if ("DOWN".equals(serviceStatus)) {
				allHealthy = false;
			}
		}

		return ResponseEntity.ok(ApiResponse.<String>builder()
				.success(allHealthy)
				.data(status.toString())
				.message("All services health check completed")
				.build());
	}
}
