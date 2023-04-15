package io.linkedlogics.redis.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.service.QueueService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.redis.service.RedisServiceConfigurer;
import redis.embedded.RedisServer;

public class QueueServiceTests {
	private static final String QUEUE = "q1";
	
	private static RedisServer redisServer;
	
	@BeforeAll
	public static void setUp() {
		redisServer = new RedisServer(6370);
		redisServer.start();
		LinkedLogics.configure(new RedisServiceConfigurer());
	}
	
	@AfterAll
	public static void cleanUp() {
		if (redisServer != null)
			redisServer.stop();
	}
	
	@Test
	public void shouldOfferAndConsume() {
		QueueService queueService = ServiceLocator.getInstance().getService(QueueService.class);
		
		queueService.offer(QUEUE, "hello");
		
		Optional<String> message = queueService.poll(QUEUE);
		assertThat(message).isPresent();
		assertThat(message.get()).isEqualTo("hello");
		
		message = queueService.poll(QUEUE);
		assertThat(message).isEmpty();
	}
}
