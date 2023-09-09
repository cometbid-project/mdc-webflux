/**
 * 
 */
package com.ndportmann.mdc_webflux.repository;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;

import com.ndportmann.mdc_webflux.helpers.ObjectMapperUtils;
import com.ndportmann.mdc_webflux.service.model.Book;
import com.ndportmann.mdc_webflux.service.model.Person;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Repository
//@RequiredArgsConstructor
public class BookRepositoryImpl extends AbstractRedisBaseRepository<Book> {

	public static final String BOOK_KEY = "BK";
	// private final ReactiveRedisComponent reactiveRedisComponent;

	/**
	 * @param reactiveRedisComponent
	 * @param key
	 * @param contentClass
	 * @param reactiveRedisComponent2
	 */
	public BookRepositoryImpl(ReactiveRedisComponent reactiveRedisComponent,
			ReactiveRedisOperations<String, Book> redisOperations) {
		super(BOOK_KEY, Book.class, redisOperations);
		// this.reactiveRedisComponent = reactiveRedisComponent;
	}

	@Override
	public Mono<Long> delete(String id) {
		return this.delete(id);
	}

	@Override
	public Mono<Void> delete(Book entity) {
		// TODO Auto-generated method stub
		return delete(entity.getId()).then();
	}

}
