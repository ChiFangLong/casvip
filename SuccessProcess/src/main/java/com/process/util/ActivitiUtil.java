package com.process.util;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.liferay.portal.kernel.util.PropsUtil;

public class ActivitiUtil {
	
	private static ProcessEngine processEngine = getProcessEngine();
	// 加载配置文件
	private static ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
	// 获得映射对象
	private static JdbcTemplate jdbcTemplate = (JdbcTemplate) ac.getBean("jdbcTemplate");
	private static ProcessEngineConfiguration processEngineConfiguration;
	public static Integer pageSize = 5;
	
	public static ProcessEngine getProcessEngine() {
		if (processEngine == null) {
			//获取config对象
	        processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
	        //Jdbc设置
	        String jdbcDriver = PropsUtil.get("jdbc.activiti.driverClassName");
	        processEngineConfiguration.setJdbcDriver(jdbcDriver);
	        String jdbcUrl = PropsUtil.get("jdbc.activiti.url");
	        processEngineConfiguration.setJdbcUrl(jdbcUrl);
	        String jdbcUsername = PropsUtil.get("jdbc.activiti.username");
	        processEngineConfiguration.setJdbcUsername(jdbcUsername);
	        String jdbcPassword = PropsUtil.get("jdbc.activiti.password");
	        processEngineConfiguration.setJdbcPassword(jdbcPassword);
//	          public static final String DB_SCHEMA_UPDATE_FALSE = "false";//不自动创建新表
	        //设置是否自动更新
	        processEngineConfiguration
	                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
	        //获取引擎对象
	        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
//			processEngine = ProcessEngines.getDefaultProcessEngine();
	        return processEngine;
		}
			return processEngine;
	}
	
	public static ProcessEngineConfiguration getProcessEngineConfiguration(){
		return processEngineConfiguration;
	}
	
	/**
	 * 查询流程信息
	 * @param screenName 查询人的名字
	 * @return 表格内容
	 */
	public static StringBuffer pageProcess(PortletRequest resourceRequest,String screenName,Integer currentPage, String searchType,String title,String numbder,String processType,String startPeople,String startTime,String startTime2,String endTime,String endTime2){
		Integer pageSize = ActivitiUtil.pageSize; // 每页显示的数量
		StringBuffer tbodyContent =pageList(resourceRequest,screenName,currentPage,pageSize,searchType,title, numbder, processType, startPeople, startTime, startTime2, endTime, endTime2);
		return tbodyContent;
	}
	
