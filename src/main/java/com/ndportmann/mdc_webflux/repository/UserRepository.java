/**
 * 
 */
package com.ndportmann.mdc_webflux.repository;

//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.ndportmann.mdc_webflux.service.model.UserData;

import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Repository
public interface UserRepository { //extends ReactiveCrudRepository<UserData, String> {

	Mono<UserData> findByUsername(String username);

	Mono<UserData> findByEmail(String name);

	Mono<Boolean> existsByUsername(String username);

	Mono<Boolean> existsByEmail(String email);
}
