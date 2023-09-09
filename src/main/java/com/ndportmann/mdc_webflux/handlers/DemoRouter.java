/**
 * 
 */
package com.ndportmann.mdc_webflux.handlers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.http.MediaType.*;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Configuration
public class DemoRouter {

	@Bean
	RouterFunction<ServerResponse> routes(@Autowired DemoHandler demoHandler) {

		return RouterFunctions.route(GET("/v1/test").and(accept(APPLICATION_JSON)), demoHandler::getMessage)
				.andRoute(GET("/v1/test/g").and(accept(APPLICATION_JSON)), demoHandler::getGlobalMessage)
				.andRoute(GET("/v1/users").and(accept(APPLICATION_JSON, APPLICATION_XML)), demoHandler::getPersons)
				.andRoute(GET("/v1/users/{id}").and(accept(APPLICATION_JSON, APPLICATION_XML)), demoHandler::getPerson)
				.andRoute(POST("/v1/users").and(accept(APPLICATION_JSON, APPLICATION_XML)), demoHandler::createPerson);
	}
}
