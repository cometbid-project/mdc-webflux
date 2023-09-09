/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.events;

import java.io.Serializable;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
/**
 * @author Gbenga
 *
 */
public final class PaginatedResultsRetrievedEvent<T extends Serializable> extends ApplicationEvent {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -494067998741956585L;
	
	private final UriComponentsBuilder uriBuilder;
    private final ServerWebExchange webExchange;
    private final int page;
    private final int totalPages;
    private final int pageSize;

    public PaginatedResultsRetrievedEvent(final Class<T> clazz, final UriComponentsBuilder uriBuilderToSet, final ServerWebExchange webExchange, final int pageToSet, final int totalPagesToSet, final int pageSizeToSet) {
        super(clazz);

        uriBuilder = uriBuilderToSet;
        this.webExchange = webExchange;
        page = pageToSet;
        totalPages = totalPagesToSet;
        pageSize = pageSizeToSet;
    }

    // API

    public final UriComponentsBuilder getUriBuilder() {
        return uriBuilder;
    }

    public final ServerWebExchange getWebExchange() {
        return this.webExchange;
    }

    public final int getPage() {
        return page;
    }

    public final int getTotalPages() {
        return totalPages;
    }

    public final int getPageSize() {
        return pageSize;
    }

    /**
     * The object on which the Event initially occurred.
     * 
     * @return The object on which the Event initially occurred.
     */
    @SuppressWarnings("unchecked")
    public final Class<T> getClazz() {
        return (Class<T>) getSource();
    }

}
