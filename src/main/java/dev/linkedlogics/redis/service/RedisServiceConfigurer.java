package dev.linkedlogics.redis.service;

import dev.linkedlogics.service.ServiceConfigurer;
import dev.linkedlogics.service.local.QueueSchedulerService;

public class RedisServiceConfigurer extends ServiceConfigurer {
	public RedisServiceConfigurer() {
		configure(new RedisContextService());
		configure(new RedisQueueService());
		configure(new RedisTopicService());
		configure(new QueueSchedulerService());
		configure(new RedisTriggerService());
	}
}
