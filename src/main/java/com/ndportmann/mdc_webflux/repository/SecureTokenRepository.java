/**
 * 
 */
package com.ndportmann.mdc_webflux.repository;

import com.ndportmann.mdc_webflux.service.model.SecureToken;

import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
public interface SecureTokenRepository {

	Mono<SecureToken> findByToken(final String token);

	Mono<Boolean> removeByToken(String token); 

	Mono<Boolean> saveToken(SecureToken token); 
}
