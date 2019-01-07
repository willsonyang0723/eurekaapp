package com.yy.eurekaapp;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.shared.Applications;

@Service
public class EurekaJob {

	@Scheduled(fixedRate=3000)
	public void checkStatus() {
		System.out.println(DateFormatUtils.format(new Date(), "HH:mm:ss")+"--------------------------------------------------------------");
		Applications as = DiscoveryManager.getInstance().getLookupService().getApplications();
		as.getRegisteredApplications().forEach(app -> app.getInstances()
				.forEach(
							appi ->{
								System.out.println(appi.getIPAddr()+":"+appi.getPort());
								}
						)
				);
		
		
	}
}
