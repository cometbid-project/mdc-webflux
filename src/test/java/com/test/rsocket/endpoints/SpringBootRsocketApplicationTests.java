/**
 * 
 */
package com.test.rsocket.endpoints;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;

import reactor.core.publisher.Flux;

import com.ndportmann.mdc_webflux.MdcWebfluxApplication;
import com.ndportmann.mdc_webflux.service.model.Course;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author Gbenga
 *
 */

@SpringBootTest
@ContextConfiguration(classes = MdcWebfluxApplication.class)
class SpringBootRsocketApplicationTests {

	private static RSocketRequester requester;

	@BeforeAll
	public static void setUpOnce(@Autowired RSocketRequester.Builder builder, @LocalRSocketServerPort Integer port,
			@Autowired RSocketStrategies rSocketStrategies) {

		requester = builder.tcp("localhost", port);
	}

	@Test
	public void testRequestResponse() {
		// Send a request
		Mono<Course> courseMono = requester.route("request-response").data(new Course("Spring"))
				.retrieveMono(Course.class);

		// Verify the response
		StepVerifier.create(courseMono)
				.consumeNextWith(course -> assertThat(course.getCourseName()).isEqualTo("Your course name: Spring"))
				.verifyComplete();
	}

	@Test
	public void testFireAndForget() {
		// Send a request
		Mono<Course> courseMono = requester.route("fire-and-forget").data(new Course("Spring"))
				.retrieveMono(Course.class);

		// Verify the response
		StepVerifier.create(courseMono).verifyComplete();
	}

	@Test
	public void testRequestStream() {
		// Send a request
		Flux<Course> courseFlux = requester.route("request-stream").data(new Course("Spring"))
				.retrieveFlux(Course.class);

		StepVerifier.create(courseFlux)
				.consumeNextWith(
						course -> assertThat(course.getCourseName()).isEqualTo("Your course name: Spring. Response #0"))
				.expectNextCount(0)
				.consumeNextWith(
						course -> assertThat(course.getCourseName()).isEqualTo("Your course name: Spring. Response #1"))
				.thenCancel().verify();
	}

	@Test
	public void testChannel() {
		// Create first setting after 0 seconds. Server starts sending after 2 seconds.
		Mono<Integer> setting1 = Mono.just(Integer.valueOf(2)).delayElement(Duration.ofSeconds(0));
		// Create next setting after 3 secconds. Server starts sending in after 1
		// second.
		Mono<Integer> setting2 = Mono.just(Integer.valueOf(1)).delayElement(Duration.ofSeconds(3));

		// Bundle settings into a Flux
		Flux<Integer> settings = Flux.concat(setting1, setting2);

		// Send a stream of request messages
		Flux<Course> stream = requester.route("stream-stream").data(settings).retrieveFlux(Course.class);

		// Verify that the response messages contain the expected data
		StepVerifier.create(stream)
				.consumeNextWith(course -> assertThat(course.getCourseName()).isEqualTo("Spring. Response #0"))
				.consumeNextWith(course -> assertThat(course.getCourseName()).isEqualTo("Spring. Response #0"))
				.thenCancel().verify();
	}
}
