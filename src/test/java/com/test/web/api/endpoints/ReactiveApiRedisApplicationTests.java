/**
 * 
 */
package com.test.web.api.endpoints;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.javafaker.Faker;
import com.ndportmann.mdc_webflux.service.model.UserData;
import com.ndportmann.mdc_webflux.services.UserServiceImpl;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

/**
 * @author Gbenga
 *
 */
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {"spring.redis.password="}
)
class ReactiveApiRedisApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private UserServiceImpl userService;
	
	private static Faker faker = Faker.instance();

	private static RedisServer REDISSERVER;
	
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
		userService.deleteAllUsers().block();

		Assertions.assertEquals(0L, userService.userCount().block());
	}

	@Test
	void createUserTest() {
		String firstName = faker.name().firstName();
	    String lastName = faker.name().lastName();
	    String username = faker.name().username();
	    String email = faker.internet().emailAddress();
	    String password = faker.internet().password();
	    
		UserData userA = new UserData(firstName, lastName, username, email, password);

		postAndVerifyUser(userA, HttpStatus.OK)
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.firstName").isEqualTo(firstName)
				.jsonPath("$.lastName").isEqualTo(lastName)
				.jsonPath("$.username").isEqualTo(username)
				.jsonPath("$.email").isEqualTo(email)
				.jsonPath("$.password").isEqualTo(password);

		// Return error for empty username
		UserData userB = new UserData(firstName, lastName, "", email, password);
		postAndVerifyUser(userB, HttpStatus.INTERNAL_SERVER_ERROR)
				.jsonPath("$.path").isEqualTo("/user")
				.jsonPath("$.message").isEqualTo("Cannot be saved: username and email are required, but one or both is empty.");

		// Return error for empty email
		UserData userC = new UserData(firstName, lastName, faker.name().username(), "", password);
		postAndVerifyUser(userB, HttpStatus.INTERNAL_SERVER_ERROR)
				.jsonPath("$.path").isEqualTo("/user")
				.jsonPath("$.message").isEqualTo("Cannot be saved: username and email are required, but one or both is empty.");

		StepVerifier.create(userService.userCount())
				.expectNext(1L)
				.verifyComplete();
		Assertions.assertEquals(1L, userService.userCount().block());
	}

	@Test
	void getUserByIdTest() {
		String firstName = faker.name().firstName();
	    String lastName = faker.name().lastName();
	    String username = faker.name().username();
	    String email = faker.internet().emailAddress();
	    String password = faker.internet().password();
	    
		UserData user = new UserData(firstName, lastName, username, email, password);

		postAndVerifyUser(user, HttpStatus.OK)
				.jsonPath("$.id").isNotEmpty();

		UserData foundUser = userService.getUserByUsername(user.getUsername()).block();
		Assertions.assertNotNull(foundUser);
		Assertions.assertNotNull(foundUser.getId());
		Assertions.assertEquals(user.getFirstName(), foundUser.getFirstName());
		Assertions.assertEquals(user.getLastName(), foundUser.getLastName());
		Assertions.assertEquals(user.getUsername(), foundUser.getUsername());
		Assertions.assertEquals(user.getEmail(), foundUser.getEmail());
		Assertions.assertEquals(user.getPassword(), foundUser.getPassword());

		getAndVerifyUserById(foundUser.getId(), HttpStatus.OK)
				.jsonPath("$.firstName").isEqualTo(user.getFirstName())
				.jsonPath("$.lastName").isEqualTo(user.getLastName())
				.jsonPath("$.username").isEqualTo(user.getUsername())
				.jsonPath("$.email").isEqualTo(user.getEmail())
				.jsonPath("$.password").isEqualTo(user.getPassword());

		StepVerifier.create(userService.userCount())
				.expectNext(1L)
				.verifyComplete();
	}

	@Test
	void getAllUsersTest() {
		UserData user1 = new UserData("firstName1", "lastName1", "username1", "email1@aol.com", "password1");
		UserData user2 = new UserData("firstName2", "lastName2", "username2", "email2@aol.com", "password2");
		UserData user3 = new UserData("firstName3", "lastName3", "username3", "email3@aol.com", "password3");

		postAndVerifyUser(user1, HttpStatus.OK);
		postAndVerifyUser(user2, HttpStatus.OK);
		postAndVerifyUser(user3, HttpStatus.OK);

		getAndVerifyUser("", HttpStatus.OK)
				.jsonPath("$.length()").isEqualTo(3);

		StepVerifier.create(userService.userCount())
				.expectNext(3L)
				.verifyComplete();
	}

	@Test
	void duplicateErrorTest() {
		// Create and save userA, verify it has saved correctly
		UserData userA = new UserData("firstName", "lastName", "username", "email@aol.com", "password");
		postAndVerifyUser(userA, HttpStatus.OK)
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.firstName").isEqualTo("firstName")
				.jsonPath("$.lastName").isEqualTo("lastName")
				.jsonPath("$.username").isEqualTo("username")
				.jsonPath("$.email").isEqualTo("email@aol.com")
				.jsonPath("$.password").isEqualTo("password");

		// Create and try to save userB with the same username and verify it fails
		UserData userB = new UserData("firstName2", "lastName2", "username", "email2@aol.com", "password2");
		postAndVerifyUser(userB, HttpStatus.INTERNAL_SERVER_ERROR)
				.jsonPath("$.path").isEqualTo("/user")
				.jsonPath("$.message").isEqualTo("Duplicate key, Username: " +
									userB.getUsername() + " or Email: " + userB.getEmail() + " exists.");

		// Create and try to save userB with the same email and verify it fails
		userB = new UserData("firstName3", "lastName3", "new_username", "email@aol.com", "password");
		postAndVerifyUser(userB, HttpStatus.INTERNAL_SERVER_ERROR)
				.jsonPath("$.path").isEqualTo("/user")
				.jsonPath("$.message").isEqualTo("Duplicate key, Username: " +
				userB.getUsername() + " or Email: " + userB.getEmail() + " exists.");

		// Create and try to save userB with the same username and email and verify it fails
		userB = new UserData("firstName", "lastName", "username", "email@aol.com", "password");
		postAndVerifyUser(userB, HttpStatus.INTERNAL_SERVER_ERROR)
				.jsonPath("$.path").isEqualTo("/user")
				.jsonPath("$.message").isEqualTo("Duplicate key, Username: " +
				userB.getUsername() + " or Email: " + userB.getEmail() + " exists.");

		// Create and try to save userB with the different username and email and verify it works
		userB = new UserData("firstName", "lastName", "new_username", "new_email@aol.com", "password");
		postAndVerifyUser(userB, HttpStatus.OK)
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.username").isEqualTo("new_username")
				.jsonPath("$.email").isEqualTo("new_email@aol.com")
				.jsonPath("$.name").isEqualTo("name");


		// Try to update userA with the same email of userB and verify it fails
		userA = userService.getUserByUsername(userA.getUsername()).block();
		Assertions.assertNotNull(userA);
		Assertions.assertNotNull(userA.getId());

		userA.setEmail(userB.getEmail());
		updateAndVerifyUser(userA, HttpStatus.INTERNAL_SERVER_ERROR)
				.jsonPath("$.path").isEqualTo("/user/" + userA.getId())
				.jsonPath("$.message").isEqualTo("Duplicate key, Username: " +
				userA.getUsername() + " or Email: " + userA.getEmail() + " exists.");

		// Try to update userA with the same username of userB and verify it fails
		userA.setUsername(userB.getUsername());
		updateAndVerifyUser(userA, HttpStatus.INTERNAL_SERVER_ERROR)
				.jsonPath("$.path").isEqualTo("/user/" + userA.getId())
				.jsonPath("$.message").isEqualTo("Duplicate key, Username: " +
				userA.getUsername() + " or Email: " + userA.getEmail() + " exists.");

		// Try to update userA with the same username and email of userB and verify it fails
		userA.setUsername(userB.getUsername());
		userA.setEmail(userB.getEmail());
		updateAndVerifyUser(userA, HttpStatus.INTERNAL_SERVER_ERROR)
				.jsonPath("$.path").isEqualTo("/user/" + userA.getId())
				.jsonPath("$.message").isEqualTo("Duplicate key, Username: " +
				userA.getUsername() + " or Email: " + userA.getEmail() + " exists.");

		// Try to update userA with the new username and verify it works
		String newUsername = "updated_username";
		String userAEmail = "email@aol.com";
		userA.setUsername(newUsername);
		userA.setEmail(userAEmail);
		updateAndVerifyUser(userA, HttpStatus.OK)
				.jsonPath("$.id").isEqualTo(userA.getId())
				.jsonPath("$.username").isEqualTo(newUsername)
				.jsonPath("$.email").isEqualTo(userAEmail)
				.jsonPath("$.fistName").isEqualTo(userA.getFirstName())
				.jsonPath("$.version").isEqualTo(1);


		//Make sure we have two entities in the database
		StepVerifier.create(userService.userCount())
				.expectNext(2L)
				.verifyComplete();

	}

	@Test
	void optimisticErrorTest() {
		UserData user = new UserData("firstName", "lastName", "username", "email@aol.com", "password");

		postAndVerifyUser(user, HttpStatus.OK)
			.jsonPath("$.firstName").isEqualTo(user.getFirstName())
			.jsonPath("$.lastName").isEqualTo(user.getLastName())
			.jsonPath("$.username").isEqualTo(user.getUsername())
			.jsonPath("$.email").isEqualTo(user.getEmail())
			.jsonPath("$.password").isEqualTo(user.getPassword());

		UserData user1 = userService.getUserByUsername(user.getUsername()).block();
		UserData user2 = userService.getUserByEmail(user.getEmail()).block();

		Assertions.assertNotNull(user1);
		Assertions.assertNotNull(user2);
		Assertions.assertEquals(user1.getId(), user2.getId());
		Assertions.assertEquals(user1.getVersion(), user2.getVersion());

		user1.setFirstName("updated-name");
		user2.setFirstName("updated-name");

		updateAndVerifyUser(user1, HttpStatus.OK)
				.jsonPath("$.username").isEqualTo("username")
				.jsonPath("$.email").isEqualTo("email@aol.com")
				.jsonPath("$.firstName").isEqualTo("updated-name")
				.jsonPath("$.lastName").isEqualTo("lastName")
				.jsonPath("$.version").isEqualTo(1);

		updateAndVerifyUser(user2, HttpStatus.INTERNAL_SERVER_ERROR)
				.jsonPath("$.path").isEqualTo("/user/" + user2.getId())
				.jsonPath("$.message").isEqualTo("This record has already been updated earlier by another object.");


		getAndVerifyUserById(user2.getId(), HttpStatus.OK)
				.jsonPath("$.username").isEqualTo("username")
				.jsonPath("$.email").isEqualTo("email@aol.com")
				.jsonPath("$.firstName").isEqualTo("updated-name")
				.jsonPath("$.lastName").isEqualTo("lastName")
				.jsonPath("$.version").isEqualTo(1);

		StepVerifier.create(userService.userCount())
				.expectNext(1L)
				.verifyComplete();
	}

	@Test
	void updateUserTest() {
		UserData user = new UserData("firstName", "lastName", "username", "email@aol.com", "password");

		postAndVerifyUser(user, HttpStatus.OK)
			.jsonPath("$.firstName").isEqualTo(user.getFirstName())
			.jsonPath("$.lastName").isEqualTo(user.getLastName())
			.jsonPath("$.username").isEqualTo(user.getUsername())
			.jsonPath("$.email").isEqualTo(user.getEmail())
			.jsonPath("$.password").isEqualTo(user.getPassword());

		user = userService.getUserByUsername(user.getUsername()).block();
		Assertions.assertNotNull(user);
		Assertions.assertNotNull(user.getId());

		user.setUsername("updated-username");
		updateAndVerifyUser(user, HttpStatus.OK);

		getAndVerifyUserById(user.getId(), HttpStatus.OK)
				.jsonPath("$.username").isEqualTo("updated-username")
				.jsonPath("$.version").isEqualTo(1);

		StepVerifier.create(userService.userCount())
				.expectNext(1L)
				.verifyComplete();
	}

	@Test
	void deleteUserTest() {
		UserData user1 = new UserData("firstName1", "lastName1", "username1", "email1@aol.com", "password1");
		UserData user2 = new UserData("firstName2", "lastName2", "username2", "email2@aol.com", "password2");
		UserData user3 = new UserData("firstName3", "lastName3", "username3", "email3@aol.com", "password3");

		postAndVerifyUser(user1, HttpStatus.OK);
		postAndVerifyUser(user2, HttpStatus.OK);
		postAndVerifyUser(user3, HttpStatus.OK);

		getAndVerifyUser("", HttpStatus.OK)
				.jsonPath("$.length()").isEqualTo(3);

		StepVerifier.create(userService.userCount())
				.expectNext(3L)
				.verifyComplete();

		user1 = userService.getUserByEmail(user1.getEmail()).block();
		Assertions.assertNotNull(user1);
		Assertions.assertNotNull(user1.getId());

		deleteAndVerifyUserById(user1.getId(), HttpStatus.OK);

		StepVerifier.create(userService.userCount())
				.expectNext(2L)
				.verifyComplete();

		deleteAndVerifyUserById("", HttpStatus.OK);

		StepVerifier.create(userService.userCount())
				.expectNext(0L)
				.verifyComplete();

		deleteAndVerifyUserById("", HttpStatus.OK);
	}

	// Utility Methods

	private WebTestClient.BodyContentSpec getAndVerifyUserById(String userId, HttpStatus expectedStatus) {
		return getAndVerifyUser("/" + userId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyUser(String path, HttpStatus expectedStatus) {
		return client.get()
				.uri("/user" + path)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyUser(UserData user, HttpStatus expectedStatus) {
		return client.post()
				.uri("/user")
				.body(Mono.just(user), UserData.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyUserById(String userId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/user/" + userId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec updateAndVerifyUser(UserData user, HttpStatus expectedStatus) {
		return client.put()
				.uri("/user/" + user.getId())
				.body(Mono.just(user), UserData.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody();
	}

}