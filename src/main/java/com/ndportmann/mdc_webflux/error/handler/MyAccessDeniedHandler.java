/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ndportmann.mdc_webflux.error.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.Objects;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *
 * @author Gbenga
 */
@Log4j2
// handle 403 page
@Component
public class MyAccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        
        ReactiveSecurityContextHolder.getContext().filter(c -> Objects.nonNull(c.getAuthentication()))
                .map(s -> {
                    Authentication auth = s.getAuthentication();
                    log.info("User '" + auth.getName()
                            + "' attempted to access the protected URL: "
                            + exchange.getRequest().getURI());

                    return auth;
                });

        ServerResponse.permanentRedirect(URI.create("/403")).build();

        return Mono.empty();

    }

}
