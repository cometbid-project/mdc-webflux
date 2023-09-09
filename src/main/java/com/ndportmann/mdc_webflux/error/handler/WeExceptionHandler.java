/**
 * 
 */
package com.ndportmann.mdc_webflux.error.handler;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebExchange;

import com.ndportmann.mdc_webflux.exceptions.ApplicationDefinedRuntimeException;
import com.ndportmann.mdc_webflux.exceptions.InvalidRequestException;
import com.ndportmann.mdc_webflux.exceptions.ResourceNotFoundException;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Log4j2
@ControllerAdvice
public class WeExceptionHandler {

	@ResponseStatus(NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	public @ResponseBody HttpErrorInfo handleNotFoundExceptions(ServerHttpRequest request, Exception ex) {

		return createHttpErrorInfo(NOT_FOUND, request, ex);
	}

	@ResponseStatus(FORBIDDEN)
	@ExceptionHandler(AccessDeniedException.class)
	public @ResponseBody HttpErrorInfo handleAccessDeniedExceptions(ServerHttpRequest request, Exception ex) {

		return createHttpErrorInfo(FORBIDDEN, request, ex);
	}

	@ResponseStatus(UNPROCESSABLE_ENTITY)
	@ExceptionHandler(InvalidRequestException.class)
	public @ResponseBody HttpErrorInfo handleInvalidInputException(ServerHttpRequest request, Exception ex) {

		return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
	}

	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler(ApplicationDefinedRuntimeException.class)
	public @ResponseBody HttpErrorInfo handleRuntimeException(ServerHttpRequest request, Exception ex) {

		return createHttpErrorInfo(INTERNAL_SERVER_ERROR, request, ex);
	}

	@ResponseStatus(GATEWAY_TIMEOUT)
	@ExceptionHandler(TimeoutException.class)
	public @ResponseBody HttpErrorInfo handleTimeoutException(ServerHttpRequest request, Exception ex) {

		return createHttpErrorInfo(GATEWAY_TIMEOUT, request, ex);
	}

	@ResponseStatus(SERVICE_UNAVAILABLE)
	@ExceptionHandler
	public @ResponseBody HttpErrorInfo gottaCatchEmAll(Exception ex, ServerHttpRequest request) {

		return createHttpErrorInfo(SERVICE_UNAVAILABLE, request, ex);
		//return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage()));
	}

	private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, ServerHttpRequest request, Exception ex) {
		final String path = request.getPath().pathWithinApplication().value();
		final String message = ex.getMessage();
		int status = httpStatus.value();

		log.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
		return new HttpErrorInfo(httpStatus, path, message);
	}

}
