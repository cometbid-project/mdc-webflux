/**
 * 
 */
package com.ndportmann.mdc_webflux.filters;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest.Builder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class QueryParameterWebFilter implements WebFilter {

	private HttpWebHandlerAdapter httpWebHandlerAdapter;
	private final ApplicationContext applicationContext;
	
	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		HttpHandler delegate = WebHttpHandlerBuilder.applicationContext(applicationContext).build();
		httpWebHandlerAdapter = (HttpWebHandlerAdapter) delegate;
	}

	@Override
	public Mono<Void> filter(final ServerWebExchange serverWebExchange, final WebFilterChain chain) {
		
		final ServerWebExchange localizedExchange = getLocalizedServerWebExchange(serverWebExchange,
				configureMediaResponseFormat(serverWebExchange));

		//modifyResponse(serverWebExchange);
		return chain.filter(localizedExchange);
	}
	
	private void modifyResponse(ServerWebExchange serverWebExchange) {
		serverWebExchange.getResponse()
        .getHeaders().add("web-filter", "web-filter-test");
	}

	private ServerWebExchange getLocalizedServerWebExchange(final ServerWebExchange serverWebExchange,
			List<MediaType> mediaTypelist) {

		final Builder requestBuilder = serverWebExchange.getRequest().mutate().headers(h -> { 
			h.setAccept(mediaTypelist);
		});
		
		if (mediaTypelist.contains(MediaTypes.HAL_JSON)) {
			requestBuilder.path(serverWebExchange.getRequest().getPath() + "/hateoas");
		}
		
		final ServerHttpRequest httpRequest = requestBuilder.build();

		return new DefaultServerWebExchange(httpRequest, serverWebExchange.getResponse(),
				httpWebHandlerAdapter.getSessionManager(), httpWebHandlerAdapter.getCodecConfigurer(),
				httpWebHandlerAdapter.getLocaleContextResolver());
	}

	private List<MediaType> configureMediaResponseFormat(ServerWebExchange serverWebExchange) {
		final ServerHttpRequest request = serverWebExchange.getRequest();

		String responseFormat = request.getQueryParams().getFirst("format");
		log.info("User Presented Accept Header format ...{}", responseFormat);

		MediaType mediaType = null;
		if (StringUtils.isNotBlank(responseFormat)) {
			log.info("Request for format(Json, XML or Hal-Json) detected ...");

			if (responseFormat.equalsIgnoreCase("xml")) {
				mediaType = MediaType.APPLICATION_XML;
			} else if (responseFormat.equalsIgnoreCase("json")) {
				mediaType = MediaType.APPLICATION_JSON;
			} else if (responseFormat.equalsIgnoreCase("hal")) {
				mediaType = MediaTypes.HAL_JSON;
			}
		}

		List<MediaType> acceptMediaTpes = Optional.ofNullable(mediaType).map(l -> Collections.singletonList(l))
				.orElse(queryRequestHeader(serverWebExchange));

		return acceptMediaTpes;
	}

	private List<MediaType> queryRequestHeader(final ServerWebExchange serverWebExchange) {
		final ServerHttpRequest request = serverWebExchange.getRequest();
		HttpHeaders headers = request.getHeaders();

		List<MediaType> reqFormatCollection = null;
		// Check the Accept MediaType header for supported format
		if (headers.getAccept().isEmpty()) {
			log.info("Accept MediaType header is empty ...");

			reqFormatCollection = Collections.singletonList(MediaType.APPLICATION_JSON);

		} else {
			log.info("Accept MediaType header is found ...");
			reqFormatCollection = headers.getAccept();
		}

		return reqFormatCollection;
	}

}
