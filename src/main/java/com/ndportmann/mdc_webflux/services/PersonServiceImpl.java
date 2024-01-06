package com.ndportmann.mdc_webflux.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import com.ndportmann.mdc_webflux.error.handler.ErrorPublisher;
import com.ndportmann.mdc_webflux.hateoas.events.ResourceCreatedEvent;
import com.ndportmann.mdc_webflux.hateoas.events.SingleResourceRetrievedEvent;
import com.ndportmann.mdc_webflux.helpers.LocaleContextUtils;
import com.ndportmann.mdc_webflux.service.model.Gender;
import com.ndportmann.mdc_webflux.service.model.Person;
import com.ndportmann.mdc_webflux.service.model.PersonMapper;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.ndportmann.mdc_webflux.helpers.LogHelper.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

/**
 * 
 * @author Gbenga
 *
 */
@Log4j2
@Service
public class PersonServiceImpl {

	Random random = new Random();
	private final PersonMapper personMapper;
	private final ApplicationEventPublisher eventPublisher;

	public PersonServiceImpl(PersonMapper personMapper, ApplicationEventPublisher eventPublisher) {
		this.personMapper = personMapper;
		this.eventPublisher = eventPublisher;
	}

	public Mono<String> processRequestForClient(String clientId) {

		return Mono.just(clientId).flatMap(id -> processRequest(id))
				.doOnEach(logOnNext(res -> log.info("Result: {}", res)))
				.doOnEach(logOnError(e -> log.warn("An error occurred...", e)))
				.contextWrite(put("CLIENT-ID", clientId));
	}

	private Mono<String> processRequest(String id) {
		return Mono.just("42");
	}

	@Autowired
	@Qualifier("messageSource")
	private MessageSource messageSource;

	/**
	 * 
	 * @param person
	 * @return
	 */
	public Mono<Person> createPerson(final Person person, final ServerWebExchange webExchange) {

		Long id = Math.abs(random.nextLong());
		Person newPerson = new Person(String.valueOf(id), person.getFirstname(), person.getLastname(),
				person.getGender());

		List<Person> personsList = listUsers();
		final String personId = newPerson.getId();
		Optional<Person> optPerson = personsList.stream().filter(p -> p.getId().equals(personId)).findFirst();

		if (optPerson.isPresent()) {
			return ErrorPublisher.raiseResourceAlreadyExistError("user.exist", new Object[] {});
		}

		personsList.add(newPerson);
		log.info("id: {}", newPerson.getId());

		return Mono.fromSupplier(() -> newPerson).doOnSuccess(c -> {
			eventPublisher.publishEvent(new ResourceCreatedEvent(this, webExchange, newPerson.getId()));
		});
	}

	/**
	 * 
	 * @param person
	 * @return
	 */
	public Mono<Person> updatePerson(final Person updatedPerson, final Long id, final ServerWebExchange webExchange) {

		List<Person> personsList = listUsers();
		Optional<Person> optPerson = personsList.stream().filter(p -> p.getId().equals(id)).findFirst();

		if (optPerson.isPresent()) {
			personsList = personsList.stream().filter(p -> !p.getId().equals(id)).collect(Collectors.toList());

			personsList.add(updatedPerson);

			return Mono.just(updatedPerson);
		}

		return Mono.empty();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Mono<Person> getOnePerson(Long id, final ServerWebExchange webExchange) {

		List<Person> personsList = listUsers();
		Optional<Person> optPerson = personsList.stream().filter(p -> p.getId().equals(id)).findFirst();

		return Mono.justOrEmpty(optPerson).doOnSuccess(c -> {
			eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, webExchange));
		});

	}

	/**
	 * 
	 * @return
	 */
	public Flux<Person> getPersons() {

		return Flux.fromIterable(listUsers());
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Mono<String> deletePerson(String id) {

		List<Person> personsList = listUsers();
		personsList = personsList.stream().filter(p -> !p.getId().equals(id)).collect(Collectors.toList());

		return Mono.just("Delete successful");
	}

	/**
	 * 
	 * @param code
	 * @param exchange
	 * @return
	 */
	public String getMessage(String code, Object[] args) {

		return messageSource.getMessage(code, args, LocaleContextUtils.getContextLocale());
	}

	private List<Person> listUsers() {
		List<Person> manyPersons = new ArrayList<>();
		manyPersons.add(new Person(String.valueOf(1L), "Gregory", "Batistuta", Gender.MALE));
		manyPersons.add(new Person(String.valueOf(2L), "Lionel", "Messi", Gender.MALE));
		manyPersons.add(new Person(String.valueOf(3L), "Wayne", "Rooney", Gender.MALE));
		manyPersons.add(new Person(String.valueOf(4L), "Antoinne", "Griezmann", Gender.MALE));
		manyPersons.add(new Person(String.valueOf(5L), "Frankie", "Deyoung", Gender.MALE));
		manyPersons.add(new Person(String.valueOf(6L), "Roberto", "Carlos", Gender.MALE));
		manyPersons.add(new Person(String.valueOf(7L), "Ronaldo", "Diego", Gender.MALE));

		return manyPersons;
	}

}
