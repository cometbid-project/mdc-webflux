/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.listeners;

import java.net.URI;

import org.springframework.context.ApplicationListener;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriTemplate;

import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;
import com.ndportmann.mdc_webflux.hateoas.events.SingleResourceRetrievedEvent;
import com.ndportmann.mdc_webflux.helpers.LinkUtil;

import lombok.extern.log4j.Log4j2;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Component
class SingleResourceRetrievedDiscoverabilityListener implements ApplicationListener<SingleResourceRetrievedEvent> {

	@Override
	public void onApplicationEvent(final SingleResourceRetrievedEvent resourceRetrievedEvent) {
		Preconditions.checkNotNull(resourceRetrievedEvent);

		final ServerWebExchange serverWebExchange = resourceRetrievedEvent.getWebExchange();
		addLinkHeaderOnSingleResourceRetrieval(serverWebExchange);
	}

	void addLinkHeaderOnSingleResourceRetrieval(final ServerWebExchange webExchange) {
		final String requestURL = webExchange.getRequest().getURI().toASCIIString();

		// final String requestURL =
		// ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().toASCIIString();
		final int positionOfLastSlash = requestURL.lastIndexOf("/");
		final String uriForResourceCreation = requestURL.substring(0, positionOfLastSlash);

		log.info("URI Resource {}", uriForResourceCreation);
		
		final String linkHeaderValue = LinkUtil.createLinkHeader(uriForResourceCreation, "collection");
		log.info("URI Link Header {}", linkHeaderValue);
		
		webExchange.getResponse().getHeaders().add(HttpHeaders.LINK, linkHeaderValue);
		// response.addHeader(HttpHeaders.LINK, linkHeaderValue);
	}

}
