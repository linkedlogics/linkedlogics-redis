package dev.linkedlogics.redis.service;

import dev.linkedlogics.service.ServiceConfigurer;

public class RedisServiceConfigurer extends ServiceConfigurer {
	public RedisServiceConfigurer() {
		configure(new RedisContextService());
//		configure(new JdbcQueueService());
//		configure(new JdbcTopicService());
		configure(new RedisConsumerService());
		configure(new RedisPublisherService());
//		configure(new JdbcSchedulerService());
		configure(new RedisTriggerService());
	}
}
