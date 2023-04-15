package io.linkedlogics.redis.process;

import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.redis.process.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.LinkedLogicsBuilder;
import io.linkedlogics.LinkedLogicsCallback;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.redis.service.RedisServiceConfigurer;
import redis.embedded.RedisServer;

public class SimpleProcess1Tests {
	private static RedisServer redisServer;
	
	@BeforeAll
	public static void setUp() {
		redisServer = new RedisServer(6370);
		redisServer.start();
		LinkedLogics.configure(new RedisServiceConfigurer());
		LinkedLogics.registerLogic(SimpleProcess1Tests.class);
		LinkedLogics.registerProcess(SimpleProcess1Tests.class);
		LinkedLogics.launch();
	}
	
	@AfterAll
	public static void cleanUp() {
		if (redisServer != null)
			redisServer.stop();
	}

	@Test
	public void testScenario1() {
		AtomicBoolean result = new AtomicBoolean();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("s", "hello");}},
				new LinkedLogicsCallback() {
					
					@Override
					public void onTimeout() {
						
					}
					
					@Override
					public void onSuccess(Context context) {
						String s = (String) context.getParams().get("s");
						result.set(s.equals("ZZZYYYXXXHELLO"));						
					}
					
					@Override
					public void onFailure(Context context, ContextError error) {
						
					}
				});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();
		assertThat(result.get()).isTrue();
	}
	
	@Logic(id = "UPPER", returnAs = "s")
	public static String makeUpper(@Input("s") String s) {
		return s.toUpperCase();
	}
	
	@Logic(id = "PREFIX", returnAs = "s")
	public static String addPrefix(@Input("s") String s, @Input("p") String p) {
		return p + s;
	}
	
	public static ProcessDefinition simple() {
		return LinkedLogicsBuilder.createProcess("SIMPLE_SCENARIO_1", 1)
				.add(logic("UPPER").application("test").input("s", expr("s")).build())
				.add(logic("PREFIX").application("test").input("s", expr("s")).input("p", "XXX").build())
				.add(logic("UPPER").application("test").input("s", expr("s")).build())
				.add(logic("PREFIX").application("test").input("s", expr("s")).input("p", "yyy").build())
				.add(logic("UPPER").application("test").input("s", expr("s")).build())
				.add(logic("PREFIX").application("test").input("s", expr("s")).input("p", "ZZZ").build())
				.build();
	}
}
