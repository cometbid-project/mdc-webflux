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
public class SingleResourceRetrievedEvent extends ApplicationEvent {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -8523497891662132145L;
	
	private final ServerWebExchange serverWebExchange;

    public SingleResourceRetrievedEvent(final Object source, final ServerWebExchange serverWebExchange) {
        super(source);

        this.serverWebExchange = serverWebExchange;
    }

    // API
    public ServerWebExchange getWebExchange() {
        return serverWebExchange;
    }

}