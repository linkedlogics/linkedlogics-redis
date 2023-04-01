package dev.linkedlogics.redis.repository;

import java.util.Optional;

import org.springframework.dao.OptimisticLockingFailureException;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.ServiceLocator;

public class ContextRepository extends JedisRepository {
	private static final String CONTEXT = "context:";
	
	public void create(Context context) throws Exception {
		redisTemplate.opsForValue().set(getKey(context), getValue(context));
	}

	public void update(Context context) throws Exception {
		String currentValue = redisTemplate.opsForValue().get(getKey(context));
		if (currentValue != null && !currentValue.isEmpty()) {
			if (getValue(currentValue).getVersion() <= context.getVersion()) {
				redisTemplate.opsForValue().set(getKey(context), getValue(context));
			}
		} else {
			redisTemplate.opsForValue().set(getKey(context), getValue(context));
		}
	}

	public Optional<Context> get(String id) throws Exception {
		String currentValue = redisTemplate.opsForValue().get(getKey(id));
		if (currentValue != null && !currentValue.isEmpty()) {
			return Optional.of(getValue(currentValue));
		} 
		return Optional.empty();
	}

	public void delete(String id, int version) throws Exception {
		String currentValue = redisTemplate.opsForValue().get(getKey(id));
		if (currentValue != null && !currentValue.isEmpty()) {
			if (getValue(currentValue).getVersion() <= version) {
				redisTemplate.delete(getKey(id));
			}
		}
	}
	
	private String getKey(Context context) {
		return CONTEXT + context.getId();
	}
	
	private String getKey(String id) {
		return CONTEXT + id;
	}
	
	private String getValue(Context context) throws Exception {
		return ServiceLocator.getInstance().getMapperService().getMapper().writeValueAsString(context);
	}
	
	private Context getValue(String context) throws Exception {
		return ServiceLocator.getInstance().getMapperService().getMapper().readValue(context, Context.class);
	}
}
