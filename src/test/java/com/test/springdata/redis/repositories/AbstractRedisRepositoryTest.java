/**
 * 
 */
package com.test.springdata.redis.repositories;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;
import java.io.IOException;

import javax.annotation.PostConstruct;

/**
 * @author Gbenga
 *
 */
public abstract class AbstractRedisRepositoryTest {

	private static RedisServer REDISSERVER;

	@Autowired
	RedisConnectionFactory redisConnectionFactory;
	@Autowired
	private static @Value("${spring.redis.port}") int port;

	
	@BeforeAll
	static void startUpRedisServer() {
		REDISSERVER = new RedisServerBuilder().port(8850).setting("maxmemory 128M").build();
		REDISSERVER.start();
		
		//redisConnectionFactory.getConnection().flushAll();
	}

	@AfterAll
	static void shutDownRedisServer() {
		REDISSERVER.stop();
	}

}