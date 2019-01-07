package com.yy.eurekaapp.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Signal;
import sun.misc.SignalHandler;
@Service
@Slf4j
public class SignalService implements SignalHandler {
	private DisconveryService disconveryService = new DisconveryService();

	static {
		SignalService signaService = new SignalService();
		Signal.handle(new Signal("TERM"), signaService);
	}
	@Override
	public void handle(Signal signal) {
		
		log.info("接收到信号："+signal.getName());
		
		log.info("关闭注册中心开始....");
		disconveryService.shutdown();
		log.info("关闭注册中心结束....");
		
		log.info("活跃线程数[{}],正在结束自己......",Thread.activeCount());
		
		System.exit(0);
	}

}
