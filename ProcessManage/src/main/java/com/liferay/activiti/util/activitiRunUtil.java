package com.liferay.activiti.util;

import java.util.Map;

import org.activiti.engine.RuntimeService;

public class activitiRunUtil {
	public static final RuntimeService runTimeService = activitiUtil.getProcessEngine().getRuntimeService() ;

	public static Map<String, Object> getAllVaribles(String executionId){
		return runTimeService.getVariables(executionId) ;
	}
	
	

}
