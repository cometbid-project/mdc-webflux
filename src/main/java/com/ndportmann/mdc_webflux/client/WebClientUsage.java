/**
 * 
 */
package com.ndportmann.mdc_webflux.client;

import java.net.URI;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.ndportmann.mdc_webflux.config.ApiRetryConfig;
import com.ndportmann.mdc_webflux.config.AuthClientProperties;
import com.ndportmann.mdc_webflux.error.handler.ErrorPublisher;
import com.ndportmann.mdc_webflux.error.handler.ExceptionHandler;
import com.ndportmann.mdc_webflux.exceptions.ServiceUnavailableException;

import io.netty.handler.ssl.SslHandshakeTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;
import reactor.util.retry.Retry;
import reactor.util.retry.Retry.RetrySignal;
import reactor.util.retry.RetryBackoffSpec;

/**
 * @author Gbenga
 *
 */
@Log4j2 
@Component
public class WebClientUsage {

	private final WebClient webClient;  
	private final AuthClientProperties clientProperties;
	private final AuthRestClient restWebClient;

	private final ObjectMapper objectMapper;
	private int maxRetries;
	private long minBackoff;

	
	public WebClientUsage(@Qualifier("webClientOauth") WebClient webClient, 
			AuthRestClient restWebClient,
			AuthClientProperties clientProperties, ObjectMapper objectMapper) {
		this.webClient = webClient;
		this.restWebClient = restWebClient;
		this.clientProperties = clientProperties;
		this.objectMapper = objectMapper;
		
		this.minBackoff = this.clientProperties.getBackOffPeriodInMillis();
		this.maxRetries = this.clientProperties.getMaxRetriesPerTransaction();
	}

	/**
	 * We used the WebClient's httpRequest() method to get access to the native
	 * HttpClientRequest from the underlying Netty library. Next, we used it to set
	 * the timeout value to 2 seconds.
	 * 
	 * Response timeout setting overrides any response timeout on the HttpClient
	 * level
	 */
	public void makeGetCall() {

		webClient.get().uri("https://baeldung.com/path").httpRequest(httpRequest -> {
			HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
			reactorRequest.responseTimeout(Duration.ofSeconds(2));
		});
	}

	/**
	 * TimeoutException will appear in case no item arrives within the given 5
	 * seconds.
	 * 
	 * It's better to use the more specific timeout configuration options available
	 * in Reactor Netty since they provide more control for a specific purpose and
	 * use case
	 * 
	 * The timeout() method applies to the whole operation, from establishing the
	 * connection to the remote peer to receiving the response. It doesn't override
	 * any HttpClient related settings.
	 */
	public void makeGetCallWithTimeout() {

		webClient.get().uri("https://baeldung.com/path").retrieve().bodyToFlux(JsonNode.class)  // Replace with domain type
				.timeout(Duration.ofSeconds(5));
	}

	/**
	 * 
	 */
	public void makeGetCallHandleTimeoutError() {

		webClient.get().uri("https://baeldung.com/path").retrieve().bodyToFlux(JsonNode.class) // Replace with domain type
				.timeout(Duration.ofSeconds(5), 
				         Mono.just(objectMapper.createObjectNode().put("message", "fallback")))
				.onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
				.onErrorReturn(SslHandshakeTimeoutException.class, new TextNode("SslHandshakeTimeout"))
				.doOnError(WriteTimeoutException.class, ex -> log.error("WriteTimeout"));
	}
	
	/**
	 * 
	 * @param isbn
	 */
	public void getDataWithTimeoutRetryAndBackoffStrategy() {
		webClient
				.get()
				.uri("https://baeldung.com/path")
				.retrieve()
				.bodyToMono(JsonNode.class)  // Replace with domain type
				.timeout(Duration.ofSeconds(3), Mono.empty())
				.onErrorResume(WebClientResponseException.NotFound.class, 
						exception -> Mono.empty())
				.retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
				.onErrorResume(Exception.class, 
						exception -> Mono.empty());
	}


