/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.listeners;

import java.net.URI;

import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import com.google.common.base.Preconditions;
import com.ndportmann.mdc_webflux.hateoas.events.ResourceCreatedEvent;

import lombok.extern.log4j.Log4j2;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Component
class ResourceCreatedDiscoverabilityListener implements ApplicationListener<ResourceCreatedEvent> {

    @Override
    public void onApplicationEvent(final ResourceCreatedEvent resourceCreatedEvent) {
        Preconditions.checkNotNull(resourceCreatedEvent);

        final ServerWebExchange webExchange = resourceCreatedEvent.getWebExchange();
        final String idOfNewResource = resourceCreatedEvent.getIdOfNewResource();

        addLinkHeaderOnResourceCreation(webExchange, idOfNewResource);
    }

    void addLinkHeaderOnResourceCreation(final ServerWebExchange webExchange, final String idOfNewResource) {
    	final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(webExchange.getRequest().getURI()).replaceQuery(null);
		URI uri = uriBuilder.path("/{id}").buildAndExpand(idOfNewResource).toUri();
		
        log.info("URI Location {}", uri.toString());
        webExchange.getResponse().getHeaders().add(HttpHeaders.LOCATION, uri.toASCIIString());
    }

}
