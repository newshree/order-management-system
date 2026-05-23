package com.ecom.api.gateway.config;

import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Utility provider for {@link RestTemplate} instances used by the API Gateway.
 *
 * <p>This class supplies a buffering {@link RestTemplate} to allow request and
 * response bodies to be read multiple times if needed.</p>
 */
public class RestTemplateConfig {

	/**
	 * Creates a {@link RestTemplate} that uses buffering for HTTP request bodies.
	 *
	 * @return buffered {@link RestTemplate}
	 */
	public static RestTemplate createRestTemplate() {
		ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(
				new SimpleClientHttpRequestFactory());
		return new RestTemplate(factory);
	}
}
