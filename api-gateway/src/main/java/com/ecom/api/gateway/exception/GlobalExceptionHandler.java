package com.ecom.api.gateway.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import com.ecom.api.gateway.dto.response.ErrorResponse;
import com.ecom.api.gateway.enums.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GatewayException.class)
    public ResponseEntity<ErrorResponse> handleGatewayException(
            GatewayException ex,
            ServerWebExchange exchange) {

        String path =
                exchange.getRequest()
                        .getURI()
                        .getPath();

        log.warn(
                "Gateway exception. Path: {}, Message: {}",
                path,
                ex.getMessage()
        );

        ErrorCode errorCode = ex.getErrorCode();

        HttpStatus status =
                mapErrorCodeToHttpStatus(errorCode);

        ErrorResponse error =
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .code(errorCode)
                        .message(ex.getMessage())
                        .path(path)
                        .build();

        return ResponseEntity
                .status(status)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            ServerWebExchange exchange) {

        String path =
                exchange.getRequest()
                        .getURI()
                        .getPath();

        log.error(
                "Unhandled exception occurred for path: {}",
                path,
                ex
        );

        ErrorResponse error =
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error("INTERNAL_SERVER_ERROR")
                        .code(ErrorCode.INTERNAL_SERVER_ERROR)
                        .message(
                                "An unexpected error occurred. Please try again later."
                        )
                        .path(path)
                        .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

	/**
	 * Handles BadRequestException exceptions.
	 *
	 * Dynamically determines HTTP status based on the error code (400, 401, or 403).
	 *
	 * @param ex the caught exception
	 * @param request the HTTP request
	 * @return a ResponseEntity with appropriate status and error details
	 */
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(
			BadRequestException ex,
			ServerWebExchange exchange) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		if (ex.getErrorCode() == ErrorCode.UNAUTHORIZED || ex.getErrorCode() == ErrorCode.FORBIDDEN) {
			status = ex.getErrorCode() == ErrorCode.UNAUTHORIZED ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
		}

		String path =
			exchange.getRequest()
					.getURI()
					.getPath();

		log.warn(
				"Client error occurred. Path: {}, Status: {}, Message: {}",
				path,
				status.value(),
				ex.getMessage()
		);

		ErrorResponse error = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.code(ex.getErrorCode())
				.message(ex.getMessage())
				.path(path)
				.build();

		return ResponseEntity.status(status).body(error);
	}

    private HttpStatus mapErrorCodeToHttpStatus(
            ErrorCode errorCode) {

        return switch (errorCode) {
            case INVALID_REQUEST -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case GATEWAY_TIMEOUT -> HttpStatus.GATEWAY_TIMEOUT;
            case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case BAD_GATEWAY -> HttpStatus.BAD_GATEWAY;
            case RATE_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}