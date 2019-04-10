package com.process.util.cxt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;

import com.process.util.ActivitiUtil;

public class activitiHistoryYtil {
	public static final HistoryService historyService = ActivitiUtil.getProcessEngine().getHistoryService();

	public static String getProcessCreator(String processInsId) {
		return historyService.createHistoricProcessInstanceQuery().processInstanceId(processInsId).singleResult()
				.getStartUserId();
	}

	public static String getProcessInstanceIdByTaskId(String taskId) {
		return historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult().getProcessInstanceId();
	}

	public static String getProDefIdByProInsId(String processInstanceId) {
		return historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult()
				.getProcessDefinitionId();
	}

	public static List<Map<String, String>> getHistorySignatureOpinion(String processInstanceId, String taskId,int operator) {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
		if (historicActivityInstances != null && historicActivityInstances.size() > 0) {

			for (HistoricActivityInstance hiInstance : historicActivityInstances) {
				Map<String, String> map = new HashMap<String, String>();
				Map<String, Object> varMap =new HashMap<String, Object>() ;
				//operator==0 在运行流程   operator==1已完成流程
				if(operator==0){
				varMap = activitiRunUtil.getAllVaribles(hiInstance.getExecutionId());
				}else if(operator==1){
					varMap = activitiHistoryYtil.getHisAllVariable(processInstanceId);
				}
				if (list.size() == 0) {
					map.put("assignee", activitiHistoryYtil.getProcessCreator(processInstanceId));
					map.put("signatureOpinion", "申请人/提交");
				} else {
					map.put("assignee", hiInstance.getAssignee());
					if (varMap.get(hiInstance.getAssignee() + "_" + "signatureOpinion") != null) {
						map.put("signatureOpinion",
								varMap.get(hiInstance.getAssignee() + "_" + "signatureOpinion").toString());
					} else {
						return list;
					}
				}
				map.put("EndTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(hiInstance.getStartTime()));

				list.add(map);
			}
		}
		return list;
	}

	public static List<HistoricProcessInstance> getMyRequestProcess(String userId) {
		return historyService.createHistoricProcessInstanceQuery().startedBy(userId).list();

	}

	public static String getHisTaskIdByProcInsId(String processInstanceId) {
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
		String taskId =null;
		if (list != null && list.size() > 0 && list.size() == 1) {
			taskId = list.get(0).getId();
		} else {
			taskId = list.get(1).getId() ;
		}

		return taskId;
	}
	
	public static Map<String, Object> getHisAllVariable(String processInstanceId){
		Map<String, Object> varMap =new HashMap<String, Object>() ;
		List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list() ;
		if(list!=null&&list.size()>0){
			for(HistoricVariableInstance hisvar:list){
				varMap.put(hisvar.getVariableName(), hisvar.getValue()) ;
			}
		}
		return varMap ;
	}
	
}
