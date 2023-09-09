/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.events;

import org.springframework.context.ApplicationEvent;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Gbenga
 *
 */
public class ResourceCreatedEvent extends ApplicationEvent {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 2462971511814720945L;
	
	private final ServerWebExchange webExchange;
    private final String idOfNewResource;

    public ResourceCreatedEvent(final Object source, final ServerWebExchange webExchange, final String string) {
        super(source);

        this.webExchange = webExchange;
        this.idOfNewResource = string;
    }

    // API

    public ServerWebExchange getWebExchange() {
        return webExchange;
    }

    public String getIdOfNewResource() {
        return idOfNewResource;
    }

}
