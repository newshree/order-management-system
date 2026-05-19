package com.ecom.api.gateway.service;

/**
 * Service interface for performing health checks on downstream services.
 */
public interface HealthCheckService {

	/**
	 * Determines whether the specified service is healthy.
	 *
	 * @param serviceName name of the service to check
	 * @return {@code true} when the service responds successfully, otherwise {@code false}
	 */
	boolean isServiceHealthy(String serviceName);

	/**
	 * Returns the health status string for the specified service.
	 *
	 * @param serviceName name of the service to check
	 * @return "UP" when healthy or "DOWN" when unhealthy
	 */
	String getServiceStatus(String serviceName);
}
