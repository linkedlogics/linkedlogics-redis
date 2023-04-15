package io.linkedlogics.redis.process;

import java.time.OffsetDateTime;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.service.SchedulerService.Schedule;
import io.linkedlogics.service.SchedulerService.ScheduleType;
import io.linkedlogics.redis.service.RedisServiceConfigurer;
import redis.embedded.RedisServer;
import io.linkedlogics.service.ServiceLocator;

public class Main {
	public static void main(String[] args) {
		RedisServer redisServer = new RedisServer(6370);
		redisServer.start();
		LinkedLogics.configure(new RedisServiceConfigurer());
		
		System.out.println(OffsetDateTime.now());
		ServiceLocator.getInstance().getSchedulerService().schedule(new Schedule("id", "LOGIC_3sec", "1", OffsetDateTime.now().plusSeconds(3), ScheduleType.DELAY));
		ServiceLocator.getInstance().getSchedulerService().schedule(new Schedule("id", "LOGIC_10sec", "1", OffsetDateTime.now().plusSeconds(10), ScheduleType.DELAY));
		ServiceLocator.getInstance().getSchedulerService().schedule(new Schedule("id", "LOGIC_40sec", "1", OffsetDateTime.now().plusSeconds(40), ScheduleType.DELAY));
		ServiceLocator.getInstance().getSchedulerService().schedule(new Schedule("id", "LOGIC_1min_10sec", "1", OffsetDateTime.now().plusSeconds(70), ScheduleType.DELAY));
	
		
	}
}
