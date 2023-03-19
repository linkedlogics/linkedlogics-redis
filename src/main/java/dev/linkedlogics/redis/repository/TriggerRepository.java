package dev.linkedlogics.redis.repository;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.TriggerService.Trigger;

public class TriggerRepository  extends JedisRepository {
	private static final String TRIGGER = "trigger:";

	public void create(String contextId, Trigger trigger) {
		redisTemplate.opsForList().leftPush(getKey(contextId), getValue(trigger));
	}
	
	public List<Trigger> get(String contextId) {
		List<String> list = redisTemplate.opsForList().range(getKey(contextId), 0, -1);
		redisTemplate.delete(getKey(contextId));
		return list.stream().map(this::getValue).collect(Collectors.toList());
	}
	
	private String getKey(String id) {
		return TRIGGER + id;
	}
	
	private String getValue(Trigger trigger) {
		try {
			return ServiceLocator.getInstance().getMapperService().getMapper().writeValueAsString(trigger);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Trigger getValue(String trigger) {
		try {
			return ServiceLocator.getInstance().getMapperService().getMapper().readValue(trigger, Trigger.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}