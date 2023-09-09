/**
 * 
 */
package com.ndportmann.mdc_webflux.hateoas.web.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriTemplate;

import com.ndportmann.mdc_webflux.helpers.LinkUtil;

/**
 * @author Gbenga
 *
 */
@RestController
@RequestMapping("/api/v1/")
public class RootController {

	// API discover

	@GetMapping("home")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void adminRoot(final ServerWebExchange serverWebExchange) {
		final String rootUri = serverWebExchange.getRequest().getURI().toString();

		final URI fooUri = new UriTemplate("{rootUri}{resource}").expand(rootUri, "foos");
		
		final String linkToFoos = LinkUtil.createLinkHeader(fooUri.toASCIIString(), "collection");
		serverWebExchange.getResponse().getHeaders().add("Link", linkToFoos);
	}

}