package com.process.portlet;

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
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSON;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.process.util.ActivitiUtil;
import com.process.util.cxt.activitiFormUtil;
import com.process.util.cxt.activitiHistoryYtil;
import com.process.util.processPng.ActivitiPngUtil;

/**
 * Portlet implementation class ProcessPortlet
 */
public class ProcessPortlet extends MVCPortlet {
	// 加载配置文件
	ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
	// 获得映射对象
	JdbcTemplate jdbcTemplate = (JdbcTemplate) ac.getBean("jdbcTemplate");
	
	ProcessEngine engine = ActivitiUtil.getProcessEngine();
	HistoryService historyService = engine.getHistoryService();
	RepositoryService repositoryService = engine.getRepositoryService();

	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {
		super.serveResource(resourceRequest, resourceResponse);
		PrintWriter out = resourceResponse.getWriter();

		String title = ParamUtil.getString(resourceRequest, "title");
		String numbder = ParamUtil.getString(resourceRequest, "numbder");
		String processType = ParamUtil.getString(resourceRequest, "processType");
		String startPeople = ParamUtil.getString(resourceRequest, "startPeople");
		String startTime = ParamUtil.getString(resourceRequest, "startTime");
		String startTime2 = ParamUtil.getString(resourceRequest, "startTime2");
		String endTime = ParamUtil.getString(resourceRequest, "endTime");
		String endTime2 = ParamUtil.getString(resourceRequest, "endTime2");
		
		StringBuffer tbodyContent = new StringBuffer();
		// 根据前台传入的类型进行不同数据的获取
		String resourceType = ParamUtil.getString(resourceRequest, "resourceType");
		System.out.println(resourceType);
		// 获取所有流程
		if (resourceType.equals("1")) {
			// 获取当前登录的用户
			String screenName = "";
			try {
				screenName = PortalUtil.getUser(resourceRequest).getScreenName();
			} catch (PortalException e) {
				e.printStackTrace();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			Integer page = ParamUtil.getInteger(resourceRequest, "currPage");
			String searchType = ParamUtil.getString(resourceRequest, "searchType");
			tbodyContent = ActivitiUtil.pageProcess(resourceRequest, screenName, page, searchType, title, numbder, processType, startPeople, startTime, startTime2, endTime, endTime2);
			out.print(tbodyContent);
		} else if(resourceType.equals("2")){
			// 获取存入的参数
			Integer setTotalCount = (Integer) resourceRequest.getPortletSession().getAttribute("setTotalCount", PortletSession.APPLICATION_SCOPE);//数据总数量
			Integer totalPages = (Integer) resourceRequest.getPortletSession().getAttribute("totalPages", PortletSession.APPLICATION_SCOPE);//数据总数量
			// 存入map
			Map<String, Integer> hashMap = new HashMap<String, Integer>();
			hashMap.put("setTotalCount", setTotalCount);
			hashMap.put("totalPages", totalPages);
			// 转换格式返回
			String str = JSON.toJSONString(hashMap);
			out.print(str);
		} else if(resourceType.equals("3")){
			// 查询数据库内容
			List<Map<String, Object>> uMapList = jdbcTemplate
					.queryForList("SELECT sec.id,NAME_  FROM act_re_deployment `fir` INNER JOIN (SELECT deployment_id_ id,COUNT(deployment_id_) `count` FROM `act_re_procdef` a WHERE deployment_id_ IN (SELECT id_ FROM act_re_deployment a INNER JOIN (SELECT MAX(DEPLOY_TIME_) `time`  FROM act_re_deployment WHERE name_ IS NOT NULL GROUP BY NAME_) b ON a.DEPLOY_TIME_ = b.time) GROUP BY deployment_id_) sec ON fir.id_ = sec.id ORDER BY sec.count");
			// 转化为String类型的JSON格式
			String str = JSON.toJSONString(uMapList);
			out.print(str);
		} else if(resourceType.equals("4")){
			
		}
	}
	
	public void goToReadRequestProcessAct(ActionRequest actionRequest, ActionResponse actionResponse) {
		String taskId = ParamUtil.getString(actionRequest, "processTaskIdForRequest");
		actionRequest.getPortletSession().setAttribute("taskId", taskId, PortletSession.APPLICATION_SCOPE);
		TaskService taskService = ActivitiUtil.getProcessEngine().getTaskService();
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult(); 
//		String requestOperator = ParamUtil.getString(actionRequest, "requestOperator");
		int operator = 1;
		if (task != null) {		// 为null说明在 excution 没有此数据，说明已完成
			operator = 0;
		}
		Object processForm = activitiFormUtil.getformByTaskId(taskId, operator);
		List<Map<String, String>> myHisList = activitiHistoryYtil
				.getHistorySignatureOpinion(activitiHistoryYtil.getProcessInstanceIdByTaskId(taskId), taskId, operator);
		actionRequest.getPortletSession().setAttribute("processHisOpinionsForRequest", myHisList,
				PortletSession.APPLICATION_SCOPE);
		actionRequest.getPortletSession().setAttribute("processFormForRequest", processForm,
				PortletSession.APPLICATION_SCOPE);
		actionResponse.setRenderParameter("mvcPath", "/html/process/HistoryProcView.jsp");
	}


	/**
	 * 获得流程图的方法
	 * @param actionRequest
	 * @param actionResponse
	 */
	public void goProcessPngJSP(ActionRequest actionRequest, ActionResponse actionResponse) {
		// 获得任务Id
		String taskId = (String) actionRequest.getPortletSession().getAttribute("taskId",
				PortletSession.APPLICATION_SCOPE);
		// 根据任务获取流程实例
		HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
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
