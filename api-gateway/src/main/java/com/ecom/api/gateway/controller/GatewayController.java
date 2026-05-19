package com.ecom.api.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.api.gateway.dto.response.ApiResponse;

/**
 * REST controller exposing basic API Gateway endpoints for health and info.
 */
@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

	/**
	 * Returns a basic health response for the API Gateway.
	 *
	 * @return health status response
	 */
	@GetMapping("/health")
	public ResponseEntity<ApiResponse<String>> health() {
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.success(true)
				.data("API Gateway is running")
				.message("Health check passed")
				.build());
	}

	/**
	 * Returns static gateway information such as version.
	 *
	 * @return gateway information response
	 */
	@GetMapping("/info")
	public ResponseEntity<ApiResponse<String>> info() {
		return ResponseEntity.ok(ApiResponse.<String>builder()
				.success(true)
				.data("API Gateway v1.0.0")
				.message("Gateway information retrieved successfully")
				.build());
	}
}
