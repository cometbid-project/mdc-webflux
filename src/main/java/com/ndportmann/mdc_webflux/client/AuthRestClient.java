/**
 *
 */
package com.ndportmann.mdc_webflux.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import io.netty.handler.codec.http.cookie.Cookie;
import lombok.extern.log4j.Log4j2;  
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Component
class AuthRestClient {

	private final ReactiveClientInterface clientInterface;

	@Autowired
	public AuthRestClient(ReactiveClientInterface clientInterface) {
		this.clientInterface = clientInterface;
	}

	/**
	 * 
	 * @param webClient
	 * @param payload
	 * @param pathTemplate
	 * @param templateVar
	 * @param params
	 * @param token
	 * @param cookieList
	 * @return
	 */
	ResponseSpec doPut(WebClient webClient, Object payload, String pathTemplate, Map<String, Object> templateVar,
			MultiValueMap<String, String> params, String token, List<Cookie> cookieList) {

		return clientInterface.doPostOrPutOrPatch(webClient, templateVar, payload, pathTemplate, params,
				prepareHeaders(token), cookieList, HttpMethod.PUT);
	}

	/**
	 * 
	 * @param <T>
	 * @param webClient
	 * @param payload
	 * @param clazzRequest
	 * @param pathTemplate
	 * @param templateVar
	 * @param params
	 * @param token
	 * @param cookieList
	 * @return
	 */
	<T> ResponseSpec doPut(WebClient webClient, Mono<T> payload, Class<? extends T> clazzRequest, String pathTemplate,
			Map<String, Object> templateVar, MultiValueMap<String, String> params, String token,
			List<Cookie> cookieList) {

		return clientInterface.doPublisherPostOrPutOrPatch(webClient, templateVar, payload, clazzRequest, pathTemplate,
				params, prepareHeaders(token), cookieList, HttpMethod.PUT);
	}

	/**
	 * 
	 * @param webClient
	 * @param payload
	 * @param pathTemplate
	 * @param templateVar
	 * @param params
	 * @param token
	 * @param cookieList
	 * @return
	 */
	ResponseSpec doPost(WebClient webClient, Object payload, String pathTemplate, Map<String, Object> templateVar,
			MultiValueMap<String, String> params, String token, List<Cookie> cookieList) {

		return clientInterface.doPostOrPutOrPatch(webClient, templateVar, payload, pathTemplate, params,
				prepareHeaders(token), cookieList, HttpMethod.POST);
	}

	/**
	 * 
	 * @param <T>
	 * @param webClient
	 * @param payload
	 * @param clazzRequest
	 * @param pathTemplate
	 * @param templateVar
	 * @param params
	 * @param token
	 * @param cookieList
	 * @return
	 */
	<T> ResponseSpec doPost(WebClient webClient, Mono<T> payload, Class<? extends T> clazzRequest, String pathTemplate,
			Map<String, Object> templateVar, MultiValueMap<String, String> params, String token,
			List<Cookie> cookieList) {

		return clientInterface.doPublisherPostOrPutOrPatch(webClient, templateVar, payload, clazzRequest, pathTemplate,
				params, prepareHeaders(token), cookieList, HttpMethod.POST);
	}

	/**
	 * 
	 * @param webClient
	 * @param payload
	 * @param pathTemplate
	 * @param templateVar
	 * @param params
	 * @param token
	 * @param cookieList
	 * @return
	 */
	ResponseSpec doPatch(WebClient webClient, Object payload, String pathTemplate, Map<String, Object> templateVar,
			MultiValueMap<String, String> params, String token, List<Cookie> cookieList) {

		return clientInterface.doPostOrPutOrPatch(webClient, templateVar, payload, pathTemplate, params,
				prepareHeaders(token), cookieList, HttpMethod.PATCH);
	}

	/**
	 * 
	 * @param <T>
	 * @param webClient
	 * @param payload
	 * @param clazzRequest
	 * @param pathTemplate
	 * @param templateVar
	 * @param params
	 * @param token
	 * @param cookieList
	 * @return
	 */
	<T> ResponseSpec doPatch(WebClient webClient, Mono<T> payload, Class<? extends T> clazzRequest, String pathTemplate,
			Map<String, Object> templateVar, MultiValueMap<String, String> params, String token,
			List<Cookie> cookieList) {

		return clientInterface.doPublisherPostOrPutOrPatch(webClient, templateVar, payload, clazzRequest, pathTemplate,
				params, prepareHeaders(token), cookieList, HttpMethod.PATCH);
	}

