/**
 * 
 */
package com.ndportmann.mdc_webflux.repository;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.repository.query.FluentQuery.ReactiveFluentQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.ndportmann.mdc_webflux.service.model.Book;
import com.ndportmann.mdc_webflux.service.model.Person;
import com.ndportmann.mdc_webflux.service.model.UserData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Repository
public class PersonRepositoryImpl extends AbstractRedisBaseRepository<Person> implements PersonRepository {

	public static final String PERSON_KEY = "PERSON";
	// private final ReactiveRedisComponent reactiveRedisComponent;

	// private final ReactiveRedisOperations<String, Person> redisOperations;
	private final ReactiveHashOperations<String, String, Person> hashOperations;

	public PersonRepositoryImpl(ReactiveRedisOperations<String, Person> redisOperations) {

		super(PERSON_KEY, Person.class, redisOperations);
		// this.reactiveRedisComponent = reactiveRedisComponent;

		// this.redisOperations = redisOperations;
		this.hashOperations = redisOperations.opsForHash();
	}

	@Override
	public Mono<Long> delete(String id) {
		Assert.isTrue(StringUtils.isNotBlank(id), "The given Id must not be empty!");

		return hashOperations.remove(PERSON_KEY, id);
	}

	@Override
	public Flux<Person> findByLastname(String lastname) {
		Assert.isTrue(StringUtils.isNotBlank(lastname), "The lastname must not be empty!");

		return hashOperations.values(PERSON_KEY).filter(u -> StringUtils.containsIgnoreCase(u.getLastname(), lastname));
	}

	@Override
	public Mono<Person> findOneByLastname(String lastname) {
		Assert.isTrue(StringUtils.isNotBlank(lastname), "The lastname must not be empty!");

		return hashOperations.values(PERSON_KEY).filter(u -> u.getLastname().equalsIgnoreCase(lastname))
				.singleOrEmpty();
	}

	@Override
	public Mono<Page<Person>> findByLastnamePaginated(String lastname, Pageable pageable,
			Comparator<Person> comparator) {

		Assert.notNull(pageable, "Pageable must not be null!");
		Assert.isTrue(StringUtils.isNotBlank(lastname), "The lastname must not be empty!");
		Assert.notNull(comparator, "Specify a java.util.Comparator for sorting!");

		Mono<List<Person>> items = findAll().collectSortedList(comparator);

		return items.flatMap(content -> this.getPage(content, pageable, this.count()));
	}

	@Override
	public Flux<Person> findByFirstname(String firstname) {
		Assert.isTrue(StringUtils.isNotBlank(firstname), "The firstname must not be empty!");

		return hashOperations.values(PERSON_KEY)
				.filter(u -> StringUtils.containsIgnoreCase(u.getFirstname(), firstname));
	}

	@Override
	public Mono<Person> findOneByFirstname(String firstname) {
		Assert.isTrue(StringUtils.isNotBlank(firstname), "The firstname must not be empty!");

		return hashOperations.values(PERSON_KEY).filter(u -> u.getFirstname().equalsIgnoreCase(firstname))
				.singleOrEmpty();
	}

	@Override
	public Mono<Page<Person>> findByFirstnamePaginated(String firstname, Pageable pageable,
			Comparator<Person> comparator) {

		Assert.notNull(pageable, "Pageable must not be null!");
		Assert.isTrue(StringUtils.isNotBlank(firstname), "The firstname must not be empty!");
		Assert.notNull(comparator, "Specify a java.util.Comparator for sorting!");

		Mono<List<Person>> items = findAll().collectSortedList(comparator);

		return items.flatMap(content -> this.getPage(content, pageable, this.count()));
	}

	@Override
	public Flux<Person> findByFirstnameAndLastname(String firstname, String lastname) {
		Assert.isTrue(StringUtils.isNotBlank(firstname), "The firstname must not be empty!");
		Assert.isTrue(StringUtils.isNotBlank(lastname), "The lastname must not be empty!");

		Predicate<? super Person> p = u -> StringUtils.containsIgnoreCase(u.getLastname(), lastname)
				&& StringUtils.containsIgnoreCase(u.getFirstname(), firstname);

		return hashOperations.values(PERSON_KEY).filter(p);
	}

	@Override
	public Flux<Person> findByFirstnameOrLastname(String firstname, String lastname) {
		Assert.isTrue(StringUtils.isNotBlank(firstname), "The firstname must not be empty!");
		Assert.isTrue(StringUtils.isNotBlank(lastname), "The lastname must not be empty!");

		Predicate<? super Person> p = u -> StringUtils.containsIgnoreCase(u.getLastname(), lastname)
				|| StringUtils.containsIgnoreCase(u.getFirstname(), firstname);

		return hashOperations.values(PERSON_KEY).filter(p);
	}

	@Override
	public Flux<Person> findByAddress_City(String city) {
		Assert.isTrue(StringUtils.isNotBlank(city), "The city must not be empty!");

		Predicate<? super Person> p = u -> u.getAddress().getCity().equalsIgnoreCase(city);

		return hashOperations.values(PERSON_KEY).filter(p);
	}

	@Override
	public Flux<Person> findByAddress_LocationWithin(Circle circle) {
		// TODO Auto-generated method stub
		return null;
	}
}
