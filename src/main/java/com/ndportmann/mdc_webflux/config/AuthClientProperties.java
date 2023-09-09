/**
 * 
 */
package com.ndportmann.mdc_webflux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

/**
 * @author Gbenga
 *
 */
@Getter
@Configuration
@ConfigurationProperties
@PropertySource(value = { "classpath:api_config.properties", "classpath:uri_path.properties" })
public class AuthClientProperties {

	@Value("${oauth2.client.id}")
	private String oauthClientId;

	@Value("${user.service.uri}")
	private String userServiceUri;

	@Value("${merchant.service.uri}")
	private String merchantServiceUri;
	
	@Value("${uri.product.base-url}")
	private String productServiceBaseUrl;
	
	@Value("${employee.server.host}")
	private String employeeHost;

	@Value("${user.service.name}")
	private String userServiceName;

	@Value("${merchant.service.name}")
	private String merchantServiceName;

	@Value("${create.user.endpoint}")
	private String createUserEndpoint;

	@Value("${create.merchant.endpoint}")
	private String createMerchantEndpoint;

	@Value("${auth.connect.timeout}")
	private Integer connectTimeout;

	@Value("${auth.read.timeout}")
	private Integer readTimeout;

	@Value("${auth.write.timeout}")
	private Integer writeTimeout;

	@Value("${auth.request.timeout}")
	private Integer requestTimeout;
	
	@Value("${retries.perFailedTransaction}")
	private Integer maxRetriesPerTransaction;

	@Value("${retries.backoffFailedTransactionInMillis}")
	private Long backOffPeriodInMillis;

	/*
	 * @Value("${server.ssl.trust-store}") private String
	 * clientSslTrustStoreClassPath;
	 * 
	 * @Value("${server.ssl.trust-store-password}") private String trustStorePass;
	 * 
	 * @Value("${server.ssl.key-store}") private String clientSslKeyStoreClassPath;
	 * 
	 * @Value("${server.ssl.key-store-password}") private String
	 * clientSslKeyStorePassword;
	 * 
	 * @Value("${server.ssl.key-alias}") String keyAlias;
	 */
}
