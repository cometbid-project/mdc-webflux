/**
 * 
 */
package com.ndportmann.mdc_webflux.handlers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.MapUtils;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import com.ndportmann.mdc_webflux.helpers.XmlStreamConverterUtil;
import com.ndportmann.mdc_webflux.service.model.ObjectWithList;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Component
class PojoToXml {

	@SuppressWarnings("unchecked")
	public <T> Mono<String> doXmlConversion1(Flux<T> profiles, Class<? extends T> type) {

		Mono<List<T>> monoList = profiles.collectList();

		return monoList.map(list -> {
			ObjectWithList<T> authList = new ObjectWithList<T>();
			authList.setList(list);

			String output = XmlStreamConverterUtil.fromListModeltoXml(authList, ObjectWithList.class, type);

			// System.out.println("Output " + output);
			return output;
		});
	}

	@SuppressWarnings("unchecked")
	public <T> Mono<ObjectWithList<T>> wrapToCollection(Flux<T> profiles, Class<? extends T> type) {

		Mono<List<T>> monoList = profiles.collectList();

		return monoList.map(list -> {
			ObjectWithList<T> authList = new ObjectWithList<T>();
			authList.setList(list);

			return authList;
		});
	}

	public <T> Mono<ServerResponse> defaultWriteResponse(Mono<T> profiles, Class<T> type,
			Map<String, List<String>> headerFields, URI uri, ServerRequest r) {

		ServerResponse.BodyBuilder responseBuilder = ServerResponse.created(uri).headers(headers -> {
			if (!MapUtils.isEmpty(headerFields)) {
				headers.putAll(headerFields);
			}
		});

		return buildSingleResponse(responseBuilder, r).body(profiles, type);
	}

	public <T> Mono<ServerResponse> defaultReadMultiAuthResponse(Flux<T> profiles, Class<? extends T> type,
			Map<String, List<String>> headerFields, ServerRequest r) {

		return buildMultiResponse(profiles, type, HttpStatus.FOUND, headerFields, r);
	}

	public <T> Mono<ServerResponse> defaultReadResponse(Mono<T> profiles, Class<? extends T> type,
			Map<String, List<String>> headerFields, ServerRequest r) {

		ServerResponse.BodyBuilder responseBuilder = ServerResponse.status(HttpStatus.FOUND).headers(headers -> {
			if (!MapUtils.isEmpty(headerFields)) {
				headers.putAll(headerFields);
			}
		});
		return buildSingleResponse(responseBuilder, r).body(profiles, type);
	}

	private ServerResponse.BodyBuilder buildSingleResponse(ServerResponse.BodyBuilder responseBuilder,
			ServerRequest r) {

		List<MediaType> mediaType = r.exchange().getRequest().getHeaders().getAccept();
		log.info("Response MediaType {}", mediaType);

		if (mediaType.contains(MediaType.APPLICATION_XML)) {
			responseBuilder.contentType(MediaType.APPLICATION_XML);
		} else {
			responseBuilder.contentType(MediaType.APPLICATION_JSON);
		}

		return responseBuilder;
	}

	private <T> Mono<ServerResponse> buildMultiResponse(Flux<T> profiles, Class<? extends T> type, HttpStatus status,
			Map<String, List<String>> headerFields, ServerRequest r) {

		List<MediaType> mediaType = r.exchange().getRequest().getHeaders().getAccept();
		log.info("Response MediaType {}", mediaType);

		ServerResponse.BodyBuilder responseBuilder = ServerResponse.status(HttpStatus.FOUND).headers(headers -> {
			if (!MapUtils.isEmpty(headerFields)) {
				headers.putAll(headerFields);
			}
		});

		if (mediaType.contains(MediaType.APPLICATION_XML)) {
			Mono<ObjectWithList<T>> result = wrapToCollection(profiles, type);

			return responseBuilder.contentType(MediaType.APPLICATION_XML).body(result, ObjectWithList.class);
		} else {
			return responseBuilder.contentType(MediaType.APPLICATION_JSON).body(profiles, type);
		}
	}

}