	/**
	 *
	 * @param path
	 * @param uri
	 * @param templateVar
	 * @param params
	 * @param token
	 * @param cookieList
	 * @return
	 */
	ResponseSpec doGetMethod(WebClient webClient, String pathTemplate, Map<String, Object> templateVar,
			MultiValueMap<String, String> params, String token, List<Cookie> cookieList) {

		log.info("Beginning GET REST Service call...");

		return clientInterface.doGet(webClient, templateVar, params, pathTemplate, prepareHeaders(token), cookieList);
	}

	/**
	 *
	 * @param formData
	 * @param path
	 * @param uri
	 * @param templateVar
	 * @param authorizationHeader
	 * @param cookieList
	 * @param httpMethod
	 * @return
	 */
	ResponseSpec doFormDataPost(WebClient webClient, MultiValueMap<String, String> formData, String path, String uri,
			Map<String, Object> templateVar, String token, List<Cookie> cookieList) {

		log.info("Beginning Form Post REST Service call...");

		Map<String, List<String>> headerMap = prepareHeaders(token);
		headerMap.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED_VALUE));

		return clientInterface.doFormDataPostOrPut(webClient, templateVar, formData, path, headerMap, cookieList,
				HttpMethod.POST);
	}

	/**
	 *
	 * @param parts
	 * @param path
	 * @param uri
	 * @param templateVar
	 * @param authorizationHeader
	 * @param cookieList
	 * @param httpMethod
	 * @return
	 */
	ResponseSpec doMultipartDataPost(WebClient webClient, MultiValueMap<String, HttpEntity<?>> multiParts, String path,
			String uri, Map<String, Object> templateVar, String token, List<Cookie> cookieList) {

		log.info("Beginning Multipart REST Service call...");

		Map<String, List<String>> headerMap = prepareHeaders(token);
		headerMap.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.MULTIPART_FORM_DATA_VALUE));

		return clientInterface.doMultipartPostOrPut(webClient, templateVar, multiParts, path, headerMap, cookieList,
				HttpMethod.POST);
	}

	/**
	 *
	 * @param path
	 * @param uri
	 * @param templateVar
	 * @param authorizationHeader
	 * @param cookieList
	 * @return
	 */
	ResponseSpec doDelete(WebClient webClient, String path, String uri, Map<String, Object> templateVar, String token,
			MultiValueMap<String, String> params, List<Cookie> cookieList) {

		log.info("Beginning Delete REST Service call...");

		return clientInterface.doDelete(webClient, templateVar, path, params, prepareHeaders(token), cookieList);
	}

	private Map<String, List<String>> prepareHeaders(String authHeader) {

		Map<String, List<String>> myHeaders = new HashMap<>();
		if (StringUtils.isNotBlank(authHeader)) {

			myHeaders.put(HttpHeaders.AUTHORIZATION, Arrays.asList("Bearer " + authHeader));
		}

		myHeaders.put(HttpHeaders.ACCEPT, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
		return myHeaders;
	}

	private MultiValueMap<String, HttpEntity<?>> buildMultiparts(Map<String, Object> formParts) {

		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		formParts.forEach((k, v) -> builder.part(k, v));

		// Build and use
		return builder.build();
	}

	private MultiValueMap<String, HttpEntity<?>> sampleBuilder() throws IOException {

		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

		bodyBuilder.part("profileImage", new ClassPathResource("test-image.jpg").getFile())
				.header("Content-Disposition", "form-data; name=profileImage; filename=profile-image.jpg");

		Resource logo = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
		Flux<DataBuffer> buffers = DataBufferUtils.read(logo, new DefaultDataBufferFactory(), 1024);
		long contentLength = logo.contentLength();

		bodyBuilder.asyncPart("buffers", buffers, DataBuffer.class).headers(h -> {
			h.setContentDispositionFormData("buffers", "buffers.jpg");
			h.setContentType(MediaType.IMAGE_JPEG);
			h.setContentLength(contentLength);
		});

		bodyBuilder.part("resource", new UrlResource("file:/tmp/test-document.pdf")).headers(h -> {
			h.setContentDispositionFormData("userDocument", "my-thesis.pdf");
			h.setContentType(MediaType.APPLICATION_PDF);
		});

		bodyBuilder.part("username", "shiveenpandita", MediaType.TEXT_PLAIN)
				.header("Content-Disposition", "form-data; name=username").header("Content-type", "text/plain");

		bodyBuilder.part("email", "shiveenpandita@gmail.com", MediaType.TEXT_PLAIN)
				.header("Content-Disposition", "form-data; name=email").header("Content-type", "text/plain");

		return bodyBuilder.build();
	}
}
