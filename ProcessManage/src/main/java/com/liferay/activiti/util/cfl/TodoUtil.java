package com.liferay.activiti.util.cfl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;

import com.liferay.activiti.util.activitiHistoryYtil;
import com.liferay.activiti.util.activitiTaskUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

public class TodoUtil {
	
	private static int PageNum = 5;
	
	public static List<Map<String, Object>> getList(ResourceRequest request,String page){
		List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
		try {
			String activitiUserId = PortalUtil.getUser(request).getScreenName();
			// 获取数据集合获得数据总数量
			List<Task> myTasks = activitiTaskUtil.getTaskByUserId(activitiUserId);
			int listSize = myTasks.size();
			request.getPortletSession().setAttribute("setTotalCount", listSize, PortletSession.APPLICATION_SCOPE);
			// 根据页数显示数据
			int pageNum = Integer.parseInt(page);
			int firstNum = (pageNum - 1) * PageNum;
			List<Task> taskList = activitiTaskUtil.getTaskPageByUserId(activitiUserId, firstNum, PageNum);
			
			if (myTasks != null && myTasks.size() > 0) {
				for (Task task : taskList) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("processName", task.getName());
					map.put("processCreator", activitiHistoryYtil.getProcessCreator(task.getProcessInstanceId()));
					map.put("processCreateTime", activitiTaskUtil.theDateToString(task.getCreateTime()));
					map.put("processTaskId", task.getId());
					dataMap.add(map);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return dataMap;
	}
	
	public static List<Map<String, Object>> getHistoricList(ResourceRequest request, String page, String searchType){
		List<Map<String, Object>> myList = new ArrayList<Map<String, Object>>();
		try {
			
			int pageNum = Integer.parseInt(page);
			int firstNum = (pageNum - 1) * PageNum;
			
			String activitiUserId = PortalUtil.getUser(request).getScreenName();
			// 获取总数量
			List<HistoricProcessInstance> list = activitiHistoryYtil.getMyRequestProcess(activitiUserId, searchType) ;
			request.getPortletSession().setAttribute("setTotalCount", list.size(), PortletSession.APPLICATION_SCOPE);
			// 获得数据
			List<HistoricProcessInstance> myRequestList = activitiHistoryYtil.getMyRequestProcess(activitiUserId, searchType, firstNum, PageNum) ;
			
			if (myRequestList != null && myRequestList.size() > 0) {
				for (HistoricProcessInstance hpIns : myRequestList) {
					String processInstanceId = hpIns.getId() ;
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("processName", hpIns.getProcessDefinitionName());
					map.put("processCreator", activitiUserId);
					map.put("processCreateTime", activitiTaskUtil.theDateToString(hpIns.getStartTime()));		
					if(hpIns.getEndTime()!=null){
						map.put("processNextAssignee","(已完结)" ) ;	
						map.put("processTaskId", activitiHistoryYtil.getHisTaskIdByProcInsId(processInstanceId)) ;
					} else{
						map.put("processNextAssignee",activitiTaskUtil.getNextAssigneeByProInsId(processInstanceId)) ;	
						map.put("processTaskId", activitiTaskUtil.getTaskIdByProInsId(processInstanceId)) ;			
					}
					myList.add(map);
				}
			}
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return myList;
	}

}
