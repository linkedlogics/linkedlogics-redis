package dev.linkedlogics.redis.repository;

import java.util.Optional;

public class QueueRepository extends JedisRepository {
	private static final String QUEUE = "queue:";

	public void offer(String queue, String payload) {
		redisTemplate.opsForList().rightPush(getKey(queue), payload);
	}
	
	public Optional<String> poll(String queue) {
		return Optional.ofNullable(redisTemplate.opsForList().leftPop(getKey(queue)));
	}

	private String getKey(String queue) {
		return QUEUE + queue;
	}
}
