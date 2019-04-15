package com.process.util;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.liferay.portal.kernel.util.PropsUtil;

public class ActivitiUtil {
	
	private static ProcessEngine processEngine = getProcessEngine();
	
	private static JdbcTemplate jdbcTemplate = getJdbcTemplate();
	
	private static ProcessEngineConfiguration processEngineConfiguration;
	private static Integer pageSize = 5;
	
	
	public static JdbcTemplate getJdbcTemplate(){
		if(jdbcTemplate == null){
			String jdbcDriver = PropsUtil.get("jdbc.activiti.driverClassName");
	        String jdbcUrl = PropsUtil.get("jdbc.activiti.url");
	        String jdbcUsername = PropsUtil.get("jdbc.activiti.username");
	        String jdbcPassword = PropsUtil.get("jdbc.activiti.password");
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
		    dataSource.setDriverClassName(jdbcDriver);
		    dataSource.setUrl(jdbcUrl);
		    dataSource.setUsername(jdbcUsername);
		    dataSource.setPassword(jdbcPassword);
		    
		    // 创建JDBC模板
		    jdbcTemplate = new JdbcTemplate();
		    // 这里也可以使用构造方法
		    jdbcTemplate.setDataSource(dataSource);
		    
		    return jdbcTemplate;
		} else{
			return jdbcTemplate; 
		}
	}
	
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
	        return processEngine;
		}else{
			return processEngine;
		}
	}
	
	public static ProcessEngineConfiguration getProcessEngineConfiguration(){
		return processEngineConfiguration;
	}
	
	/**
	 * 查询流程信息
	 * @param screenName 查询人的名字
	 * @return 表格内容
	 */
	public static StringBuffer pageProcess(PortletRequest resourceRequest,String screenName,Integer currentPage, String searchType){
		Integer pageSize = ActivitiUtil.pageSize; // 每页显示的数量
		StringBuffer tbodyContent =pageList(resourceRequest,screenName,currentPage,pageSize,searchType);
		return tbodyContent;
	}
	
	/**
	 * 分页显示数据
	 * @param screenName	当前用户
	 * @param currentPage	当前页码数
	 * @param pageSize		每页显示数量
	 * @return				数据
	 */
	public static StringBuffer pageList(PortletRequest resourceRequest, String screenName, Integer currentPage, Integer pageSize,  String searchType){
		ProcessEngine engine = processEngine;
		HistoryService historyService = engine.getHistoryService();
		// 查询数据库内容
		StringBuffer sql = new StringBuffer("SELECT * FROM `act_hi_taskinst` where PROC_INST_ID_ in (select PROC_INST_ID_ from act_hi_identitylink where USER_ID_ = '"+screenName+"') ");
		StringBuffer sqlCount = new StringBuffer("SELECT count(1) FROM `act_hi_taskinst` where PROC_INST_ID_ in (select PROC_INST_ID_ from act_hi_identitylink where USER_ID_ = '"+screenName+"') ");
		// 数据筛选
		List<HistoricTaskInstance> list = null;
		// 分类筛选
		if(("已完成").equals(searchType)){
			sql.append(" and END_TIME_ is not NULL");
			sqlCount.append(" and END_TIME_ is not NULL");
		} else if(("未完成").equals(searchType)){
			sql.append(" and END_TIME_ is NULL");
			sqlCount.append(" and END_TIME_ is NULL");
		}
		sql.append(" order by START_TIME_ desc");
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
//			String user = historyProcessInstance.getStartUserId();
//			String createDate = new SimpleDateFormat("yyyy-MM-dd").format(historyProcessInstance.getStartTime());
			String processInstanceId = item.getId();
			String endDate = "";
			String notDoPeople = "";
			// 判断任务是否为完结
			if(historyProcessInstance.getEndTime() != null){
				endDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(historyProcessInstance.getEndTime());
				notDoPeople = "(已完结)";
			} else{
				endDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(historyProcessInstance.getStartTime());
				notDoPeople = item.getAssignee();
			}
			String nowTask = item.getName();
//			String titles = processName + "-" + user + "-" + createDate;
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
