/**
 * 
 */
package com.test.springdata.redis.operations;

import reactor.test.StepVerifier;

import java.nio.ByteBuffer;

import javax.annotation.PreDestroy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.test.springdata.redis.RedisTestConfiguration;
import com.test.springdata.redis.condition.EnabledOnRedisAvailable;

/**
 * @author Gbenga
 *
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@EnabledOnRedisAvailable
class JacksonJsonTests {

	@Autowired
	ReactiveRedisOperations<String, Person> typedOperations;

	@Autowired
	ReactiveRedisOperations<String, Object> genericOperations;

	
	@TestConfiguration
	public static class autoConfiguration {
		
		@Autowired
		ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

		/**
		 * Configures a {@link ReactiveRedisTemplate} with {@link String} keys and a
		 * typed {@link Jackson2JsonRedisSerializer}.
		 */	
		@Bean
		public ReactiveRedisOperations<String, Person> reactiveJsonPersonRedisTemplate(
				ReactiveRedisConnectionFactory connectionFactory) {

			var serializer = new Jackson2JsonRedisSerializer<Person>(Person.class);
			RedisSerializationContextBuilder<String, Person> builder = RedisSerializationContext
					.newSerializationContext(new StringRedisSerializer());

			var serializationContext = builder.value(serializer).build();

			return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
		}

		@Bean
		public ReactiveRedisOperations<String, Object> reactiveJsonObjectRedisTemplate(
				ReactiveRedisConnectionFactory connectionFactory) {

			RedisSerializationContextBuilder<String, Object> builder = RedisSerializationContext
					.newSerializationContext(new StringRedisSerializer());

			var serializationContext = builder.value(new GenericJackson2JsonRedisSerializer("_type")).build();

			return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
		}
		
		/**
		 * Clear database before shut down.
		 */
		public @PreDestroy void flushTestDb() {
			reactiveRedisConnectionFactory.getReactiveConnection().close();
		}
	}

	/**
	 * {@link ReactiveRedisOperations} using {@link String} keys and {@link Person}
	 * values serialized via
	 * {@link org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer}
	 * to JSON without additional type hints.
	 *
	 * @see RedisTestConfiguration#reactiveJsonPersonRedisTemplate(ReactiveRedisConnectionFactory)
	 */
	@Test
	void shouldWriteAndReadPerson() {

		StepVerifier.create(typedOperations.opsForValue().set("homer", new Person("Homer", "Simpson"))) //
				.expectNext(true) //
				.verifyComplete();

		var get = typedOperations.execute(conn -> conn.stringCommands().get(ByteBuffer.wrap("homer".getBytes()))) //
				.map(ByteUtils::getBytes) //
				.map(String::new);

		get.as(StepVerifier::create) //
				.expectNext("{\"firstname\":\"Homer\",\"lastname\":\"Simpson\"}") //
				.verifyComplete();

		typedOperations.opsForValue().get("homer").as(StepVerifier::create) //
				.expectNext(new Person("Homer", "Simpson")) //
				.verifyComplete();
	}

	/**
	 * {@link ReactiveRedisOperations} using {@link String} keys and {@link Object}
	 * values serialized via
	 * {@link org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer}
	 * to JSON with additional type hints. This example uses the non-final type
	 * {@link Person} using its FQCN as type identifier.
	 *
	 * @see RedisTestConfiguration#reactiveJsonObjectRedisTemplate(ReactiveRedisConnectionFactory)
	 */
	@Test
	void shouldWriteAndReadPersonObject() {

		genericOperations.opsForValue().set("homer", new Person("Homer", "Simpson")) //
				.as(StepVerifier::create) //
				.expectNext(true) //
				.verifyComplete();

		var get = genericOperations.execute(conn -> conn.stringCommands().get(ByteBuffer.wrap("homer".getBytes()))) //
				.map(ByteUtils::getBytes) //
				.map(String::new);

		get.as(StepVerifier::create) //
				.expectNext(
						"{\"_type\":\"com.test.springdata.redis.operations.Person\",\"firstname\":\"Homer\",\"lastname\":\"Simpson\"}") //
				.verifyComplete();

		genericOperations.opsForValue().get("homer").as(StepVerifier::create) //
				.expectNext(new Person("Homer", "Simpson")) //
				.verifyComplete();
	}

	/**
	 * {@link ReactiveRedisOperations} using {@link String} keys and {@link Object}
	 * values serialized via
	 * {@link org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer}
	 * to JSON with additional type hints. This example uses the final type
	 * {@link example.springdata.redis.EmailAddress} using configuration from
	 * {@link com.fasterxml.jackson.annotation.JsonTypeInfo} as type identifier.
	 *
	 * @see RedisTestConfiguration#reactiveJsonObjectRedisTemplate(ReactiveRedisConnectionFactory)
	 */
	@Test
	void shouldWriteAndReadEmailObject() {

		genericOperations.opsForValue().set("mail", new EmailAddress("homer@the-simpsons.com")) //
				.as(StepVerifier::create) //
				.expectNext(true) //
				.verifyComplete();

		var get = genericOperations.execute(conn -> conn.stringCommands().get(ByteBuffer.wrap("mail".getBytes()))) //
				.map(ByteUtils::getBytes) //
				.map(String::new);

		get.as(StepVerifier::create) //
				.expectNext(
						"{\"_type\":\"com.test.springdata.redis.operations.EmailAddress\",\"address\":\"homer@the-simpsons.com\"}") //
				.verifyComplete();

		genericOperations.opsForValue().get("mail") //
				.as(StepVerifier::create) //
				.expectNext(new EmailAddress("homer@the-simpsons.com")) //
				.verifyComplete();
	}
}