	/**
	 * 分页显示数据
	 * @param screenName	当前用户
	 * @param currentPage	当前页码数
	 * @param pageSize		每页显示数量
	 * @return				数据
	 */
	public static StringBuffer pageList(PortletRequest resourceRequest, String screenName, Integer currentPage, Integer pageSize,  String searchType,String title,String numbder,String processType,String startPeople,String startTime,String startTime2,String endTime,String endTime2){
		ProcessEngine engine = processEngine;
		HistoryService historyService = engine.getHistoryService();
		// 查询数据库内容
		StringBuffer sql = new StringBuffer("SELECT * FROM `act_hi_taskinst` where PROC_INST_ID_ in (select PROC_INST_ID_ from act_hi_identitylink where USER_ID_ = '"+screenName+"') ");
		StringBuffer sqlCount = new StringBuffer("SELECT count(1) FROM `act_hi_taskinst` where PROC_INST_ID_ in (select PROC_INST_ID_ from act_hi_identitylink where USER_ID_ = '"+screenName+"') ");
		// 数据筛选
		if(emptyValue(title)){						// 标题  ==>  Name_
			sql.append(" and NAME_ like '%"+title+"%'");	
			sqlCount.append(" and NAME_ like '%"+title+"%'");
		}
		if(emptyValue(numbder)){				// 编号  ==>  Id
			sql.append(" and ID_ like '%"+numbder+"%'");	
			sqlCount.append(" and ID_ like '%"+numbder+"%'");	
		}
		if(!"0".equals(processType) && emptyValue(processType)){			// 类型  ==>   部署表的Name
			sql.append(" and PROC_DEF_ID_ = (select ID_ from act_re_procdef where DEPLOYMENT_ID_ = "+processType+")");
			sqlCount.append(" and PROC_DEF_ID_ = (select ID_ from act_re_procdef where DEPLOYMENT_ID_ = "+processType+")");
		}
		if(emptyValue(startPeople)){				// 创建人  ==>  Assignee
			sql.append(" and ASSIGNEE_ like '%"+startPeople+"%'");
			sqlCount.append(" and ASSIGNEE_ like '%"+startPeople+"%'");
		}
		if(emptyValue(startTime)){				// 创建时间  ==> START_TIME_
			sql.append(" and (START_TIME_ > '"+startTime+"' and START_TIME_ < '"+startTime2+"')");
			sqlCount.append(" and (START_TIME_ > '"+startTime+"' and START_TIME_ < '"+startTime2+"')");
		}
		if(emptyValue(endTime)){					// 接受日期  ==>  END_TIME_
			sql.append(" and (END_TIME_ > '"+endTime+"' and END_TIME_ < '"+endTime2+"')");
			sqlCount.append(" and (END_TIME_ > '"+endTime+"' and END_TIME_ < '"+endTime2+"')");
		}
		List<HistoricTaskInstance> list = null;
		// 分类筛选
		if(("已完成").equals(searchType)){
			sql.append(" and END_TIME_ is not NULL");
			sqlCount.append(" and END_TIME_ is not NULL");
		} else if(("未完成").equals(searchType)){
			sql.append(" and END_TIME_ is NULL");
			sqlCount.append(" and END_TIME_ is NULL");
		}
		
		// 数据总数量
		Integer totalCount = (Integer)jdbcTemplate.queryForObject(sqlCount.toString(), Integer.class);	// 数据总数量
		
		StringBuffer tbodyContent = new StringBuffer();
		
		/**
		 * 分页处理
		 */
		// 总页数   pageSize:页面数量
		Integer totalPages = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1 ;
		
		if(currentPage < 1){
			currentPage = 1;
		}else if(currentPage > totalPages && totalPages > 0){
			currentPage = totalPages;
		}
		Integer firstNum = (currentPage - 1) * pageSize;
		
		// 数据获取
		list = historyService.createNativeHistoricTaskInstanceQuery().sql(sql.toString()).listPage(firstNum, pageSize);
		
		for (HistoricTaskInstance item : list) {
			String piId = item.getProcessInstanceId();
			HistoricProcessInstance historyProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(piId).singleResult();
			
			String processName = historyProcessInstance.getProcessDefinitionName();
			String user = historyProcessInstance.getStartUserId();
			String createDate = new SimpleDateFormat("yyyy-MM-dd").format(historyProcessInstance.getStartTime());
			String processInstanceId = item.getId();
			String endDate = "";
			String notDoPeople = "";
			if(historyProcessInstance.getEndTime() != null){
				endDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(historyProcessInstance.getEndTime());
				notDoPeople = "(已完结)";
			} else{
				endDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(historyProcessInstance.getStartTime());
				notDoPeople = item.getAssignee();
			}
			String nowTask = item.getName();
			String titles = processName + "-" + user + "-" + createDate;
			tbodyContent.append("<tr><td><a href='javascript:void(0)'>"+processName+"</a><input type='hidden' value='"+processInstanceId+"'></td><td>"+endDate+"</td><td>"+nowTask+"</td><td>"+notDoPeople+"</td></tr>");
		}
		resourceRequest.getPortletSession().setAttribute("setTotalCount", totalCount, PortletSession.APPLICATION_SCOPE);//数据总数量
		resourceRequest.getPortletSession().setAttribute("totalPages", totalPages, PortletSession.APPLICATION_SCOPE);//总页数
		return tbodyContent;
	}
	
	
	/**
	 * 非空验证
	 * @param val 需要验证的参数
	 * @return	null返回false  反则返回true
	 */
	public static boolean emptyValue(String val){
		if(!"".equals(val) && val != null){
			return true;
		} else{
			return false;
		}
	}
}
