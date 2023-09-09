/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.web.controller;

import org.springframework.web.bind.annotation.*;
import com.ndportmann.mdc_webflux.service.model.UserData;
import com.ndportmann.mdc_webflux.services.UserServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	private final UserServiceImpl userService;

	public UserController(UserServiceImpl userService) {
		this.userService = userService;
	}

	@GetMapping
	public Flux<UserData> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{userId}")
	public Mono<UserData> getUserById(@PathVariable String userId) {
		return userService.getUserById(userId);
	}

	@PostMapping
	public Mono<UserData> createUser(@RequestBody UserData user) {
		return userService.saveUser(user);
	}

	@PutMapping("/{userId}")
	public Mono<UserData> updateUser(@PathVariable String userId, @RequestBody UserData user) {
		if (user.getId() == null || user.getId().isEmpty()) {
			user.setId(userId);
		}
		return userService.updateUser(user);
	}

	@DeleteMapping("/{userId}")
	public Mono<Void> deleteUserById(@PathVariable String userId) {
		return userService.deleteUserById(userId);
	}

	@DeleteMapping
	public Mono<Void> deleteAllUsers() {
		return userService.deleteAllUsers();
	}
}
