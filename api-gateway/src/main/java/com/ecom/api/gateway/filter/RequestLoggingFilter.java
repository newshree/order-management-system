package com.ecom.api.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * Gateway filter that logs incoming requests and outgoing responses.
 */
@Component
public class RequestLoggingFilter extends AbstractGatewayFilterFactory<RequestLoggingFilter.Config> {

	private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

	/**
	 * Constructs the request logging filter factory.
	 */
	public RequestLoggingFilter() {
		super(Config.class);
	}

	/**
	 * Builds a gateway filter that logs request and response details.
	 *
	 * @param config filter configuration
	 * @return logging gateway filter
	 */
	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			String method = exchange.getRequest().getMethod().toString();
			String path = exchange.getRequest().getURI().getPath();

			log.info("Gateway Request: {} {}", method, path);

			return chain.filter(exchange)
					.doFinally(signalType -> {
						int statusCode = exchange.getResponse().getStatusCode() != null ?
							exchange.getResponse().getStatusCode().value() : 0;
						log.info("Gateway Response: {} {} - Status: {}", method, path, statusCode);
					});
		};
	}

	/**
	 * Placeholder filter configuration type required by Spring Cloud Gateway.
	 */
	public static class Config {
	}
}
