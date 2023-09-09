/**
 * 
 */
package com.ndportmann.mdc_webflux.services;

import org.springframework.stereotype.Service;
import com.ndportmann.mdc_webflux.repository.BookRepositoryImpl;
import com.ndportmann.mdc_webflux.service.model.Book;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

	private final BookRepositoryImpl bookRepository;

	@Override
	public Mono<Book> create(Book book) {
		return bookRepository.save(book);
	}

	@Override
	public Flux<Book> getAll() {
		return bookRepository.findAll();
	}

	@Override
	public Mono<Book> getOne(String id) {
		return bookRepository.findById(id);
	}

	@Override
	public Mono<Long> deleteById(String id) {
		return bookRepository.delete(id);
	}

}