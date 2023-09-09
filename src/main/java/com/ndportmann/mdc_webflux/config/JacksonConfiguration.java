/**
 * 
 */
package com.ndportmann.mdc_webflux.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @author Gbenga
 *
 */
@Configuration
public class JacksonConfiguration {

	@Bean
	Jackson2JsonEncoder jackson2JsonEncoder(ObjectMapper mapper) {
		return new Jackson2JsonEncoder(mapper);
	}

	@Bean
	Jackson2JsonDecoder jackson2JsonDecoder(ObjectMapper mapper) {
		return new Jackson2JsonDecoder(mapper);
	}

	@Bean
	WebFluxConfigurer webFluxConfigurer(Jackson2JsonEncoder encoder, Jackson2JsonDecoder decoder) {
		return new WebFluxConfigurer() {
			@Override
			public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
				configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024);

				configurer.defaultCodecs().jackson2JsonEncoder(encoder);
				configurer.defaultCodecs().jackson2JsonDecoder(decoder);
			}
		};
	}

}