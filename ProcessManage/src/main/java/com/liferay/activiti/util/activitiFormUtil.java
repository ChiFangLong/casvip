package com.liferay.activiti.util;

import org.activiti.engine.FormService;

public class activitiFormUtil {
	public static final FormService formService = activitiUtil.getProcessEngine().getFormService() ;
	
	public static Object getformByTaskId(String taskId,int operator){
			if(operator==0){	
				return formService.getRenderedTaskForm(taskId);
			}	
			Object object = new Object();
			object = new String("该流程已结束，无法查询历史表单数据！");
		return  object;
	}
	

}
