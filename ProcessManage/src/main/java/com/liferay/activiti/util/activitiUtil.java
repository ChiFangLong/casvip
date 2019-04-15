package com.liferay.activiti.util;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;

import com.liferay.portal.kernel.util.PropsUtil;

public class activitiUtil {
	private static ProcessEngine processEngine;
	private static ProcessEngineConfiguration processEngineConfiguration;
	
	public static ProcessEngineConfiguration getConfiguration(){
		return processEngineConfiguration;
	}
	
	public static ProcessEngine getProcessEngine() {
		if (processEngine == null) {
			// 使用配制文件的方式
			// Jdbc设置
			String jdbcDriver = PropsUtil.get("jdbc.activiti.driverClassName");
			String jdbcUrl = PropsUtil.get("jdbc.activiti.url");
			String jdbcUsername = PropsUtil.get("jdbc.activiti.username");
			String jdbcPassword = PropsUtil.get("jdbc.activiti.password");
			// 获取config对象
			processEngineConfiguration = ProcessEngineConfiguration
					.createStandaloneProcessEngineConfiguration();
			processEngineConfiguration.setJdbcDriver(jdbcDriver);
			processEngineConfiguration.setJdbcUrl(jdbcUrl);
			processEngineConfiguration.setJdbcUsername(jdbcUsername);
			processEngineConfiguration.setJdbcPassword(jdbcPassword);
			// public static final String DB_SCHEMA_UPDATE_FALSE =
			// "false";//不自动创建新表
			// 设置是否自动更新
			processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
			// 获取引擎对象
			processEngine = processEngineConfiguration.buildProcessEngine();
			return processEngine;
		}
		return processEngine;
	}
}
