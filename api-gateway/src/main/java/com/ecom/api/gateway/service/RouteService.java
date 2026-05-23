package com.ecom.api.gateway.service;

/**
 * Service interface for route resolution and validation.
 */
public interface RouteService {

	/**
	 * Returns the target service URL for the given service name.
	 *
	 * @param serviceName downstream service identifier
	 * @return service URL or empty string when unknown
	 */
	String getServiceUrl(String serviceName);

	/**
	 * Validates whether the provided request path matches a known route.
	 *
	 * @param path request URI path
	 * @return {@code true} when the path is valid, otherwise {@code false}
	 */
	boolean validateRoute(String path);

	/**
	 * Extracts the logical service name from the request path.
	 *
	 * @param path request URI path
	 * @return matching service name or {@code "unknown-service"}
	 */
	String extractServiceName(String path);
}
