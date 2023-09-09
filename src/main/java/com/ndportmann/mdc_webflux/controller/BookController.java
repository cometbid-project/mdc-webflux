/**
 * 
 */
package com.ndportmann.mdc_webflux.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ndportmann.mdc_webflux.service.model.Book;
import com.ndportmann.mdc_webflux.services.BookServiceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @author Gbenga
 * 
 * Uses Redis as Repository
 */
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class BookController {

	private final BookServiceImpl bookService;

	@PostMapping("/book")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Book> addBook(@RequestBody @Valid Book book) {
		return bookService.create(book);
	}

	@GetMapping("/book")
	public Flux<Book> getAllBooks() {
		return bookService.getAll();
	}

	@GetMapping("/book/{id}")
	public Mono<Book> getBook(@PathVariable String id) {
		return bookService.getOne(id);
	}

	@DeleteMapping("/book/{id}")
	public Mono<Long> deleteBook(@PathVariable String id) {
		return bookService.deleteById(id);
	}

}