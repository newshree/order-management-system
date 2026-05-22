package com.ecom.api.gateway.filter;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
/**
 * Global filter that adds a correlation ID to each request for tracing purposes.
 *
 * <p>This filter checks for an existing correlation ID in the incoming request headers. If one is
 * not present, it generates a new UUID and adds it to the request headers. This allows for
 * consistent tracking of requests across different services in a distributed system.</p>
 */
@Slf4j
@Component
public class CorrelationIdFilter implements GlobalFilter {

    private static final String CORRELATION_ID = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        log.info("Request received in API Gateway");

        String correlationId =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(CORRELATION_ID);

        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("traceId", correlationId);

        ServerHttpRequest mutatedRequest =
                exchange.getRequest()
                        .mutate()
                        .header(CORRELATION_ID, correlationId)
                        .build();

        return chain.filter(
                exchange.mutate()
                        .request(mutatedRequest)
                        .build()
        ).doFinally(signalType -> MDC.clear());
    }
}