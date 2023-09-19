/**
 * 
 */
package com.ndportmann.mdc_webflux.client;

import java.net.http.HttpTimeoutException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.ndportmann.mdc_webflux.config.AuthClientProperties;
import com.ndportmann.mdc_webflux.helpers.ApiPaths;
import com.ndportmann.mdc_webflux.service.model.Product;

import io.netty.handler.ssl.SslHandshakeTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Component
public class ProductClientUsage {

	private final WebClient webClient;
	
	Scheduler scheduler = Schedulers.newBoundedElastic(5, 10, "MyThreadGroup");

	//@Autowired
	public ProductClientUsage(@Qualifier("productWebClient") WebClient webClient) {

		this.webClient = webClient;
	}

	/**
	 * @return
	 * 
	 */
	public Flux<Product> getAllProducts() {

		return webClient.get().uri(uriBuilder -> uriBuilder.path("/products").build()).retrieve()
				.bodyToFlux(Product.class).timeout(Duration.ofSeconds(5))
				.onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
				.onErrorReturn(SslHandshakeTimeoutException.class, new Product("SslHandshakeTimeout"))
				.doOnError(WriteTimeoutException.class, ex -> log.error("WriteTimeout"))
				.subscribeOn(scheduler).publishOn(scheduler);

		// verifyCalledUrl("/products");
	}

	/**
	 * @return
	 * 
	 */
	public Mono<Product> getProductById() {

		return webClient.get().uri(uriBuilder -> uriBuilder.path("/products/{id}").build(2)).retrieve()
				.bodyToMono(Product.class).timeout(Duration.ofSeconds(5))
				.onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
				.onErrorReturn(SslHandshakeTimeoutException.class, new Product("SslHandshakeTimeout"))
				.doOnError(WriteTimeoutException.class, ex -> log.error("WriteTimeout"))
				.subscribeOn(scheduler).publishOn(scheduler);

		// verifyCalledUrl("/products/2");
	}

	/**
	 * @return
	 * 
	 */
	public Mono<Product> getProductByAttributeId() {

		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/products/{id}/attributes/{attributeId}").build(2, 13)).retrieve()
				.bodyToMono(Product.class).timeout(Duration.ofSeconds(5))
				.onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
				.onErrorReturn(SslHandshakeTimeoutException.class, new Product("SslHandshakeTimeout"))
				.doOnError(WriteTimeoutException.class, ex -> log.error("WriteTimeout"))
				.subscribeOn(scheduler).publishOn(scheduler);

		// verifyCalledUrl("/products/2/attributes/13");
	}

	/**
	 * @return
	 * 
	 */
	public Flux<Product> getProductBySingleParameters() {

		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/products/").queryParam("name", "AndroidPhone")
						.queryParam("color", "black").queryParam("deliveryDate", "13/04/2019").build())
				.retrieve().bodyToFlux(Product.class).timeout(Duration.ofSeconds(5))
				.onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
				.onErrorReturn(SslHandshakeTimeoutException.class, new Product("SslHandshakeTimeout"))
				.doOnError(WriteTimeoutException.class, ex -> log.error("WriteTimeout"))
				.subscribeOn(scheduler).publishOn(scheduler);

		// verifyCalledUrl("/products/?name=AndroidPhone&color=black&deliveryDate=13/04/2019");
	}

	/**
	 * @return
	 * 
	 */
	public Flux<Product> getProductBySingleParametersAnotherWay() {

		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/products/").queryParam("name", "{title}")
						.queryParam("color", "{authorId}").queryParam("deliveryDate", "{date}")
						.build("AndroidPhone", "black", "13/04/2019"))
				.retrieve().bodyToFlux(Product.class).timeout(Duration.ofSeconds(5))
				.onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
				.onErrorReturn(SslHandshakeTimeoutException.class, new Product("SslHandshakeTimeout"))
				.doOnError(WriteTimeoutException.class, ex -> log.error("WriteTimeout"))
				.subscribeOn(scheduler).publishOn(scheduler);

		// verifyCalledUrl("/products/?name=AndroidPhone&color=black&deliveryDate=13/04/2019");
	}

	/**
	 * @return
	 * 
	 */
	public Flux<Product> getProductByArrayParameters() {

		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/products/")
						.queryParam("category", String.join(",", "Phones", "Tablets")).build())
				.retrieve().bodyToFlux(Product.class).timeout(Duration.ofSeconds(5))
				.onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
				.onErrorReturn(SslHandshakeTimeoutException.class, new Product("SslHandshakeTimeout"))
				.doOnError(WriteTimeoutException.class, ex -> log.error("WriteTimeout"))
				.subscribeOn(scheduler).publishOn(scheduler);

		// verifyCalledUrl("/products/?category=Phones,Tablets");
	}

}
