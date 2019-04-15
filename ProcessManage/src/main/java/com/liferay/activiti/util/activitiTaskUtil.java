package com.liferay.activiti.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;


public class activitiTaskUtil {
	public static final TaskService taskService = activitiUtil.getProcessEngine().getTaskService();
	
	public static List<Task> getTaskByUserId(String userId){
		activitiTaskUtil.claimTaskClaimByUserId(userId);
		return taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();				
	}
	
	/**
	 * 分页获取数据
	 */
	public static List<Task> getTaskPageByUserId(String userId, int firstResult, int maxResults){
		activitiTaskUtil.claimTaskClaimByUserId(userId);
		return taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().listPage(firstResult, maxResults);				
	}

	public static void claimTaskClaimByUserId(String userId){
		List<Task> list = taskService.createTaskQuery().taskCandidateUser(userId).list() ;
		if(list!=null && list.size()>0){
			for(Task task:list){
				taskService.claim(task.getId(), userId);
			}
		}
	}
	
	public static void completTaskByTaskId(String taskId){
		taskService.complete(taskId);
	}
	
	public static String theDateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
	
	public static String getNextAssigneeByProInsId(String processInstanceId){
		return taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getAssignee();
	}
	
	public static String getTaskIdByProInsId(String processInstanceId){
		return taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getId();

	}
}
