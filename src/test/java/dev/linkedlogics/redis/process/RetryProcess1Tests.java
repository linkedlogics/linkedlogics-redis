package dev.linkedlogics.redis.process;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.group;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.redis.process.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.annotation.Input;
import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError.ErrorType;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.exception.LogicException;
import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.redis.service.RedisServiceConfigurer;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;
import redis.embedded.RedisServer;

public class RetryProcess1Tests {

	private static ContextService contextService;
	private static AtomicInteger retryCounter;
	private static RedisServer redisServer;

	@BeforeAll
	public static void setUp() {
		redisServer = new RedisServer(6370);
		redisServer.start();
		LinkedLogics.configure(new RedisServiceConfigurer());
		LinkedLogics.registerLogic(RetryProcess1Tests.class);
		LinkedLogics.registerProcess(RetryProcess1Tests.class);
		contextService = ServiceLocator.getInstance().getContextService();
	}
	
	@AfterAll
	public static void cleanUp() {
		if (redisServer != null)
			redisServer.stop();
	}

	@BeforeEach
	public void resetCounter() {
		retryCounter = new AtomicInteger();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start("RETRY_SCENARIO_1", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED, 12000)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(retryCounter.get()).isEqualTo(3);
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
	}

	public static ProcessDefinition scenario1() {
		return createProcess("RETRY_SCENARIO_1", 0)
				.add(logic("INSERT").application("test").input("list", expr("list")).input("val", 1).retry(3, 1).build())
				.add(logic("INSERT").application("test").input("list", expr("list")).input("val", 2).build())
				.build();
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start("RETRY_SCENARIO_2", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED, 12000)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().containsKey("list")).isTrue();

		assertThat(retryCounter.get()).isEqualTo(2);
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(1, 2);
	}

	public static ProcessDefinition scenario2() {
		return createProcess("RETRY_SCENARIO_2", 0)
				.add(logic("INSERT_SAFE").application("test").input("list", expr("list")).input("val", 1).retry(3, 1).build())
				.add(logic("INSERT").application("test").input("list", expr("list")).input("val", 2).build())
				.build();
	}

	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start("RETRY_SCENARIO_3", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED, 12000)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(retryCounter.get()).isEqualTo(3);
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains(2);
	}

	public static ProcessDefinition scenario3() {
		return createProcess("RETRY_SCENARIO_3", 0)
				.add(group(logic("INSERT").application("test").input("list", expr("list")).input("val", 2).build(),
						logic("INSERT").application("test").input("list", expr("list")).input("val", 1).build())
						.retry(3, 1)
						.build())
				.add(logic("INSERT").application("test").input("list", expr("list")).input("val", 3).build())
				.build();
	}

	@Logic(id = "INSERT", returnAs = "insert_result")
	public static boolean insert(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		if (value % 2 == 0) {
			list.add(value);
			return true;
		}
		retryCounter.incrementAndGet();
		throw new LogicException(-1, "value is not even", ErrorType.TEMPORARY);
	}

	@Logic(id = "INSERT_SAFE", returnAs = "insert_result")
	public static boolean insertSafe(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		if (value % 2 == 0) {
			list.add(value);
			return true;
		}
		retryCounter.incrementAndGet();
		if (retryCounter.get() == 2) {
			list.add(value % 2);
			return true;
		} else {
			throw new LogicException(-1, "value is not even", ErrorType.TEMPORARY);
		}
	}

	@Logic(id = "REMOVE", returnAs = "remove_result")
	public static boolean remove(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		return list.remove(value);
	}
}