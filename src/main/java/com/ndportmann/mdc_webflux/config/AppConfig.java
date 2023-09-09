/**
 * 
 */
package com.ndportmann.mdc_webflux.config;

import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import com.ndportmann.mdc_webflux.orm.jpa.InMemoryUniqueIdGenerator;
import com.ndportmann.mdc_webflux.orm.jpa.UniqueIdGenerator;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Configuration
@RequiredArgsConstructor
public class AppConfig implements ApplicationContextAware, WebFluxConfigurer {

	private ApplicationContext context;
	// Environment env;

	@Override
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

	@Bean
	public UniqueIdGenerator<UUID> uniqueIdGenerator() {
		return new InMemoryUniqueIdGenerator();
	}

	@Bean
	public SecretGenerator secretGenerator() {
		return new DefaultSecretGenerator(64);
	}

	@Bean
	public QrGenerator qrGenerator() {
		return new ZxingPngQrGenerator();
	}

	@Bean
	public CodeVerifier codeVerifier() {
		return new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());
	}

}
