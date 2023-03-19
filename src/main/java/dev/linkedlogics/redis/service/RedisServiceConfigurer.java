package dev.linkedlogics.redis.service;

import dev.linkedlogics.service.ServiceConfigurer;

public class RedisServiceConfigurer extends ServiceConfigurer {
	public RedisServiceConfigurer() {
		configure(new RedisContextService());
		configure(new RedisQueueService());
		configure(new RedisTopicService());
		configure(new RedisConsumerService());
		configure(new RedisPublisherService());
		configure(new RedisSchedulerService());
		configure(new RedisTriggerService());
	}
}
