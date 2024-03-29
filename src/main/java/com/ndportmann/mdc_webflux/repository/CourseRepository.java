/**
 * 
 */
package com.ndportmann.mdc_webflux.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.ndportmann.mdc_webflux.service.model.Course;

import reactor.core.publisher.Flux;

/**
 * @author Gbenga
 *
 */
@Repository
public interface CourseRepository extends ReactiveMongoRepository<Course, String> {
	
	Flux<Course> findAllByCategory(String category);
}
