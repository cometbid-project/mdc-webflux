/**
 * 
 */
package com.test.webclient.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.ndportmann.mdc_webflux.MdcWebfluxApplication;
import com.ndportmann.mdc_webflux.controller.RestfulCourseController;
import com.ndportmann.mdc_webflux.repository.CourseRepository;
import com.ndportmann.mdc_webflux.service.model.Course;

/**
 * @author Gbenga
 *
 */
@SpringBootTest
@ContextConfiguration(classes = MdcWebfluxApplication.class)
class CourseTrackerApiApplicationTests {

	private WebTestClient webTestClient;

	@Autowired
	private CourseRepository courseRepository;

	private List<Course> expectedCourses;

	@BeforeEach
	void beforeEach() {
		this.webTestClient = WebTestClient.bindToController(new RestfulCourseController(courseRepository)).configureClient()
				.baseUrl("/courses/").build();
		this.expectedCourses = courseRepository.findAll().collectList().block();
	}

	@Test
	void testGetAllCourses() {
		this.webTestClient.get().exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Course.class).isEqualTo(expectedCourses);
	}

	@Test
	void testInvalidCoursesId() {
		this.webTestClient.get().uri("/123").exchange().expectStatus().isNotFound();
	}

	@Test
	void testValidCoursesId() {
		Course course1 = expectedCourses.get(0);
		this.webTestClient.get().uri("/{id}", course1.getId()).exchange().expectStatus().isOk().expectBody(Course.class)
				.isEqualTo(course1);
	}

}