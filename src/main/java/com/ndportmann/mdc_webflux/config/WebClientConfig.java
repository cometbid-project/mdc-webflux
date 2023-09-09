/**
 * 
 */
package com.ndportmann.mdc_webflux.config;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;

import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.ndportmann.mdc_webflux.helpers.ApiPaths;
import com.ndportmann.mdc_webflux.filters.LogFilters;
import com.ndportmann.mdc_webflux.filters.WebClientFilters;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

/**
 * @author Gbenga
 *
 */
@Log4j2
@Configuration
@RequiredArgsConstructor
@Profile("dev")
//@EnableConfigurationProperties(AuthClientProperties.class)
public class WebClientConfig {

	private final AuthClientProperties clientProperties;
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel MacOS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36";
	
	@Bean("productWebClient")
	public WebClient productWebClient(WebClient.Builder webClientBuilder) throws SSLException {
		String BASE_URL = clientProperties.getProductServiceBaseUrl();

		DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(BASE_URL);
		uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);

		webClientBuilder.filter(LogFilters.logRequest());
		webClientBuilder.filter(LogFilters.logResponse());
		webClientBuilder.filter(WebClientFilters.tracingFilter());
		webClientBuilder.filter(WebClientFilters.retryFilter());

		webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		webClientBuilder.defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT);

		webClientBuilder.exchangeStrategies(ExchangeStrategies.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(30 * 1024 * 1024)).build());

		return webClientBuilder.clone().uriBuilderFactory(uriBuilderFactory).baseUrl(BASE_URL)
				.clientConnector(getConnector()).build();
	}

	@Bean("employeeWebClient")
	public WebClient employeeWebClient(WebClient.Builder webClientBuilder) throws SSLException {

		webClientBuilder.filter(LogFilters.logRequest());
		webClientBuilder.filter(LogFilters.logResponse());
		webClientBuilder.filter(WebClientFilters.tracingFilter());
		webClientBuilder.filter(WebClientFilters.retryFilter());

		webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		webClientBuilder.defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT);

		webClientBuilder.exchangeStrategies(ExchangeStrategies.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(30 * 1024 * 1024)).build());

		return webClientBuilder.clone().clientConnector(getConnector()).build();
	}

	/**
	 * We configured the Response timeout to 1 second We configured the Connection
	 * timeout for 10 seconds We enabled keep-alive checks to probe after 5 minutes
	 * of being idle, at 60 seconds intervals. We also set the maximum number of
	 * probes before the connection dropping to 8 When the connection is not
	 * established in a given time or dropped, a ConnectTimeoutException is thrown.
	 * 
	 * read timeout occurs when no data was read within a certain period of
	 * time(ReadTimeoutException). write timeout when a write operation cannot
	 * finish at a specific time(WriteTimeoutException)
	 * 
	 * @return
	 * @throws SSLException
	 */
	@Bean("webClientOauth")
	public WebClient webclientWithOauth2(WebClient.Builder webClientBuilder,
			ReactiveClientRegistrationRepository clientRegistrations,
			ServerOAuth2AuthorizedClientRepository authorizedClients) throws SSLException {

		String clientId = clientProperties.getOauthClientId();

		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrations, authorizedClients); // (optional) explicitly opt into using the oauth2Login to
															// provide an access
		// token implicitly
		oauth2Client.setDefaultOAuth2AuthorizedClient(true); // (optional) set a default
																// ClientRegistration.registrationId
		oauth2Client.setDefaultClientRegistrationId(clientId);

		webClientBuilder.filter(LogFilters.logRequest());
		webClientBuilder.filter(LogFilters.logResponse());
		webClientBuilder.filter(WebClientFilters.tracingFilter());

		webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		return webClientBuilder.clone().filter(oauth2Client).clientConnector(getNoSSLConnector()).build();
	}

	private ReactorClientHttpConnector getNoSSLConnector() throws SSLException {

		HttpClient httpClient = HttpClient.create().noSSL();

		return new ReactorClientHttpConnector(httpClient);
	}

	private ReactorClientHttpConnector getConnector() throws SSLException {

		final ConnectionProvider theTcpClientPool = ConnectionProvider.create("tcp-client-pool"); // default pool size
		// 500
		final LoopResources theTcpClientLoopResources = LoopResources.create("tcp-client-loop", 100, true);

		HttpClient httpClient = HttpClient.create(theTcpClientPool).compress(true)
				.secure(sslContextSpec -> sslContextSpec.sslContext(noSecureSSL())
						.handshakeTimeout(Duration.ofSeconds(30)).closeNotifyFlushTimeout(Duration.ofSeconds(10))
						.closeNotifyReadTimeout(Duration.ofSeconds(10)))
				// configure a response timeout
				.responseTimeout(Duration.ofSeconds(1))
				// .proxy(spec ->
				// spec.type(ProxyProvider.Proxy.HTTP).host("proxy").port(8080).connectTimeoutMillis(30000))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientProperties.getConnectTimeout())
				.option(ChannelOption.SO_TIMEOUT, clientProperties.getRequestTimeout()) // Socket Timeout
				.option(ChannelOption.SO_KEEPALIVE, true).option(EpollChannelOption.TCP_KEEPIDLE, 300)
				.option(EpollChannelOption.TCP_KEEPINTVL, 60).option(EpollChannelOption.TCP_KEEPCNT, 8)
				.runOn(theTcpClientLoopResources).doOnConnected(connection -> {
					// set the read and write timeouts
					connection.addHandlerLast(
							new ReadTimeoutHandler(clientProperties.getReadTimeout(), TimeUnit.MILLISECONDS));
					connection.addHandlerLast(
							new WriteTimeoutHandler(clientProperties.getWriteTimeout(), TimeUnit.MILLISECONDS));
				});

		// ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

		return new ReactorClientHttpConnector(httpClient);
	}

	private SslContext noSecureSSL() {

		try {
			return SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	private SslContext getTwoWaySecureSslContext() {
		/*
		 * String clientSslKeyStoreClassPath =
		 * authClientProperties.getClientSslKeyStoreClassPath(); String
		 * clientSslTrustStoreClassPath =
		 * authClientProperties.getClientSslTrustStoreClassPath(); String
		 * clientSslKeyStorePassword =
		 * authClientProperties.getClientSslKeyStorePassword(); String
		 * clientSslTrustStorePassword = authClientProperties.getTrustStorePass();
		 */
		String clientSslKeyStoreClassPath = null;
		String clientSslTrustStoreClassPath = null;
		String clientSslKeyStorePassword = null;
		String clientSslTrustStorePassword = null;

		try (FileInputStream keyStoreFileInputStream = new FileInputStream(
				ResourceUtils.getFile(clientSslKeyStoreClassPath));
				FileInputStream trustStoreFileInputStream = new FileInputStream(
						ResourceUtils.getFile(clientSslTrustStoreClassPath));) {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(keyStoreFileInputStream, clientSslKeyStorePassword.toCharArray());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(keyStore, clientSslKeyStorePassword.toCharArray());

			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(trustStoreFileInputStream, clientSslTrustStorePassword.toCharArray());
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
			trustManagerFactory.init(trustStore);

			return SslContextBuilder.forClient().keyManager(keyManagerFactory).trustManager(trustManagerFactory)
					.build();

		} catch (Exception e) {
			log.error("An error has occurred: ", e);

			throw new RuntimeException();
		}
	}

	@Bean("clientCredentialFlow")
	WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations,
			ReactiveOAuth2AuthorizedClientService authorizedClients) {
		String clientId = clientProperties.getOauthClientId();

		AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager clientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
				clientRegistrations, authorizedClients);

		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientManager);
		oauth.setDefaultClientRegistrationId(clientId);

		WebClient.Builder webClientBuilder = WebClient.builder();
		webClientBuilder.filter(LogFilters.logRequest());
		webClientBuilder.filter(LogFilters.logResponse());
		webClientBuilder.filter(WebClientFilters.tracingFilter());
		webClientBuilder.filter(oauth);

		return webClientBuilder.build();
	}

}
