/**
 * 
 */
package com.ndportmann.mdc_webflux.filters;

import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
public class WebHandlerFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

	@Override
	public Mono<ServerResponse> filter(ServerRequest serverRequest, HandlerFunction<ServerResponse> handlerFunction) {

		if (serverRequest.pathVariable("name").equalsIgnoreCase("test")) {
			return ServerResponse.status(FORBIDDEN).build();
		}
		return handlerFunction.handle(serverRequest);
	}

}
