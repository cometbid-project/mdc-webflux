/**
 * 
 */
package com.test.springdata.redis.repositories;

import com.github.javafaker.Faker;
import com.ndportmann.mdc_webflux.MdcWebfluxApplication;
import com.ndportmann.mdc_webflux.config.AppConfig;
import com.ndportmann.mdc_webflux.config.RedisConfiguration;
import com.ndportmann.mdc_webflux.helpers.LocaleContextUtils;
import com.ndportmann.mdc_webflux.repository.PersonRepository;
import com.ndportmann.mdc_webflux.repository.PersonRepositoryImpl;
import com.ndportmann.mdc_webflux.repository.ReactiveRedisComponent;
import com.ndportmann.mdc_webflux.repository.UserRepository;
import com.ndportmann.mdc_webflux.repository.UserRepositoryImpl;
import com.ndportmann.mdc_webflux.service.model.UserData;
import com.test.springdata.redis.RedisTestConfiguration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.test.context.ContextConfiguration;

import reactor.test.StepVerifier;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

/**
 * @author Gbenga
 *
 */
//@Import(RedisTestConfiguration.class)
@DisplayName("User redis repo test")
@ContextConfiguration(classes = { RedisConfiguration.class })
@Import({ UserRepositoryImpl.class, ReactiveRedisComponent.class })
@SpringBootTest(properties = { "spring.redis.password=" })
public class RedisPersistenceTests {

	private static RedisServer REDISSERVER;

	@Autowired
	private UserRepositoryImpl repository;

	// Variable used to create a new entity before each test.
	private UserData savedUser;
	private static Faker faker = Faker.instance();

	@BeforeAll
	static void startUpRedisServer() {
		REDISSERVER = new RedisServerBuilder().port(6379).setting("maxmemory 128M").build();
		REDISSERVER.start();
	}

	@AfterAll
	static void shutDownRedisServer() {
		REDISSERVER.stop();
	}

	@BeforeEach
	void setUpDB() {
		StepVerifier.create(repository.deleteAll()).verifyComplete();

		String firstName = faker.name().firstName();
		String lastName = faker.name().lastName();
		String username = faker.name().username();
		String email = faker.internet().emailAddress();
		String password = faker.internet().password();

		UserData user = new UserData(firstName, lastName, username, email, password);

		// Verify that we can save, store the created user into the savedUser variable
		// and compare the saved user.
		StepVerifier.create(repository.save(user)).expectNextMatches(createdUser -> {
			savedUser = createdUser;
			return assertEqualUser(user, savedUser);
		}).verifyComplete();

		// Verify the number of entities in the database
		StepVerifier.create(repository.count()).expectNext(1L).verifyComplete();
	}

	@Test
	void createTest() {
		String firstName = faker.name().firstName();
		String lastName = faker.name().lastName();
		String username = faker.name().username();
		String email = faker.internet().emailAddress();
		String password = faker.internet().password();

		UserData userA = new UserData(firstName, lastName, username, email, password);

		// Verify that we can save and compare the saved user
		StepVerifier.create(repository.save(userA))
				.expectNextMatches(createdUser -> userA.getId() != null && createdUser.getId().equals(userA.getId()))
				.verifyComplete();

		// Verify we can get back the User by using findById method
		StepVerifier.create(repository.findById(userA.getId()))
				.expectNextMatches(foundUser -> assertEqualUser(userA, foundUser)).verifyComplete();

		// Save without username and verify that it fails
		UserData userB = new UserData(firstName, lastName, "", email, password);
		StepVerifier.create(repository.save(userB)).expectError(IllegalArgumentException.class).verify();

		// Save without email and verify that it fails
		UserData userC = new UserData(firstName, lastName, username, "", password);
		StepVerifier.create(repository.save(userC)).expectError(IllegalArgumentException.class).verify();

		// Verify that the database has only savedUser & userA
		StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();
	}

	@Disabled
	void updateTest() {
		String newName = "name-update";
		savedUser.setFirstName(newName);

		// Verify that we can update and compare the saved user new name
		StepVerifier.create(repository.save(savedUser))
				.expectNextMatches(updatedUser -> updatedUser.getId().equals(savedUser.getId())
						&& updatedUser.getFirstName().equals(newName) && updatedUser.getVersion() == 1)
				.verifyComplete();

		// Verify that we can update and compare the saved user new username
		String newUsername = "username-update";
		savedUser.setUsername(newUsername);

		StepVerifier.create(repository.save(savedUser))
				.expectNextMatches(updatedUser -> updatedUser.getId().equals(savedUser.getId())
						&& updatedUser.getUsername().equals(newUsername) && updatedUser.getVersion() == 2)
				.verifyComplete();

		// Verify that we still have 1 entity in the database
		StepVerifier.create(repository.count()).expectNext(1L).verifyComplete();
	}

	@Disabled
	void deleteTest() {
		// Verify that we can delete the saved user
		StepVerifier.create(repository.delete(savedUser)).verifyComplete();

		// Verify that the saved user has been deleted
		StepVerifier.create(repository.existsById(savedUser.getId())).expectNext(false).verifyComplete();

		// This should also work since delete is an idempotent operation
		StepVerifier.create(repository.deleteById(savedUser.getId())).verifyComplete();

		// Verify that we have no entity in the database
		StepVerifier.create(repository.count()).expectNext(0L).verifyComplete();
	}

