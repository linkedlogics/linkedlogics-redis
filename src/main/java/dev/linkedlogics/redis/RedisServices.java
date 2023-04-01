package dev.linkedlogics.redis;

import java.util.List;

import dev.linkedlogics.redis.service.RedisContextService;
import dev.linkedlogics.redis.service.RedisProcessService;
import dev.linkedlogics.redis.service.RedisQueueService;
import dev.linkedlogics.redis.service.RedisTopicService;
import dev.linkedlogics.redis.service.RedisTriggerService;
import dev.linkedlogics.service.LinkedLogicsService;
import dev.linkedlogics.service.ServiceProvider;
import dev.linkedlogics.service.local.QueueSchedulerService;

public class RedisServices extends ServiceProvider {
	@Override
	public List<LinkedLogicsService> getMessagingServices() {
		return List.of(new RedisQueueService(), new RedisTopicService());
	}

	@Override
	public List<LinkedLogicsService> getSchedulingServices() {
		return List.of(new QueueSchedulerService());
	}

	@Override
	public List<LinkedLogicsService> getStoringServices() {
		return List.of(new RedisContextService(), new RedisTriggerService(), new RedisProcessService());
	}
}
