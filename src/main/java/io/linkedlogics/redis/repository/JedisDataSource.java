package io.linkedlogics.redis.repository;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.linkedlogics.config.LinkedLogicsConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JedisDataSource {
	private static final String REDIS = "redis";
	
	private static StringRedisTemplate redisTemplate;

	static {
		String redisHost = getRedisConfig("host").map(c -> c.toString()).orElseThrow(() -> new IllegalArgumentException("missing configuration " + REDIS + ".host"));
		int redisPort = getRedisConfig("port").map(c -> (Integer) c).orElseThrow(() -> new IllegalArgumentException("missing configuration " + REDIS + ".port"));
		log.info("connecting to redis@{}:{}", redisHost, redisPort);
		
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisHost);
		redisStandaloneConfiguration.setPort(redisPort);
//		redisStandaloneConfiguration.setPassword(RedisPassword.of(getRedisConfig("password").map(c -> c.toString()).orElseThrow(() -> new IllegalArgumentException("missing configuration " + REDIS + ".password"))));

		JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
		jedisClientConfiguration.connectTimeout(Duration.ofMillis(getRedisConfig("timeout").map(c -> (Integer) c).orElseThrow(() -> new IllegalArgumentException("missing configuration " + REDIS + ".timeout"))));
		jedisClientConfiguration.usePooling();
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
		jedisConnectionFactory.afterPropertiesSet();
		
		redisTemplate = new StringRedisTemplate();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		redisTemplate.afterPropertiesSet();
	}

	public static StringRedisTemplate getRedisTemplate() {
		return redisTemplate;
	}
	
	private static Optional<Object> getRedisConfig(String config) {
		return LinkedLogicsConfiguration.getConfig(REDIS + "." + config);
	}
}
