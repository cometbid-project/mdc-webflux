/**
 * 
 */
package com.ndportmann.mdc_webflux.services;

import com.ndportmann.mdc_webflux.service.model.Book;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
public interface BookService {

	Mono<Book> create(Book book);

	Flux<Book> getAll();

	Mono<Book> getOne(String id);

	Mono<Long> deleteById(String id);

}