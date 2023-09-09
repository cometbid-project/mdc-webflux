/**
 * 
 */
package com.ndportmann.mdc_webflux.services;

import org.springframework.stereotype.Service;

import com.ndportmann.mdc_webflux.repository.UserRepositoryImpl;
import com.ndportmann.mdc_webflux.service.model.UserData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Service
public class UserServiceImpl {

	private final UserRepositoryImpl userRepository;

	/**
	 * @param userRepository
	 */
	public UserServiceImpl(UserRepositoryImpl userRepository) {
		super();
		this.userRepository = userRepository;
	}

	public Mono<UserData> saveUser(UserData user) {
		return userRepository.insert(user);
	}

	public Mono<UserData> updateUser(UserData user) {
		return userRepository.save(user);
	}

	public Mono<UserData> getUserById(String userId) {
		return userRepository.findById(userId);
	}

	public Flux<UserData> getAllUsers() {
		return userRepository.findAll();
	}

	public Mono<UserData> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Mono<UserData> getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public Mono<Boolean> userExistsById(String userId) {
		return userRepository.existsById(userId);
	}

	public Mono<Boolean> userExistsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	public Mono<Boolean> userExistsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	public Mono<Void> deleteUserById(String userId) {
		return userRepository.deleteById(userId);
	}

	public Mono<Void> deleteUser(UserData user) {
		return userRepository.delete(user);
	}

	public Mono<Void> deleteAllUsers() {
		return userRepository.deleteAll();
	}

	public Mono<Long> userCount() {
		return userRepository.count();
	}

}
