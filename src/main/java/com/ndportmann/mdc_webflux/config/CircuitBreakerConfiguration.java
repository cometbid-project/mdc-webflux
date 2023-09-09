/**
 * 
 */
package com.ndportmann.mdc_webflux.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

/**
 * @author Gbenga
 *
 */
public class CircuitBreakerConfiguration {

	/*
	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {

		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
	}
	*/

	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> customerServiceCusomtizer() {
		
		return factory -> {
			factory.configure(builder -> builder
					.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2)).build())
					.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()), "customer-service");
		};
	}
	
}
