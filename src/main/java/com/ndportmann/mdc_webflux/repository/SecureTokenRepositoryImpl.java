/**
 * 
 */
package com.ndportmann.mdc_webflux.repository;

import org.springframework.stereotype.Repository;

import com.ndportmann.mdc_webflux.helpers.ObjectMapperUtils;
import com.ndportmann.mdc_webflux.service.model.SecureToken;

import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Repository
public class SecureTokenRepositoryImpl implements SecureTokenRepository {

	private final ReactiveRedisComponent redisComponent;

	/**
	 * @param redisComponent
	 */
	public SecureTokenRepositoryImpl(ReactiveRedisComponent redisComponent) {
		super();
		this.redisComponent = redisComponent;
	}

	@Override
	public Mono<Boolean> saveToken(SecureToken token) {
		// TODO Auto-generated method stub
		return redisComponent.putPojo(token.getToken(), token);
	}

	@Override
	public Mono<SecureToken> findByToken(String token) {
		// TODO Auto-generated method stub
		return redisComponent.getPojo(token)
				.flatMap(d -> Mono.just(ObjectMapperUtils.objectMapper(d, SecureToken.class)));
	}

	@Override
	public Mono<Boolean> removeByToken(String token) {
		// TODO Auto-generated method stub
		return redisComponent.deletePojo(token);
	}

}
