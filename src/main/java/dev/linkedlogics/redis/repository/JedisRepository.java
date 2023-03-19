package dev.linkedlogics.redis.repository;

import org.springframework.data.redis.core.StringRedisTemplate;

public abstract class JedisRepository {
	protected StringRedisTemplate redisTemplate;
	
	public JedisRepository() {
		this.redisTemplate = JedisDataSource.getRedisTemplate();
	}
}