package com.ndportmann.mdc_webflux;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import redis.embedded.RedisServer;

/**
 * 
 * @author Gbenga
 *
 */
@Log4j2
@EnableWebFlux
@EnableHypermediaSupport(type = HypermediaType.HAL)
@SpringBootApplication
public class MdcWebfluxApplication {

	private RedisServer redisServer;
	
	static {
		System.setProperty("log4j2.isThreadContextMapInheritable", "true");

		// To be used for Illustrations only
		String key = "message";
		Mono<String> r = Mono.just("Hello").flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + " " + ctx.get(key))))
				.contextWrite(ctx -> ctx.put(key, "World"));

		// StepVerifier.create(r).expectNext("Hello World").verifyComplete();
	}

	public static void main(String[] args) {
		SpringApplication.run(MdcWebfluxApplication.class, args);
	}

	@PostConstruct
	public void startRedis() throws IOException {
		redisServer = new RedisServer(6379);
		
		log.info("Starting Redis Server at port {}", 6379);
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		log.info("Stopping Redis Server at port {}", 6379);
		
		redisServer.stop();
	}

}
