package com.liferay.activiti.casvip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;

import com.alibaba.fastjson.JSON;
import com.liferay.activiti.util.ActivitiPngUtil;
import com.liferay.activiti.util.activitiFormUtil;
import com.liferay.activiti.util.activitiHistoryYtil;
import com.liferay.activiti.util.activitiUtil;
import com.liferay.activiti.util.cfl.TodoUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class procRequestPortlet
 */
public class procRequestPortlet extends MVCPortlet {

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
//		try {
//			if(PortalUtil.getUser(renderRequest)!=null){
//			String activitiUserId = PortalUtil.getUser(renderRequest).getScreenName();
//			
//			List<HistoricProcessInstance> myRequestList = activitiHistoryYtil.getMyRequestProcess(activitiUserId) ;
//				
//		
//				List<Map<String, Object>> myList = new ArrayList<Map<String, Object>>();
//				if (myRequestList != null && myRequestList.size() > 0) {
//					for (HistoricProcessInstance hpIns : myRequestList) {
//						String processInstanceId = hpIns.getId() ;
//						Map<String, Object> map = new HashMap<String, Object>();
//						map.put("processName", hpIns.getProcessDefinitionName());
//						map.put("processCreator", activitiUserId);
//						map.put("processCreateTime", activitiTaskUtil.theDateToString(hpIns.getStartTime()));		
//						
//						if(hpIns.getEndTime()!=null){
//							map.put("processNextAssignee","(已完结)" ) ;	
//							map.put("processTaskId", activitiHistoryYtil.getHisTaskIdByProcInsId(processInstanceId)) ;
//						} else{
//							map.put("processNextAssignee",activitiTaskUtil.getNextAssigneeByProInsId(processInstanceId)) ;	
//							map.put("processTaskId", activitiTaskUtil.getTaskIdByProInsId(processInstanceId)) ;			
//						}
//						
//						myList.add(map);
//					}
//				}
//				renderRequest.setAttribute("myRequestList", myList);
//			}
//		} catch (PortalException e) {
//			e.printStackTrace();
//		} catch (SystemException e) {
//			e.printStackTrace();
//		}				
		super.doView(renderRequest, renderResponse);
	}
	
	
	

	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {
		super.serveResource(resourceRequest, resourceResponse);
		
		String resourceNum = ParamUtil.getString(resourceRequest, "resourceNum");
		PrintWriter out = resourceResponse.getWriter();
		if("1".equals(resourceNum)){
			String page = ParamUtil.getString(resourceRequest, "page");
			String searchType = ParamUtil.getString(resourceRequest, "searchType");
			List<Map<String, Object>> map = TodoUtil.getHistoricList(resourceRequest, page, searchType);
			String str = JSON.toJSONString(map);
			out.println(str);
		} else if("2".equals(resourceNum)){
			Integer setTotalCount = (Integer) resourceRequest.getPortletSession().getAttribute("setTotalCount", PortletSession.APPLICATION_SCOPE);
			out.print(setTotalCount);
		}
		
		out.flush();
		out.close();
	}


	public void goToReadRequestProcessAct(ActionRequest actionRequest, ActionResponse actionResponse) {
		
		String taskId = ParamUtil.getString(actionRequest, "taskId");
		actionRequest.getPortletSession().setAttribute("taskId", taskId,
				PortletSession.APPLICATION_SCOPE);
		TaskService taskService = activitiUtil.getProcessEngine().getTaskService();
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		int operator = 1;
		if (task != null) { // 为null说明在 excution 没有此数据，说明已完成
			operator = 0;
		}
		Object processForm = activitiFormUtil.getformByTaskId(taskId,operator);
		List<Map<String, String>> myHisList = activitiHistoryYtil.getHistorySignatureOpinion(activitiHistoryYtil.getProcessInstanceIdByTaskId(taskId), taskId, operator);
		actionRequest.getPortletSession().setAttribute("processHisOpinionsForRequest", myHisList, PortletSession.APPLICATION_SCOPE);
		actionRequest.getPortletSession().setAttribute("processFormForRequest", processForm, PortletSession.APPLICATION_SCOPE);
		actionResponse.setRenderParameter("mvcPath", "/html/procrequest/HistoryProcView.jsp");
	}
	
	public void goToreutrnAction(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException, PortletException {
		//fanhui 
	}

	public void goProcessPngJSP(ActionRequest actionRequest, ActionResponse actionResponse) {
		// 获得任务Id
		String taskId = (String) actionRequest.getPortletSession().getAttribute("taskId",
				PortletSession.APPLICATION_SCOPE);
		// 根据任务获取流程实例
		HistoricTaskInstance historicTask = activitiHistoryYtil.historyService.createHistoricTaskInstanceQuery().taskId(taskId)
				.singleResult();

		String processInstanceId = historicTask.getProcessInstanceId();	// 流程实例Id
			
		// 使用工具类获取流
		ActivitiPngUtil pngUtil = new ActivitiPngUtil();
		InputStream inputStream = pngUtil.getFlowImgByInstanceId(processInstanceId);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		// 流处理
		byte[] buf = new byte[1024];
		int numBytesRead = 0;
		byte[] data = null;
		try {
			while ((numBytesRead = inputStream.read(buf)) != -1) {
				output.write(buf, 0, numBytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		data = output.toByteArray(); // 将字节流内容存入 byte 数组
		// 将byte数组的内容存入List  方便解析
		List<Object> lists = new ArrayList<Object>();
		for (int i = 0; i < data.length; i++) {
			lists.add(data[i]);
		}
		// 存入session 供 前端获取
		actionRequest.getPortletSession().setAttribute("byteList", JSON.toJSONString(lists),PortletSession.APPLICATION_SCOPE);
	}

}
