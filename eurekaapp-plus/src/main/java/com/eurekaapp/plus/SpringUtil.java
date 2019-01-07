package com.eurekaapp.plus;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @project risk_limit
 * @author yy
 * @date 2017年12月21日 下午1:01:52
 * @description TODO spring相关设置  初始化 bean到普通类
 * @tag 
 * @company 上海金互行金融信息服务有限公司
 */
@Component
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext context;
	
	
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		SpringUtil.context=context;
	}


	public static ApplicationContext getContext() {
		return context;
	}


	
}
