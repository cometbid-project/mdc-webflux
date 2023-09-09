/**
 * 
 */
package com.test.springdata.redis.operations;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.logging.Level;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.test.context.TestPropertySource;

import com.test.springdata.redis.RedisTestConfiguration;
import com.test.springdata.redis.condition.EnabledOnRedisAvailable;
/**
 * @author Gbenga
 *
 */
@Log4j2
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@EnabledOnRedisAvailable
class ListOperationsTests {

	@Autowired ReactiveRedisOperations<String, String> operations;

	@BeforeEach
	void before() {
		StepVerifier.create(operations.execute(it -> it.serverCommands().flushDb())).expectNext("OK").verifyComplete();
	}

	/**
	 * A simple queue using Redis blocking list commands {@code BLPOP} and {@code LPUSH} to produce the queue message.
	 */
	@Test
	void shouldPollAndPopulateQueue() {

		var queue = "foo";

		var listOperations = operations.opsForList();

		var blpop = listOperations //
				.leftPop(queue, Duration.ofSeconds(30)) //
				.log("com.test.springdata.redis", Level.INFO);

		log.info("Blocking pop...waiting for message");
		StepVerifier.create(blpop) //
				.then(() -> {

					Mono.delay(Duration.ofSeconds(10)).doOnSuccess(it -> {

						log.info("Subscriber produces message");

					}).then(listOperations.leftPush(queue, "Hello, World!")).subscribe();

				}).expectNext("Hello, World!").verifyComplete();

		log.info("Blocking pop...done!");
	}
}
