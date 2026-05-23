package com.ecom.api.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import com.ecom.api.gateway.exception.ServiceUnavailableException;

/**
 * Gateway filter that maps downstream errors to service unavailability responses.
 */
@Component
public class ErrorHandlingFilter extends AbstractGatewayFilterFactory<ErrorHandlingFilter.Config> {

	/**
	 * Constructs the error handling filter factory.
	 */
	public ErrorHandlingFilter() {
		super(Config.class);
	}

	/**
	 * Builds a gateway filter that converts errors during downstream processing
	 * into {@link ServiceUnavailableException}.
	 *
	 * @param config filter configuration
	 * @return error handling gateway filter
	 */
	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			return chain.filter(exchange)
					.onErrorResume(Throwable.class, e -> {
						if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
							throw new ServiceUnavailableException("Target service is unavailable");
						}
						throw new ServiceUnavailableException("Service temporarily unavailable. Please try again later.");
					});
		};
	}

	/**
	 * Placeholder filter configuration type required by Spring Cloud Gateway.
	 */
	public static class Config {
	}
}
