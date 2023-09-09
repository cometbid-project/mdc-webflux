/**
 * 
 */
package com.test.springdata.redis.operations;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.*;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisOperations;

import com.test.springdata.redis.RedisTestConfiguration;
import com.test.springdata.redis.condition.EnabledOnRedisAvailable;

import lombok.extern.log4j.Log4j2;

/**
 * @author Gbenga
 *
 */
@Log4j2
@SpringBootTest(classes = RedisTestConfiguration.class)
@EnabledOnRedisAvailable
class ValueOperationsTests {

	@Autowired ReactiveRedisOperations<String, String> operations;

	@BeforeEach
	void before() {
		StepVerifier.create(operations.execute(it -> it.serverCommands().flushDb())).expectNext("OK").verifyComplete();
	}

	/**
	 * Implement a simple caching sequence using {@code GET} and {@code SETEX} commands.
	 */
	@Test
	void shouldCacheValue() {

		var cacheKey = "foo";

		var valueOperations = operations.opsForValue();

		var cachedMono = valueOperations.get(cacheKey) //
				.switchIfEmpty(cacheValue().flatMap(it -> {

					return valueOperations.set(cacheKey, it, Duration.ofSeconds(60)).then(Mono.just(it));
				}));

		log.info("Initial access (takes a while...)");

		StepVerifier.create(cachedMono).expectSubscription() //
				.expectNoEvent(Duration.ofSeconds(9)) //
				.expectNext("Hello, World!") //
				.verifyComplete();

		log.info("Subsequent access (use cached value)");

		var duration = StepVerifier.create(cachedMono) //
				.expectNext("Hello, World!") //
				.verifyComplete();

		log.info("Done");

		assertThat(duration).isLessThan(Duration.ofSeconds(2));
	}

	/**
	 * @return the cache value that is expensive to calculate.
	 */
	private Mono<String> cacheValue() {
		return Mono.delay(Duration.ofSeconds(10)).then(Mono.just("Hello, World!"));
	}
}
