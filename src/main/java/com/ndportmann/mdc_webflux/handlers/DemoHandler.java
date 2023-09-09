/**
 * 
 */
package com.ndportmann.mdc_webflux.handlers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariable.VariableType;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ndportmann.mdc_webflux.controller.DemoController;
import com.ndportmann.mdc_webflux.hateoas.events.ResourceCreatedEvent;
import com.ndportmann.mdc_webflux.hateoas.web.controller.PersonController;
import com.ndportmann.mdc_webflux.helpers.XmlStreamConverterUtil;
import com.ndportmann.mdc_webflux.service.model.Gender;
import com.ndportmann.mdc_webflux.service.model.ObjectWithList;
import com.ndportmann.mdc_webflux.service.model.Person;
import com.ndportmann.mdc_webflux.services.PersonServiceImpl;

import java.lang.reflect.Method;
import java.net.URI;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.reactivestreams.Publisher;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class DemoHandler {

	private final PersonServiceImpl fooService;
	private final PojoToXml responseCreator;

	public Mono<ServerResponse> getMessage(ServerRequest r) {

		ServerWebExchange serverWebExchange = r.exchange();
		String code = "exception.UnavailableService";

		String responses = fooService.getMessage(code, new Object[] {});

		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(responses);
	}

	public Mono<ServerResponse> getGlobalMessage(ServerRequest r) {

		ServerWebExchange serverWebExchange = r.exchange();
		String code = "global.error.shortMessage";

		String responses = fooService.getMessage(code, new Object[] {});

		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(responses);
	}

	public Mono<ServerResponse> getPerson(ServerRequest r) {

		String id = r.pathVariable("id");
		Long personId = Long.valueOf(id);

		return responseCreator.defaultReadResponse(fooService.getOnePerson(personId, r.exchange()), Person.class, null, r);
	}

	public Mono<ServerResponse> getPersons(ServerRequest r) {

		return responseCreator.defaultReadMultiAuthResponse(fooService.getPersons(), Person.class, null, r);
	}

	public Mono<ServerResponse> createPerson(ServerRequest r) {

		return r.bodyToMono(Person.class).flatMap(person -> {
			log.debug("Request Body: {}", person);
			ServerWebExchange webExchange = r.exchange();

			return this.fooService.createPerson(person, webExchange).flatMap(p -> {
				final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(webExchange.getRequest().getURI()).replaceQuery(null);
				URI uri = uriBuilder.path("/{id}").buildAndExpand(p.getId()).toUri();
				
				return responseCreator.defaultWriteResponse(Mono.just(p), Person.class, null, uri, r);
			});
			//return responseCreator.defaultWriteResponse(this.fooService.createPerson(person, webExchange), Person.class, null, uri,
				//	r);
		});

	}

	private static URI hateaosLinks(String path, String... pathVariable) {
		Link link = Link.of("/{segment}/{something}{?parameter}");
		// assertThat(link.isTemplated()).isTrue();
		// assertThat(link.getVariableNames()).contains("segment", "parameter");

		Map<String, Object> values = new HashMap<>();
		values.put("segment", path);
		values.put("something", pathVariable);
		// values.put("parameter", 42);

		Link expandedLink = link.expand(values);
		return expandedLink.toUri();

		// assertThat(link.expand(values).getHref())
		// .isEqualTo("/path/something?parameter=42");
	}

	private void hateaosUriTemplates() {
		UriTemplate template = UriTemplate.of("/{segment}/something")
				.with(new TemplateVariable("parameter", VariableType.REQUEST_PARAM));

		// assertThat(template.toString()).isEqualTo("/{segment}/something{?parameter}");
	}

	private void hateaosLinkRelation(String path) {
		Link link = Link.of(path, IanaLinkRelations.NEXT);

		URI uri = link.toUri();
		// assertThat(link.getRel()).isEqualTo(LinkRelation.of("next"));
		// assertThat(IanaLinkRelations.isIanaRel(link.getRel())).isTrue();
	}

	private void hateaosRelationModel() {
		Person customModel = new Person(String.valueOf(1L), "Dave", "Matthews", Gender.MALE);
		//customModel.setFirstname("Dave");
		//customModel.setLastname("Matthews");

		//customModel.add(Link.of("https://myhost/people/42"));

		// ====================================================

		Person person = new Person(String.valueOf(1L), "Dave", "Matthews", Gender.MALE);
		EntityModel<Person> entityModel = EntityModel.of(person);

		// ====================================================

		Collection<Person> people = Collections.singleton(new Person(String.valueOf(1L), "Dave", "Matthews", Gender.MALE));
		CollectionModel<Person> model = CollectionModel.of(people);
	}

	private void hateaosMVCApproach() throws Exception {

		Link collectionLink = linkTo(DemoController.class).withRel("people");

		// assertThat(link.getRel()).isEqualTo(LinkRelation.of("people"));
		// assertThat(link.getHref()).endsWith("/people");

		Person person = new Person(String.valueOf(1L), "Dave", "Matthews", Gender.FEMALE);
		/// person / 1
		Link entityLink = linkTo(DemoController.class).slash(person.getId()).withSelfRel();
		URI uri = entityLink.toUri();

		// ====================================================
		// assertThat(link.getRel(), is(IanaLinkRelations.SELF.value()));
		// assertThat(link.getHref(), endsWith("/people/1"));

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(linkTo(DemoController.class).slash(person).toUri());

		ResponseEntity<Person> responseEntity = new ResponseEntity<Person>(headers, HttpStatus.CREATED);

		// ====================================================

		Method method = DemoController.class.getMethod("show", Long.class);
		Link dummylink = linkTo(method, 2L).withSelfRel();

		// assertThat(link.getHref()).endsWith("/people/2");
		Link methodlink = linkTo(methodOn(DemoController.class).clientDemo("2L")).withSelfRel();

		// assertThat(link.getHref()).endsWith("/people/2");
		// ====================================================
	}
}
