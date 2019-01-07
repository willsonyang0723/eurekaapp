package com.yy.eurekaapp.web;

import javax.annotation.Resource;

import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.shared.Applications;
import com.yy.eurekaapp.service.DisconveryService;

@RestController
@RequestMapping("disconvery")
public class DisconveryWeb {
	@Resource
	private DisconveryService disconveryService;
	@GetMapping("offline")
	public void offline() {
		disconveryService.shutdown();
		
	}
	
	@GetMapping("remove")
	public String remove(String homepageUrl) {
		
		disconveryService.removeInstance(homepageUrl);
		
		return "ok";
	}
}
