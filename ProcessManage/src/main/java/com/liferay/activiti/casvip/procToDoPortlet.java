package com.liferay.activiti.casvip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

import org.activiti.engine.history.HistoricTaskInstance;

import com.alibaba.fastjson.JSON;
import com.liferay.activiti.util.ActivitiPngUtil;
import com.liferay.activiti.util.activitiFormUtil;
import com.liferay.activiti.util.activitiHistoryYtil;
import com.liferay.activiti.util.activitiTaskUtil;
import com.liferay.activiti.util.cfl.TodoUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class procToDoPortlet
 */
public class procToDoPortlet extends MVCPortlet {

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		/*try {
			if (PortalUtil.getUser(renderRequest) != null) {
				String activitiUserId = PortalUtil.getUser(renderRequest).getScreenName();
				List<Task> myTasks = activitiTaskUtil.getTaskByUserId(activitiUserId);
				List<Map<String, Object>> myList = new ArrayList<Map<String, Object>>();
				if (myTasks != null && myTasks.size() > 0) {
					int index = 0;
					for (Task task : myTasks) {
						if(index == 5){
							break;
						}
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("processName", task.getName());
						map.put("processCreator", activitiHistoryYtil.getProcessCreator(task.getProcessInstanceId()));
						map.put("processCreateTime", activitiTaskUtil.theDateToString(task.getCreateTime()));
						map.put("processTaskId", task.getId());
						myList.add(map);
						index ++;
					}
				}
				renderRequest.setAttribute("myList", myList);
			}
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}*/
		super.doView(renderRequest, renderResponse);
	}

	public void requestSubmission(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {
		String taskId = ParamUtil.getString(actionRequest, "processTaskId");
		actionRequest.getPortletSession().setAttribute("taskId", taskId, PortletSession.APPLICATION_SCOPE);
		Object processForm = activitiFormUtil.getformByTaskId(taskId, 0);
		List<Map<String, String>> myHisList = activitiHistoryYtil
				.getHistorySignatureOpinion(activitiHistoryYtil.getProcessInstanceIdByTaskId(taskId), taskId, 0);
		actionRequest.getPortletSession().setAttribute("processHisOpinions", myHisList,
				PortletSession.APPLICATION_SCOPE);
		actionRequest.getPortletSession().setAttribute("processForm", processForm, PortletSession.APPLICATION_SCOPE);
		actionRequest.getPortletSession().setAttribute("toDoProTaskId", taskId, PortletSession.APPLICATION_SCOPE);

		actionResponse.setRenderParameter("mvcPath", "/html/proctodo/formView.jsp");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {
		super.serveResource(resourceRequest, resourceResponse);
		
		String resourceNum = ParamUtil.getString(resourceRequest, "resourceNum");
		PrintWriter out = resourceResponse.getWriter();
		if("1".equals(resourceNum)){
			String toDoProTaskId = resourceRequest.getPortletSession()
					.getAttribute("toDoProTaskId", PortletSession.APPLICATION_SCOPE).toString();
			String NewForm = ParamUtil.getString(resourceRequest, "NewForm");
			Map maps = (Map) JSON.parse(NewForm);
			Map<String, Object> myMap = new HashMap<String, Object>();
			for (Object map : maps.entrySet()) {
				String key = ((Map.Entry) map).getKey().toString();
				Object value = ((Map.Entry) map).getValue();
				if (key.equals("signatureOpinion")) {
					String activitiId;
					try {
						activitiId = PortalUtil.getUser(resourceRequest).getScreenName();
						key = activitiId + "_" + key;
					} catch (PortalException e) {
						e.printStackTrace();
					} catch (SystemException e) {
						e.printStackTrace();
					}
				}
				myMap.put(key, value);
			}
			if (myMap != null) {
				activitiTaskUtil.taskService.complete(toDoProTaskId, myMap);
			}
		} else if("2".equals(resourceNum)){
			String page = ParamUtil.getString(resourceRequest, "page");
			List<Map<String, Object>> map = TodoUtil.getList(resourceRequest, page);
			String str = JSON.toJSONString(map);
			out.println(str);
		} else if("3".equals(resourceNum)){
			Integer setTotalCount = (Integer) resourceRequest.getPortletSession().getAttribute("setTotalCount", PortletSession.APPLICATION_SCOPE);
			out.print(setTotalCount);
		}
		
		out.flush();
		out.close();
	}

	public void goToreutrnAction(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		// fanhui
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

	public void goToreutrnAct(ActionRequest actionRequest, ActionResponse actionResponse) {
		actionResponse.setRenderParameter("mvcPath", "/html/proctodo/view.jsp");
	}
	
}
