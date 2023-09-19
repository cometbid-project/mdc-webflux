/**
 * 
 */
package com.ndportmann.mdc_webflux.config;

import java.time.Duration;
import java.util.function.Consumer;

import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.log4j.Log4j2;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.Retry.RetrySignal;

/**
 * @author Gbenga
 *
 */
@Log4j2
public class ApiRetryConfig {

	public static RetryBackoffSpec retryWhenTooManyRequests() { 
		 return Retry.backoff(4, Duration.ofSeconds(2)) 
		 .filter(ApiRetryConfig::isTooManyRequestsException) 
		 .doBeforeRetry(ApiRetryConfig::logRetryAttemptsWithErrorMessage) 
		 .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()); 
	}
	
	private static boolean isTooManyRequestsException(final Throwable throwable) { 
		 return throwable instanceof WebClientResponseException.TooManyRequests; 
	} 
	
	private static Consumer<RetrySignal> logRetryAttemptsWithErrorMessage(Retry.RetrySignal retrySignal) {
		return retrySignals -> {
			log.error("Retrying:" + retrySignal.totalRetries() + ";"
					+ retrySignal.totalRetriesInARow() + ";" 
					+ retrySignal.failure());
		};		
	}
}
