package dev.linkedlogics.redis.service;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.redis.repository.ContextRepository;
import dev.linkedlogics.service.ContextService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisContextService implements ContextService {
	private ContextRepository repository;
	
	public RedisContextService() {
		this.repository = new ContextRepository();
	}
	
	@Override
	public Optional<Context> get(String id) {
		try {
			return repository.get(id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<Context> remove(String id) {
		Optional<Context> context = get(id);
		context.ifPresent(c -> {
			try {
				repository.delete(c.getId(), c.getVersion());
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(), e);
			}
		});
		return context;
	}

	@Override
	public void set(Context context) {
		try {
			if (context.getStatus() == Status.INITIAL) {
				repository.create(context);
			} else {
				repository.update(context);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
