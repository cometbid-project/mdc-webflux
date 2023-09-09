/**
 * 
 */
package com.ndportmann.mdc_webflux.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import com.ndportmann.mdc_webflux.service.model.UserData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author Gbenga
 *
 */
@Repository
public class UserRepositoryImpl extends AbstractRedisBaseRepository<UserData> implements UserRepository {

	private final static String USER_KEY = "USERS";

	private final ReactiveRedisOperations<String, UserData> redisOperations;
	private final ReactiveHashOperations<String, String, UserData> hashOperations;

	public UserRepositoryImpl(ReactiveRedisOperations<String, UserData> redisOperations) {

		super(USER_KEY, UserData.class, redisOperations);
		this.redisOperations = redisOperations;
		this.hashOperations = redisOperations.opsForHash();
	}

	@Override
	public Mono<UserData> save(UserData user) {
		if (user.getUsername().isEmpty() || user.getEmail().isEmpty()) {
			return Mono
					.error(new IllegalArgumentException(
							"Cannot be saved: username and email are required, but one or both is empty."))
					.thenReturn(user);
		}
		
		if (StringUtils.isBlank(user.getId())) {
			String userId = UUID.randomUUID().toString().replaceAll("-", "");
			user.setId(userId);
			user.setVersion(0);

			return Mono.defer(() -> addOrUpdateUser(user,
					existsByUsername(user.getUsername()).mergeWith(existsByEmail(user.getEmail())).any(b -> b)));
		} else {
			return findById(user.getId()).flatMap(u -> {
				if (u.getVersion() != user.getVersion()) {
					return Mono.error(new OptimisticLockingFailureException(
							"This record has already been updated earlier by another object."));
				} else {
					user.setVersion(user.getVersion() + 1);

					return Mono.defer(() -> {
						Mono<Boolean> exists = Mono.just(false);

						if (!u.getUsername().equals(user.getUsername())) {
							exists = existsByUsername(user.getUsername());
						}
						if (!u.getEmail().equals(user.getEmail())) {
							exists = existsByEmail(user.getEmail());
						}

						return addOrUpdateUser(user, exists);
					});
				}
			}).switchIfEmpty(Mono.defer(() -> addOrUpdateUser(user,
					existsByUsername(user.getUsername()).mergeWith(existsByEmail(user.getEmail())).any(b -> b))));
		}
	}

	@Override
	public Mono<UserData> findByUsername(String username) {
		return hashOperations.values(USER_KEY).filter(u -> u.getUsername().equals(username)).singleOrEmpty();
	}

	@Override
	public Mono<UserData> findByEmail(String email) {
		return hashOperations.values(USER_KEY).filter(u -> u.getEmail().equals(email)).singleOrEmpty();
	}

	@Override
	public Mono<Boolean> existsByUsername(String username) {
		return findByUsername(username).hasElement();
	}

	@Override
	public Mono<Boolean> existsByEmail(String email) {
		return findByEmail(email).hasElement();
	}

	// private utility method to add new user if not exist with username and email
	protected Mono<UserData> addOrUpdateUser(UserData user, Mono<Boolean> exists) {
		return exists.flatMap(exist -> {
			if (exist) {
				return Mono.error(new DuplicateKeyException("Duplicate key, Username: " + user.getUsername()
						+ " or Email: " + user.getEmail() + " exists."));
			} else {
				return hashOperations.put(USER_KEY, user.getId(), user).map(isSaved -> user);
			}
		}).thenReturn(user);
	}

}
