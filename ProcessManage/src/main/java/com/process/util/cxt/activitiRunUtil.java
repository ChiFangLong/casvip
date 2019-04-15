package com.process.util.cxt;

import java.util.Map;

import org.activiti.engine.RuntimeService;

import com.process.util.ActivitiUtil;

public class activitiRunUtil {
	public static final RuntimeService runTimeService = ActivitiUtil.getProcessEngine().getRuntimeService() ;

	public static Map<String, Object> getAllVaribles(String executionId){
		return runTimeService.getVariables(executionId) ;
	}
	
	

}
