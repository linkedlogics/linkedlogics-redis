package io.linkedlogics.redis.repository;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueRepository extends JedisRepository {
	private static final String QUEUE = "queue:";

	public void offer(String queue, String payload) {
		redisTemplate.opsForList().rightPush(getKey(queue), payload);
	}
	
	public Optional<String> poll(String queue) {
		try {
			return Optional.ofNullable(redisTemplate.opsForList().leftPop(getKey(queue)));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			return Optional.empty();
		}
	}

	private String getKey(String queue) {
		return QUEUE + queue;
	}
}
