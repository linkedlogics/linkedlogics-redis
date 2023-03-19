package dev.linkedlogics.redis.service;

import java.util.Optional;

import dev.linkedlogics.redis.repository.QueueRepository;
import dev.linkedlogics.service.QueueService;

public class RedisQueueService implements QueueService {
	private QueueRepository repository;
	
	public RedisQueueService() {
		this.repository = new QueueRepository();
	}
	
	public void offer(String queue, String payload) {
		repository.offer(queue, payload);
	}
	
	public Optional<String> poll(String queue) {
		return repository.poll(queue);
	}
}