	/**
	 * 
	 */
	public void makeGetCallWithCustomErrorHandler() {

		webClient.get().uri("https://baeldung.com/path").retrieve().onStatus(HttpStatus::is4xxClientError, resp -> {
			log.error("ClientError {}", resp.statusCode());
			return Mono.error(new RuntimeException("ClientError"));
		}).bodyToFlux(JsonNode.class).timeout(Duration.ofSeconds(5))
				.onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
				.onErrorMap(WebClientResponseException.class, ex -> ExceptionHandler.handle4xxException(ex))
				.onErrorReturn(SslHandshakeTimeoutException.class, new TextNode("SslHandshakeTimeout"))
				.doOnError(WriteTimeoutException.class, ex -> log.error("WriteTimeout"))
				.retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(minBackoff)).filter(
						throwable -> throwable instanceof TimeoutException || throwable instanceof ReadTimeoutException)
						.filter(throwable -> {
							if (throwable instanceof WebClientResponseException) {

								return ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
							}
							return false;
						}));

	}

	/**
	 * 
	 * @param stockId
	 * @return
	 */
	public Mono<String> getDataWithRetryAndBackoffStrategy(String stockId) {

		return webClient.get().uri("https://baeldung.com/path", stockId).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class).retryWhen(Retry.backoff(3, Duration.ofSeconds(2)).jitter(0.75));
	}

	/**
	 * 
	 * @param stockId
	 * @return
	 */
	public Mono<String> getDataWithFilterException(String stockId) {

		return webClient.get().uri("https://baeldung.com/path", stockId).retrieve()
				.onStatus(HttpStatus::is5xxServerError,
						response -> ErrorPublisher.raiseServiceUnavailableError("server.error",
								new Object[] { response.rawStatusCode() }))
				.onStatus(HttpStatus::is4xxClientError,
						response -> ErrorPublisher.raiseResourceNotFoundError("client.error",
								new Object[] { response.rawStatusCode() }))
				.bodyToMono(String.class).retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
						.filter(throwable -> throwable instanceof ServiceUnavailableException));
	}

	/**
	 * 
	 * @param stockId
	 * @return
	 */
	public Mono<String> getDataHandlExhaustedRetry(String stockId) {

		return webClient.get().uri("https://baeldung.com/path", stockId).retrieve()
				.onStatus(HttpStatus::is5xxServerError,
						response -> ErrorPublisher.raiseServiceUnavailableError("server.error",
								new Object[] { response.rawStatusCode() }))
				.bodyToMono(String.class)
				.retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
						.filter(throwable -> throwable instanceof ServiceUnavailableException).onRetryExhaustedThrow(
								(retryBackoffSpec, retrySignal) -> ErrorPublisher.raiseServiceUnavailableException(
										"server.maxRetry.error", new Object[] { HttpStatus.SERVICE_UNAVAILABLE.value() })));
	}
	
	/**
	 * 
	 * @param stockId
	 * @return
	 */
	public Mono<String> getDataWithOverridingBearerToken(String stockId, String overridingToken) {

		return webClient.get().uri("https://baeldung.com/path", stockId)
				.headers(headers -> headers.setBearerAuth(overridingToken))
				.retrieve()
				.onStatus(HttpStatus::is5xxServerError,
						response -> ErrorPublisher.raiseServiceUnavailableError("server.error",
								new Object[] { response.rawStatusCode() }))
				.onStatus(HttpStatus::is4xxClientError,
						response -> ErrorPublisher.raiseResourceNotFoundError("client.error",
								new Object[] { response.rawStatusCode() }))
				.bodyToMono(String.class)
				//
				.retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
						.filter(throwable -> throwable instanceof ServiceUnavailableException));
	}
	
	
	public Mono<JsonNode[]> getWithLocalizedRetryHandlingData(final String dataId) { 
		 return webClient.get() 
		 .uri(buildPathUrl(dataId)) 
		 .retrieve() 
		 .bodyToMono(JsonNode[].class) 
		 .retryWhen(ApiRetryConfig.retryWhenTooManyRequests());
	}
	
	@Autowired
	@Qualifier("clientCredentialFlow")
	private WebClient webOauthClient;
	
	/**
	 * 
	 * @return
	 */
	public Mono<String> getDataOauthRequiredRemoteMachine() {
		
	  return webOauthClient.get()
				  .uri("http://localhost:8084/retrieve-resource")
				  .attributes(
				    ServerOAuth2AuthorizedClientExchangeFilterFunction
				      .clientRegistrationId("bael"))
				  .retrieve()
				  .bodyToMono(String.class);
	}
	
	/**
	 * /products/{id} 
	 * 
	 * @param dataId
	 * @return
	 */
	Function<UriBuilder, URI> buildPathUrl(String dataId) {
		
		return uriBuilder -> uriBuilder
			    .path("/products/{id}")
			    .build(dataId);
	}
	/**
	 *  /products/{id}/attributes/{attributeId} 
	 *  
	 * @param dataId
	 * @param attributeId
	 * @return
	 */
	Function<UriBuilder, URI> buildPathUrl(String dataId, String attributeId) {
		
		return uriBuilder -> uriBuilder
			    .path("/products/{id}/attributes/{attributeId}")
			    .build(dataId, attributeId);
	}
	
	/**
	 * /products/?name={name}&deliveryDate={deliveryDate}&color={color} 
	 * 
	 * @param dataId
	 * @param attributeId
	 * @return
	 */
	Function<UriBuilder, URI> buildQueryParameterUrl(Map<String, String> parameters) {
		MultiValueMap<String, String> multivalue = new LinkedMultiValueMap<>(
				parameters.entrySet().stream().collect(
						Collectors.toMap(Map.Entry::getKey, e -> Arrays.asList(e.getValue())))
		);
		
		return uriBuilder -> uriBuilder
				    .path("/products/")
				    .queryParams(multivalue)
				    .build();
		/*
		return uriBuilder -> uriBuilder
			    .path("/products/")
			    .queryParam("name", "AndroidPhone")
			    .queryParam("color", "black")
			    .queryParam("deliveryDate", "13/04/2019")
			    .build();
		return uriBuilder - > uriBuilder
		    .path("/products/")
		    .queryParam("name", "{title}")
		    .queryParam("color", "{authorId}")
		    .queryParam("deliveryDate", "{date}")
		    .build("AndroidPhone", "black", "13/04/2019");
	    */
	}
	
	Function<UriBuilder, URI> buildQueryParameterUrl(String... parameters) {
		
		return uriBuilder -> uriBuilder
				    .path("/products/")
				    .queryParam("tag[]", (Object[])parameters)
				    .build();
		/*
		return uriBuilder -> uriBuilder
			    .path("/products/")
			    .queryParam("category", "Phones", "Tablets")
			    .build();
			    
		return uriBuilder - > uriBuilder
		    .path("/products/")
		    .queryParam("category", String.join(",", "Phones", "Tablets"))
		    .build();
		 */		
	}

}
