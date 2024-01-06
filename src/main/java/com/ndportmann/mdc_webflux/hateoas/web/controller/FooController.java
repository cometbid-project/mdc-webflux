/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.web.controller;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.Preconditions;
import com.ndportmann.mdc_webflux.exceptions.BadRequestException;
import com.ndportmann.mdc_webflux.exceptions.ResourceNotFoundException;
import com.ndportmann.mdc_webflux.hateoas.events.PaginatedResultsRetrievedEvent;
import com.ndportmann.mdc_webflux.hateoas.events.ResourceCreatedEvent;
import com.ndportmann.mdc_webflux.hateoas.events.SingleResourceRetrievedEvent;
import com.ndportmann.mdc_webflux.hateoas.services.IFooService;
import com.ndportmann.mdc_webflux.helpers.RestPreconditions;
import com.ndportmann.mdc_webflux.service.model.Foo;

/**
 * @author Gbenga
 *
 */
@Log4j2
@RestController
@RequestMapping(value = "/foos")
public class FooController {

	// private static final Logger logger =
	// LoggerFactory.getLogger(FooController.class);

	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	private Sort DEFAULT_SORT = Sort.unsorted();

	@Autowired
	private IFooService service;

	public FooController() {
		super();
	}

	// API

	// Note: the global filter overrides the ETag value we set here. We can still
	// analyze its behaviour in the Integration Test.
	@GetMapping(value = "/{id}/custom-etag")
	public ResponseEntity<Foo> findByIdWithCustomEtag(@PathVariable("id") final Long id,
			final ServerWebExchange webExchange) {
		
		final var newId = service.createId(id);
		final Foo foo = RestPreconditions.checkFound(service.findById(newId));

		eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, webExchange));
		return ResponseEntity.ok().eTag(Long.toString(foo.getVersion())).body(foo);
	}

	// read - one

	@GetMapping(value = "/{id}")
	public Foo findById(@PathVariable("id") final Long id, final ServerWebExchange webExchange) {
		try {
			final var newId = service.createId(id);
			final Foo resourceById = RestPreconditions.checkFound(service.findById(newId));

			eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, webExchange));
			return resourceById;
		} catch (ResourceNotFoundException exc) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foo Not Found", exc);
		}
	}

	// read - all
	@GetMapping
	public List<Foo> findAll() {
		return service.findAll(DEFAULT_SORT);
	}

	@GetMapping(params = { "page", "size" })
	public List<Foo> findPaginated(@RequestParam("page") final int page, @RequestParam("size") final int size,
			final UriComponentsBuilder uriBuilder, final ServerWebExchange webExchange) {

		final Page<Foo> resultPage = service.findPaginated(page, size);
		if (page > resultPage.getTotalPages()) {
			throw new ResourceNotFoundException();
		}
		eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Foo>(Foo.class, uriBuilder, webExchange, page,
				resultPage.getTotalPages(), size));

		return resultPage.getContent();
	}

	@GetMapping("/pageable")
	public List<Foo> findPaginatedWithPageable(Pageable pageable, final UriComponentsBuilder uriBuilder,
			final ServerWebExchange webExchange) {

		final Page<Foo> resultPage = service.findPaginated(pageable);
		if (pageable.getPageNumber() > resultPage.getTotalPages()) {
			throw new ResourceNotFoundException();
		}
		eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Foo>(Foo.class, uriBuilder, webExchange,
				pageable.getPageNumber(), resultPage.getTotalPages(), pageable.getPageSize()));

		return resultPage.getContent();
	}

	// write

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Foo create(@RequestBody final Foo resource, final ServerWebExchange webExchange) {
		Preconditions.checkNotNull(resource);
		
		final Foo foo = service.create(resource);
		final String idOfCreatedResource = foo.getId().asString();

		eventPublisher.publishEvent(new ResourceCreatedEvent(this, webExchange, idOfCreatedResource));

		return foo;
	}

	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void update(@PathVariable("id") final Long id, @RequestBody final Foo resource) {
		Preconditions.checkNotNull(resource);
		
		final var newId = service.createId(id);
		RestPreconditions.checkFound(service.findById(newId));
		service.update(resource);
	}

	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable("id") final Long id) {
		
		final var newId = service.createId(id);
		
		service.deleteById(newId);
	}

	@ExceptionHandler({ ResourceNotFoundException.class, BadRequestException.class })
	public void handleException(final Exception ex) {
		final String error = "Application specific error handling";
		log.error(error, ex);
	}
}
