package dev.linkedlogics.redis.repository;

import java.util.Optional;
import java.util.OptionalInt;

import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.model.ProcessDefinitionReader;
import dev.linkedlogics.model.ProcessDefinitionWriter;

public class ProcessRepository extends JedisRepository {
	private static final String PROCESS = "process:";

	public void set(ProcessDefinition process) throws Exception {
		redisTemplate.opsForValue().set(getKey(process), getValue(process));
	}

	public Optional<ProcessDefinition> get(String id, int version) throws Exception {
		String currentValue = redisTemplate.opsForValue().get(getKey(id, version));
		if (currentValue != null && !currentValue.isEmpty()) {
			return Optional.of(getValue(currentValue));
		} 
		return Optional.empty();
	}

	public void delete(String id, int version) throws Exception {
		String currentValue = redisTemplate.opsForValue().get(getKey(id, version));
		if (currentValue != null && !currentValue.isEmpty()) {
			redisTemplate.delete(getKey(id, version));
		}
	}
	
	public void setVersion(String id, int version) {
		redisTemplate.opsForSet().add(getMaxVersionKey(id), String.valueOf(version));
	}
	
	public void deleteVersion(String id, int version) {
		redisTemplate.opsForSet().remove(getMaxVersionKey(id), String.valueOf(version));
	}
	
	public Optional<Integer> getMaxVersion(String id) {
		OptionalInt version = redisTemplate.opsForSet().members(getMaxVersionKey(id)).stream().mapToInt(s -> Integer.parseInt(s)).max();
		if (version.isPresent()) {
			return Optional.of(version.getAsInt());
		} else {
			return Optional.empty();
		}
	}

	private String getKey(ProcessDefinition process) {
		return PROCESS + process.getId() + ":" + process.getVersion();
	}

	private String getKey(String id, int version) {
		return PROCESS + id + ":" + version;
	}
	
	private String getMaxVersionKey(String id) {
		return PROCESS + id + ":MAX_VERSION";
	}

	private String getValue(ProcessDefinition process) throws Exception {
		return new ProcessDefinitionWriter(process).write();
	}

	private ProcessDefinition getValue(String process) throws Exception {
		return new ProcessDefinitionReader(process).read();
	}
}
