/**
 * 
 */
package com.ndportmann.mdc_webflux.repository;

import java.util.Comparator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Circle;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.ndportmann.mdc_webflux.service.model.Person;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Repository
public interface PersonRepository {

	Mono<Long> delete(String id);

	Flux<Person> findByLastname(String lastname);

	Flux<Person> findByFirstnameAndLastname(String firstname, String lastname);

	Flux<Person> findByFirstnameOrLastname(String firstname, String lastname);

	Flux<Person> findByAddress_City(String city);

	Flux<Person> findByAddress_LocationWithin(Circle circle);

	Mono<Page<Person>> findByLastnamePaginated(String lastname, Pageable pageable, Comparator<Person> comparator);

	Mono<Person> findOneByLastname(String lastname);

	Flux<Person> findByFirstname(String firstname);

	Mono<Person> findOneByFirstname(String firstname);

	Mono<Page<Person>> findByFirstnamePaginated(String firstname, Pageable pageable, Comparator<Person> comparator);    
}
