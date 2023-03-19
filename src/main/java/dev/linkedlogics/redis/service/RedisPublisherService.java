package dev.linkedlogics.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.redis.repository.QueueRepository;
import dev.linkedlogics.service.PublisherService;
import dev.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisPublisherService implements PublisherService {
	private QueueRepository repository;
	
	public RedisPublisherService() {
		this.repository = new QueueRepository();
	}
	
	@Override
	public void publish(Context context) {
		ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
		try {
			repository.offer(context.getApplication(), mapper.writeValueAsString(context));
		} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
