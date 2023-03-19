package dev.linkedlogics.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.PublisherService;
import dev.linkedlogics.service.QueueService;
import dev.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisPublisherService implements PublisherService {
	
	@Override
	public void publish(Context context) {
		ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
		QueueService queueService = ServiceLocator.getInstance().getQueueService();
		try {
			queueService.offer(context.getApplication(), mapper.writeValueAsString(context));
		} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