	@Disabled
	void getByUsernameAndEmailTest() {
		// Verify that we can get the saved user by username
		StepVerifier.create(repository.findByUsername(savedUser.getUsername()))
				.expectNextMatches(foundUser -> assertEqualUser(savedUser, foundUser)).verifyComplete();

		// Verify that we can get the saved user by email
		StepVerifier.create(repository.findByEmail(savedUser.getEmail()))
				.expectNextMatches(foundUser -> assertEqualUser(savedUser, foundUser)).verifyComplete();

		// Verify that we still have 1 entity in the database
		StepVerifier.create(repository.count()).expectNext(1L).verifyComplete();
	}

	@Disabled
	void duplicateErrorTest() {
		// Same username will fail because username should be unique
		String firstName = faker.name().firstName();
		String lastName = faker.name().lastName();
		String username = faker.name().username();
		String email = faker.internet().emailAddress();
		String password = faker.internet().password();

		UserData userA = new UserData(firstName, lastName, username, email, password);
		// using the same username as savedUser
		UserData userB = new UserData(firstName, lastName, username, faker.internet().emailAddress(), password); 

		// Verify that we have error due to duplicate username
		StepVerifier.create(repository.save(userA).and(repository.save(userB))).expectError(DuplicateKeyException.class)
				.verify();

		// Same email will fail because email should be unique
		// using the same email as savedUser
		UserData userC = new UserData(firstName, lastName, faker.name().username(), email, password); 

		// Verify that we have error due to duplicate email
		StepVerifier.create(repository.save(userA).and(repository.save(userC))).expectError(DuplicateKeyException.class)
				.verify();

		// Add a new user and verify that it saves
		firstName = faker.name().firstName();
		lastName = faker.name().lastName();
		username = faker.name().username();
		email = faker.internet().emailAddress();
		UserData newUser = new UserData(firstName, lastName, username, email, password);

		StepVerifier.create(repository.save(newUser))
				.expectNextMatches(createdUser -> assertEqualUser(newUser, createdUser)).verifyComplete();

		// Verify that we only have 2 entities in the database
		StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();

		// Update savedUser with the above username-2 and verify duplicate error
		savedUser.setUsername("username-2");
		StepVerifier.create(repository.save(savedUser)).expectError(DuplicateKeyException.class).verify();

		// Verify that username and version didn't change
		StepVerifier.create(repository.findById(savedUser.getId()))
				.expectNextMatches(
						foundUser -> foundUser.getUsername().equals("username") && foundUser.getVersion() == 0)
				.verifyComplete();
	}

	@Disabled
	void optimisticLockErrorTest() {

		// Store the saved user in two separate objects
		UserData user1 = repository.findById(savedUser.getId()).block(); // Wait by blocking the thread
		UserData user2 = repository.findById(savedUser.getId()).block(); // Wait by blocking the thread

		Assertions.assertNotNull(user1); // Assert it is not null
		Assertions.assertNotNull(user2); // Assert it is not null
		Assertions.assertEquals(user1.getVersion(), user2.getVersion()); // Assert both version are same
		assertEqualUser(user1, user2);

		String newName1 = "New Name Object1";
		String newName2 = "New Name Object2";

		// Update the user using the first user object. THIS WILL WORK
		user1.setFirstName(newName1);
		StepVerifier.create(repository.save(user1)).expectNextMatches(updatedUser -> updatedUser.getVersion() == 1)
				.verifyComplete();

		// Update the user using the second object.
		// This should FAIL since this second object now holds an old version number,
		// i.e. an Optimistic Lock
		user2.setFirstName(newName2);
		StepVerifier.create(repository.save(user2)).expectError(OptimisticLockingFailureException.class).verify();

		// Get the updated user from the database and verify its new state
		StepVerifier.create(repository.findById(savedUser.getId()))
				.expectNextMatches(
						foundUser -> foundUser.getVersion() == 1 && foundUser.getFirstName().equals(newName1))
				.verifyComplete();

		// Verify we still have one user in the database
		StepVerifier.create(repository.count()).expectNext(1L).verifyComplete();
	}

	// Personal method used in the tests above to compare the User entity.
	private boolean assertEqualUser(UserData expectedUser, UserData actualUser) {
		Assertions.assertEquals(expectedUser.getId(), actualUser.getId());
		Assertions.assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
		Assertions.assertEquals(expectedUser.getLastName(), actualUser.getLastName());
		Assertions.assertEquals(expectedUser.getUsername(), actualUser.getUsername());
		Assertions.assertEquals(expectedUser.getEmail(), actualUser.getEmail());
		Assertions.assertEquals(expectedUser.getPassword(), actualUser.getPassword());

		return (expectedUser.getId().equals(actualUser.getId()))
				&& (expectedUser.getFirstName().equals(actualUser.getFirstName()))
				&& (expectedUser.getLastName().equals(actualUser.getLastName()))
				&& (expectedUser.getUsername().equals(actualUser.getUsername()))
				&& (expectedUser.getEmail().equals(actualUser.getEmail()))
				&& (expectedUser.getPassword().equals(actualUser.getPassword()))
				&& (expectedUser.getVersion() == actualUser.getVersion());
	}
}
