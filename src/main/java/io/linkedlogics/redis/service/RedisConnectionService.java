package io.linkedlogics.redis.service;

import java.time.Duration;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.linkedlogics.redis.service.config.RedisConnectionServiceConfig;
import io.linkedlogics.service.ConfigurableService;
import io.linkedlogics.service.LinkedLogicsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisConnectionService extends ConfigurableService<RedisConnectionServiceConfig> implements LinkedLogicsService {
	private static StringRedisTemplate redisTemplate;
	
	public RedisConnectionService() {
		super(RedisConnectionServiceConfig.class);
	}
	
	public StringRedisTemplate getRedisTemplate() {
		if (redisTemplate != null) {
			return redisTemplate;
		} else {
			synchronized (RedisConnectionService.class) {
				if (redisTemplate != null) {
					return redisTemplate;
				} else {
					redisTemplate = initRedisTemplate();
					return redisTemplate;
				}
			}
		}
	}
	
	private StringRedisTemplate initRedisTemplate() {
		log.info("connecting to redis@{}:{}", getConfig().getHost(), getConfig().getPort());
		
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(getConfig().getHost());
		redisStandaloneConfiguration.setPort(getConfig().getPort());
		getConfig().getPassword().ifPresent(redisStandaloneConfiguration::setPassword);

		JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
		getConfig().getTimeout().map(t -> Duration.ofMillis(t)).ifPresent(jedisClientConfiguration::connectTimeout);

		jedisClientConfiguration.usePooling();
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
		jedisConnectionFactory.afterPropertiesSet();
		
		StringRedisTemplate redisTemplate = new StringRedisTemplate();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		redisTemplate.afterPropertiesSet();
		
		return redisTemplate;
	}
}
