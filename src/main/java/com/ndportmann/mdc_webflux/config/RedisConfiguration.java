/**
 * 
 */
package com.ndportmann.mdc_webflux.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

import com.ndportmann.mdc_webflux.service.model.Person;
import com.ndportmann.mdc_webflux.service.model.UserData;

import jakarta.annotation.PreDestroy;

/**
 * @author Gbenga
 *
 */
@Configuration
@EnableRedisWebSession
public class RedisConfiguration {

	@Value("${spring.redis.host}")
	private String redisHost;

	@Value("${spring.redis.port}")
	private int redisPort;

	@Value("${spring.redis.password}")
	private String redisPassword;

	@Autowired
	ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;
	
	/*
	@Bean
	public ReactiveRedisConnectionFactory lettuceConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
		redisStandaloneConfig.setHostName(redisHost);
		redisStandaloneConfig.setPort(redisPort);
		redisStandaloneConfig.setPassword(redisPassword);
		return new LettuceConnectionFactory(redisStandaloneConfig);
	}
	*/	

	@Bean
	public ReactiveRedisOperations<String, UserData> reactiveJsonUserRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {

		RedisSerializationContext<String, UserData> serializationContext = RedisSerializationContext
				.<String, UserData>newSerializationContext(new StringRedisSerializer()).key(new StringRedisSerializer())
				.value(new GenericToStringSerializer<>(UserData.class)).hashKey(new StringRedisSerializer())
				.hashValue(new GenericJackson2JsonRedisSerializer()).build();

		return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
	}
	
	@Bean
	public ReactiveRedisOperations<String, Person> reactiveJsonPersonRedisTemplate(
			ReactiveRedisConnectionFactory connectionFactory) {

		RedisSerializationContext<String, Person> serializationContext = RedisSerializationContext
				.<String, Person>newSerializationContext(new StringRedisSerializer()).key(new StringRedisSerializer())
				.value(new GenericToStringSerializer<>(Person.class)).hashKey(new StringRedisSerializer())
				.hashValue(new GenericJackson2JsonRedisSerializer()).build();

		return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
	}
	
	@Bean
	public ReactiveRedisOperations<String, String> reactiveStringRedisTemplate(
			ReactiveRedisConnectionFactory connectionFactory) {

		var serializer = new Jackson2JsonRedisSerializer<String>(String.class);
		RedisSerializationContextBuilder<String, String> builder = RedisSerializationContext
				.newSerializationContext(new StringRedisSerializer());

		var serializationContext = builder.value(serializer).build();

		return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
	}

	@Bean
	public ReactiveRedisTemplate<String, Object> reactiveJsonObjectRedisTemplate(
			ReactiveRedisConnectionFactory connectionFactory) {

		RedisSerializationContextBuilder<String, Object> builder = RedisSerializationContext
				.newSerializationContext(new StringRedisSerializer());

		var serializationContext = builder.value(new GenericJackson2JsonRedisSerializer("_type")).build();

		return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
	}
	
	/*
	@Bean
    public ReactiveRedisOperations<String, Object> redisOperations(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Object> context = builder.value(serializer).hashValue(serializer)
                .hashKey(serializer).build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }
    */
    

	/**
	 * Clear database before shut down.
	 */
	public @PreDestroy void flushTestDb() {
		//lettuceConnectionFactory().getConnection().flushDb();
		//reactiveRedisConnectionFactory.getReactiveConnection().close();
	}
}
