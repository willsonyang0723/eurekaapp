package com.eurekaapp.web;

import javax.annotation.Resource;

import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eurekaapp.service.DisconveryService;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.shared.Applications;

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
