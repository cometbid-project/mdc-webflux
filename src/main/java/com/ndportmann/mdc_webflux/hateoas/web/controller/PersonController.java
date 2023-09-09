/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.web.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.ndportmann.mdc_webflux.service.model.Person;
import com.ndportmann.mdc_webflux.service.model.PersonMapper;
import com.ndportmann.mdc_webflux.services.PersonServiceImpl;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/users")
//@EnableHypermediaSupport(type = HypermediaType.HAL)
public class PersonController {

	private final PersonServiceImpl fooService;
	private final PersonMapper personMapper;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}/hateoas")
	public Mono<EntityModel<Person>> getPerson(@PathVariable("id") Long personId, ServerWebExchange webExchange) {

		// Mono<Person> person = fooService.getOnePerson(personId);
		PersonController controller = methodOn(PersonController.class);

		Mono<Link> selfLink = linkTo(controller.getPerson(personId, webExchange)) //
				.withSelfRel() //
				.andAffordance(controller.updatePerson(personId, null, webExchange)) // <3>
				.andAffordance(controller.createPerson(null, webExchange)) // <4>
				.andAffordance(controller.updatePerson(personId, null, webExchange)) // <4>
				.andAffordance(controller.deletePerson(personId)) // <4>
				.toMono();

		Mono<Link> aggregateLink = linkTo(controller.getAllPersons(webExchange)) //
				.withRel("persons") //
				.toMono();

		return Mono.zip(fooService.getOnePerson(personId, webExchange), selfLink, aggregateLink) //
				.map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));

	}

	@GetMapping("/hateoas")
	public Mono<CollectionModel<EntityModel<Person>>> getAllPersons(ServerWebExchange webExchange) {

		PersonController controller = methodOn(PersonController.class);

		Mono<Link> aggregateRoot = linkTo(controller.getAllPersons(webExchange)) //
				.withSelfRel() //
				.andAffordance(controller.createPerson(null, webExchange)) // <1>
				.toMono();

		return this.fooService.getPersons()// <2>
				.flatMap(item -> getPerson(Long.valueOf(item.getId()), webExchange)) // <3>
				.collectList() // <4>
				.flatMap(models -> aggregateRoot //
						.map(selfLink -> CollectionModel.of( //
								models, selfLink))); // <5>
	}

	/**
	 * 
	 * @param personModel
	 * @param r
	 * @return
	 */
	@PostMapping(value = "/hateoas", produces = { "application/hal+json" })
	public Mono<ResponseEntity<?>> createPerson(@RequestBody Mono<Person> person,
			ServerWebExchange webExchange) {

		Mono<EntityModel<Person>> personModel = person.map(EntityModel::of);
		
		return personModel //
				.map(EntityModel::getContent) // <3>
				.flatMap(model -> this.fooService.createPerson(model, webExchange)) // <4>
				.map(Person::getId) // <5>
				.flatMap(id -> this.getPerson(Long.valueOf(id), webExchange)) // <6>
				.map(newModel -> ResponseEntity.created(newModel // <7>
						.getRequiredLink(IanaLinkRelations.SELF) //
						.toUri()).body(newModel.getContent()));
	}

	/**
	 * 
	 * @param id
	 * @param personModel
	 * @return
	 */
	@PutMapping(value = "/{id}/hateoas", produces = { "application/hal+json" })
	public Mono<ResponseEntity<?>> updatePerson(@PathVariable("id") Long id,
			@RequestBody Mono<Person> person, ServerWebExchange webExchange) {

		Mono<EntityModel<Person>> personModel = person.map(EntityModel::of);
		
		return personModel //
				.map(EntityModel::getContent) //
				.map(content -> new Person(String.valueOf(id), content.getFirstname(), // <3>
						content.getLastname(), content.getGender())) //
				.flatMap(model -> this.fooService.updatePerson(model, id, webExchange)) // <4>
				.then(this.getPerson(id, webExchange)) // <5>
				.map(model -> ResponseEntity.noContent() // <6>
						.location(model.getRequiredLink(IanaLinkRelations.SELF)//
								.toUri()).build());
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}/hateoas")
	public Mono<ResponseEntity<?>> deletePerson(@PathVariable("id") Long id) {

		return fooService.deletePerson(String.valueOf(id)) 
				.map(model -> ResponseEntity.noContent() // <6>
						.build());
	}

}