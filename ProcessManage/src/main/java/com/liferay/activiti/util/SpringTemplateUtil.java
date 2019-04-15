package com.liferay.activiti.util;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.liferay.portal.kernel.util.PropsUtil;

public class SpringTemplateUtil {
	private static JdbcTemplate jdbcTemplate;

	public static JdbcTemplate getJdbcTemplate() {
		if (jdbcTemplate == null) {
			// 使用配制文件的方式
			// Jdbc设置
			String jdbcDriver = PropsUtil.get("jdbc.activiti.driverClassName");
			String jdbcUrl = PropsUtil.get("jdbc.activiti.url");
			String jdbcUsername = PropsUtil.get("jdbc.activiti.username");
			String jdbcPassword = PropsUtil.get("jdbc.activiti.password");
			 BasicDataSource datasource = new BasicDataSource();
			 datasource.setDriverClassName(jdbcDriver);
			 datasource.setUrl(jdbcUrl);
			 datasource.setUsername(jdbcUsername);
			 datasource.setPassword(jdbcPassword);	 
			  jdbcTemplate = new JdbcTemplate(datasource);
			return jdbcTemplate;
		}
		return jdbcTemplate;
	}
}
