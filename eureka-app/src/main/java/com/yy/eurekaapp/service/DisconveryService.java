package com.yy.eurekaapp.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.SystemPropertyUtils;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.util.SystemUtil;
import com.yy.eurekaapp.EurekaAppApplication;
import com.yy.eurekaapp.plus.SpringUtil;
import com.yy.eurekaapp.util.HttpUtils;

import lombok.extern.slf4j.Slf4j;
/**
 * @project eureka-app
 * @author yy
 * @date 2018年12月29日 下午5:05:34
 * @description TODO 注册发现分服务类
 * @tag 
 * @company 上海金互行金融信息服务有限公司
 */
@Slf4j
@Service
public class DisconveryService {

	@Resource
	private Environment env;
	/**
	 * @title shutdown 关闭当前 注册服务
	 * @description 
	 * @author yy
	 * @date 2018年12月29日 下午5:06:08
	 * @return void
	 */
	public void shutdown() {
		try {
			String homepage = "http://"+com.netflix.discovery.util.SystemUtil.getServerIPv4()+":"
					+SpringUtil.getContext().getBean(Environment.class).getProperty("server.port")+"/";//当前homepage
			log.info("homepage:"+homepage);
			Applications as = DiscoveryManager.getInstance().getLookupService().getApplications();
			
			log.info("注销本实例开始");
			DiscoveryManager.getInstance().shutdownComponent();//注销注册
			log.info("注销本实例开始");
			log.info("通知组件删除本实例开始");
			//调用其他服务接口 清除 本实例
			as.getRegisteredApplications().forEach(app -> app.getInstances()
					.forEach(
								appi ->{
										if(!appi.getHomePageUrl().equals(homepage)) {
											try {
												String rs=HttpUtils.sendGet(appi.getHomePageUrl()+"disconvery/remove", "homepageUrl="+homepage);
												log.info(appi.getHomePageUrl()+">>>>"+rs);
											} catch (Exception e) {
												log.error("[{}]发送清除服务消息失败",appi.getHomePageUrl(),e);
											}
										}
									}
							)
					);
			log.info("通知组件删除本实例结束");
		} catch (Exception e) {
			log.error("",e);
		}
	}
	
	
	/**
	 * @title removeInstance 
	 * @description 根据homepage移除实例
	 * @author yy
	 * @date 2018年12月29日 下午5:07:09
	 * @param homepage
	 * @return void
	 */
	public void removeInstance(String homepageUrl) {
		
		Applications as = DiscoveryManager.getInstance().getLookupService().getApplications();
		as.getRegisteredApplications().forEach(app -> app.getInstances()
				.removeIf(aapi ->aapi.getHomePageUrl().equals(homepageUrl))
				);
	}
}
