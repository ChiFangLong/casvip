package com.process.util.cxt;

import org.activiti.engine.FormService;

import com.process.util.ActivitiUtil;

public class activitiFormUtil {
	public static final FormService formService = ActivitiUtil.getProcessEngine().getFormService() ;
	
	public static Object getformByTaskId(String taskId, int operator){
		if(operator==0){	
			return formService.getRenderedTaskForm(taskId);
		}	
		Object object = new Object();
		object = new String("该流程已结束，无法查询历史表单数据！");
	return  object;
	}
	

}
