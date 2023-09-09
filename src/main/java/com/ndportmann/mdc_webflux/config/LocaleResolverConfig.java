/**
 * 
 */
package com.ndportmann.mdc_webflux.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import com.ndportmann.mdc_webflux.helpers.LocaleContextUtils;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Configuration
public class LocaleResolverConfig {

	@Bean
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
	public HttpWebHandlerAdapter httpHandler(ApplicationContext applicationContext) {
		HttpHandler delegate = WebHttpHandlerBuilder.applicationContext(applicationContext).build();
		HttpWebHandlerAdapter httpWebHandlerAdapter = (HttpWebHandlerAdapter) delegate;

		return new HttpWebHandlerAdapter(httpWebHandlerAdapter) {
			@Override
			protected ServerWebExchange createExchange(ServerHttpRequest request, ServerHttpResponse response) {
				ServerWebExchange serverWebExchange = super.createExchange(request, response);

				List<Locale> localeColletion = configureLocale(serverWebExchange);

				final ServerHttpRequest httpRequest = request.mutate().headers(h -> {
					h.setAcceptLanguageAsLocales(localeColletion);
					// h.setAccept(mediaTypelist);
				}).build();

				return getLocalizedServerWebExchange(httpRequest, response, httpWebHandlerAdapter);
			}
		};
	}

	private List<Locale> configureLocale(ServerWebExchange serverWebExchange) {
		final ServerHttpRequest request = serverWebExchange.getRequest();

		// Check the Request Query Parameter ("locale")
		String queryParamLocale = request.getQueryParams().getFirst("locale");

		if (isBlank(queryParamLocale)) {
			// Check the Request Query Parameter ("lang")
			queryParamLocale = request.getQueryParams().getFirst("lang");

			log.info("lang detected {}", queryParamLocale);
		}

		List<Locale> reqLocaleCollection = Optional.ofNullable(queryParamLocale)
				.map(l -> Collections.singletonList(LocaleUtils.toLocale(l)))
				.orElse(queryRequestHeader(serverWebExchange));

		log.info("Locale detected {}", reqLocaleCollection);

		// *****Save User agents detected locale for global access***/
		ThreadContext.put(LocaleContextUtils.THREAD_CONTEXT_LOCALE_KEY, reqLocaleCollection.get(0).toString());

		return reqLocaleCollection;
	}

	private List<Locale> queryRequestHeader(final ServerWebExchange serverWebExchange) {
		final ServerHttpRequest request = serverWebExchange.getRequest();
		HttpHeaders headers = request.getHeaders();

		List<Locale> reqLocaleCollection = null;
		// Check the Accept language header for supported locale
		if (headers.getAcceptLanguage().isEmpty()) {
			log.info("Accept Language header is empty ...");

			reqLocaleCollection = Collections.singletonList(defaultLocale(serverWebExchange));

		} else {
			log.info("Accept Language header is found ...");
			reqLocaleCollection = headers.getAcceptLanguageAsLocales();
		}

		return reqLocaleCollection;
	}

	private Locale defaultLocale(ServerWebExchange serverWebExchange) {

		/*
		 * Check the request's locale or default to ENGLISH(en) if no locale parameter
		 * is detected
		 */
		Locale sweLocale = serverWebExchange.getLocaleContext().getLocale();
		return sweLocale != null ? sweLocale : Locale.ENGLISH;
	}

	private ServerWebExchange getLocalizedServerWebExchange(ServerHttpRequest httpRequest,
			ServerHttpResponse httpResponse, HttpWebHandlerAdapter httpWebHandlerAdapter) {

		return new DefaultServerWebExchange(httpRequest, httpResponse, httpWebHandlerAdapter.getSessionManager(),
				httpWebHandlerAdapter.getCodecConfigurer(), httpWebHandlerAdapter.getLocaleContextResolver());
	}
}
