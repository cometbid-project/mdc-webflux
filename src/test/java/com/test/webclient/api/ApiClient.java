/**
 * 
 */
package com.test.webclient.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.ndportmann.mdc_webflux.MdcWebfluxApplication;
import com.ndportmann.mdc_webflux.client.api.WebClientApi;
import com.ndportmann.mdc_webflux.service.model.Course;

/**
 * @author Gbenga
 *
 */
@SpringBootTest
@ContextConfiguration(classes = MdcWebfluxApplication.class)
public class ApiClient {

	@Autowired
	WebClientApi webClientApi;

	@Test
	public void makeAPostRequest() {
		Course course = Course.builder().name("Angular Basics").category("JavaScript").rating(3)
				.description("Learn Angular Fundamentals").build();

		webClientApi.postNewCourse(course).thenMany(webClientApi.getAllCourses()).subscribe();
	}

}
