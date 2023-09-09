/**
 * 
 */
package com.ndportmann.mdc_webflux.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ndportmann.mdc_webflux.repository.CourseRepository;
import com.ndportmann.mdc_webflux.service.model.Course;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Log4j2
@RestController
@RequestMapping("/non-func/courses/")
public class RestfulCourseController {

	private CourseRepository courseRepository;

	public RestfulCourseController(@Autowired CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	@GetMapping
	public Flux<Course> getAllCourses() {
		return courseRepository.findAll();
	}

	@GetMapping("{id}")
	public Mono<ResponseEntity<Course>> getCourseById(@PathVariable("id") String courseId) {
		return courseRepository.findById(courseId).map(course -> ResponseEntity.ok(course))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@GetMapping("category/{name}")
	public Flux<Course> getCourseByCategory(@PathVariable("name") String category) {
		return courseRepository.findAllByCategory(category)
				.doOnError(e -> log.error("Failed to create course", e.getMessage()));
	}

	@PostMapping
	public Mono<Course> createCourse(@RequestBody Course course) {
		return courseRepository.save(course)
				.doOnSuccess(updatedCourse -> log.info("Successfully created course", updatedCourse))
				.doOnError(e -> log.error("Failed to create course", e.getMessage()));
	}

	@PutMapping("{id}")
	public Mono<ResponseEntity<Course>> updateCourse(@PathVariable("id") String courseId, @RequestBody Course course) {

		return this.courseRepository.findById(courseId).flatMap(existingCourse -> {
			existingCourse.setName(course.getName());
			existingCourse.setRating(course.getRating());
			existingCourse.setCategory(course.getCategory());
			existingCourse.setDescription(course.getDescription());
			return this.courseRepository.save(existingCourse);
		}).map(updatedCourse -> ResponseEntity.ok(updatedCourse)).defaultIfEmpty(ResponseEntity.notFound().build())
				.doOnError(e -> log.error("Failed to update course", e.getMessage()));

	}

	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Course>> deleteCourseById(@PathVariable("id") String courseId) {
		return this.courseRepository.findById(courseId).flatMap(
				course -> this.courseRepository.deleteById(course.getId()).then(Mono.just(ResponseEntity.ok(course))))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping
	public Mono<Void> deleteCourses() {
		return courseRepository.deleteAll();
	}

}
