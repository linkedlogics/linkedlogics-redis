package dev.linkedlogics.redis.service;

import java.util.List;

import dev.linkedlogics.redis.repository.TriggerRepository;
import dev.linkedlogics.service.TriggerService;

public class RedisTriggerService implements TriggerService {
	private TriggerRepository repository;
	
	public RedisTriggerService() {
		this.repository = new TriggerRepository();
	}
	
	@Override
	public List<Trigger> get(String id) {
		return repository.get(id);
	}

	@Override
	public void set(String id, Trigger trigger) {
		repository.create(id, trigger);
	}
}
