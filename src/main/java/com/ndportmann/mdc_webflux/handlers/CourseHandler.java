/**
 * 
 */
package com.ndportmann.mdc_webflux.handlers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ndportmann.mdc_webflux.repository.CourseRepository;
import com.ndportmann.mdc_webflux.service.model.Course;
import com.ndportmann.mdc_webflux.services.PersonServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Component
//@RequiredArgsConstructor
public class CourseHandler {

	private CourseRepository courseRepository;

	public CourseHandler(@Autowired CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	public Mono<ServerResponse> findAllCourses(ServerRequest serverRequest) {
		Flux<Course> courses = this.courseRepository.findAll();
		return ServerResponse.ok().contentType(APPLICATION_JSON).body(courses, Course.class);
	}

	public Mono<ServerResponse> findCourseById(ServerRequest serverRequest) {
		String courseId = serverRequest.pathVariable("id");
		Mono<Course> courseMono = this.courseRepository.findById(courseId);
		return courseMono.flatMap(course -> ServerResponse.ok().contentType(APPLICATION_JSON).body(fromValue(course)))
				.switchIfEmpty(notFound());
	}

	public Mono<ServerResponse> createCourse(ServerRequest serverRequest) {
		Mono<Course> courseMono = serverRequest.bodyToMono(Course.class);

		return courseMono.flatMap(course -> ServerResponse.status(HttpStatus.CREATED).contentType(APPLICATION_JSON)
				.body(this.courseRepository.save(course), Course.class));
	}

	public Mono<ServerResponse> updateCourse(ServerRequest serverRequest) {
		String courseId = serverRequest.pathVariable("id");
		Mono<Course> existingCourseMono = this.courseRepository.findById(courseId);
		Mono<Course> newCourseMono = serverRequest.bodyToMono(Course.class);
		return newCourseMono
				.zipWith(existingCourseMono,
						(newCourse, existingCourse) -> Course.builder().id(existingCourse.getId())
								.name(newCourse.getName()).category(newCourse.getCategory())
								.rating(newCourse.getRating()).description(newCourse.getDescription()).build())
				.flatMap(course -> ServerResponse.ok().contentType(APPLICATION_JSON)
						.body(this.courseRepository.save(course), Course.class))
				.switchIfEmpty(notFound());
	}

	public Mono<ServerResponse> deleteCourse(ServerRequest serverRequest) {
		String courseId = serverRequest.pathVariable("id");
		return this.courseRepository.findById(courseId)
				.flatMap(existingCourse -> ServerResponse.ok().build(this.courseRepository.deleteById(courseId)))
				.switchIfEmpty(notFound());
	}

	public Mono<ServerResponse> deleteAllCourses(ServerRequest serverRequest) {
		return ServerResponse.ok().build(this.courseRepository.deleteAll());
	}

	private Mono<ServerResponse> notFound() {
		return ServerResponse.notFound().build();
	}

}
