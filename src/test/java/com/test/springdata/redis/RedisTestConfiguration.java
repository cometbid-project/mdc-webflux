/**
 * 
 */
package com.test.springdata.redis;

/**
 * @author Gbenga
 *
 */
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.ndportmann.mdc_webflux.repository.PersonRepository;
import com.ndportmann.mdc_webflux.repository.PersonRepositoryImpl;
import com.ndportmann.mdc_webflux.repository.ReactiveRedisComponent;
import com.ndportmann.mdc_webflux.service.model.Person;

/**
 * @author Mark Paluch
 */
@SpringBootApplication
@TestConfiguration
public class RedisTestConfiguration {

	/*
	 * @Bean public LettuceConnectionFactory redisConnectionFactory() { return new
	 * LettuceConnectionFactory(); }
	 */

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
		
		/*
		RedisSerializationContext<String, Person> serializationContext = RedisSerializationContext
                .<String, Person>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new GenericToStringSerializer<>(Person.class))
                .hashKey(new StringRedisSerializer())
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();
        */
		return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
	}

	/**
	 * Configures a {@link ReactiveRedisTemplate} with {@link String} keys and
	 * {@link GenericJackson2JsonRedisSerializer}.
	 */
	@Bean
	public ReactiveRedisTemplate<String, Object> reactiveJsonObjectRedisTemplate(
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