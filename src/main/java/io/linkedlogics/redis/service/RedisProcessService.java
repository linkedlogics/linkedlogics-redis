package io.linkedlogics.redis.service;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.service.local.LocalProcessService;
import io.linkedlogics.redis.repository.ProcessRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisProcessService extends LocalProcessService {
	private ScheduledExecutorService service;
	private ProcessRepository repository;

	public RedisProcessService() {
		this.repository = new ProcessRepository();
	}

	@Override
	public void start() {
		service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				refreshProcesses();
			}
		}, 5, 5, TimeUnit.MINUTES);
	}

	@Override
	public void stop() {
		if (service != null) {
			service.shutdownNow();
		}
	}

	@Override
	public Optional<ProcessDefinition> getProcess(String processId, int processVersion) {
		try {
			int version;
			if (processVersion == LATEST_VERSION) {
				version = repository.getMaxVersion(processId).map(Function.identity()).orElseThrow(() -> new RuntimeException("ASDAD"));
			} else {
				version = processVersion;
			}
			
			Optional<ProcessDefinition> process = Optional.ofNullable(definitions.get(getProcessKey(processId, version)));

			if (process.isEmpty()) {
				Optional<ProcessDefinition> newProcess = repository.get(processId, version);
				if (newProcess.isPresent()) {
					super.addProcess(newProcess.get());
					return Optional.of(newProcess.get());
				}
			} else {
				return process;
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return Optional.empty();
	}

	@Override
	protected void addProcess(ProcessDefinition process) {
		if (!process.isArchived()) {
			super.addProcess(process);
			try {
				repository.set(process);
				repository.setVersion(process.getId(), process.getVersion());
			} catch (Exception e) {
				throw new RuntimeException(String.format("unable to store process %s[%d] in redis", process.getId(), process.getVersion()), e);
			}
		} else {
			try {
				repository.deleteVersion(process.getId(), process.getVersion());
				repository.delete(process.getId(), process.getVersion());
			} catch (Exception e) {
				throw new RuntimeException(String.format("unable to delete process %s[%d] in redis", process.getId(), process.getVersion()), e);
			}
		}
	}

	public void refreshProcesses() {
		try {
			for (ProcessDefinition process : definitions.values()) {
				try {
					repository.get(process.getId(), process.getVersion()).ifPresent(p -> {
						RedisProcessService.super.addProcess(p);
					});
				} catch (Exception e) {
					log.error(String.format("unable to read process %s:%d", process.getId(), process.getVersion()), e);
				}
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
