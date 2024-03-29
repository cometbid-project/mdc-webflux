/**
 * 
 */
package com.ndportmann.mdc_webflux.helpers;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Configuration
public class MessageConfig extends DelegatingWebFluxConfiguration {

	@PostConstruct
	private void init() {
		// Locale.setDefault(Locale.ENGLISH);

		System.setProperty("isThreadContextMapInheritable", "true");
	}

	@Bean(name = "messageSource")
	public MessageSource bundleMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		// messageSource.setBasename("classpath:locale/messages");

		messageSource.setBasenames("classpath:messages/business/messages",
				"classpath:messages/validation/validationMessages");

		// messageSource.setBasenames("classpath:messages",
		// "https://<configserverUrl>/myapp/default/master/messages");
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}

	@Bean
	@Primary
	public LocalValidatorFactoryBean getValidator(MessageSource messageSource) {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.setValidationMessageSource(messageSource);
		return localValidatorFactoryBean;
	}

}
