/**
 * 
 */
package com.ndportmann.mdc_webflux.handlers.validator;

import reactor.core.publisher.Mono;

/**
 * 
 * @author Gbenga
 *
 */
interface BaseValidator<T> {
	
	Mono<T> validate(T data);
}
