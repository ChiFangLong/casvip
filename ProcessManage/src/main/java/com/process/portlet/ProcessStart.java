// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProcessPortlet.java

package com.process.portlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.process.util.ActivitiUtil;

public class ProcessStart extends MVCPortlet
{
	
    public ProcessStart()
    {
        engine = ActivitiUtil.getProcessEngine();
        formService = engine.getFormService();
        rs = engine.getRepositoryService();
        runtimeService = engine.getRuntimeService();
        identityService = engine.getIdentityService();
        
        jdbcTemplate = ActivitiUtil.getJdbcTemplate();
    }
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
        throws IOException, PortletException
    {
        super.doView(renderRequest, renderResponse);
    }
    
    @SuppressWarnings("rawtypes")
    public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
        throws IOException, PortletException
    {
        PrintWriter out = resourceResponse.getWriter();
        long eid = ParamUtil.getLong(resourceRequest, "eid");
        String str = "";
        if(eid == 0L)
        {
//            ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
//            JdbcTemplate jdbcTemplate = (JdbcTemplate)ac.getBean("jdbcTemplate");
            List uMapList = jdbcTemplate.queryForList("SELECT sec.id,NAME_,sec.count  FROM act_re_deployment `fir` INNER JOIN (SELECT deployment_id_ id,COUNT(deployment_id_) `count` FROM `act_re_procdef` a WHERE deployment_id_ IN (SELECT id_ FROM act_re_deployment a INNER JOIN (SELECT MAX(DEPLOY_TIME_) `time`  FROM act_re_deployment WHERE name_ IS NOT NULL GROUP BY NAME_) b ON a.DEPLOY_TIME_ = b.time) GROUP BY deployment_id_) sec ON fir.id_ = sec.id ORDER BY sec.count");
            str = JSON.toJSONString(uMapList);
        } else
        if(eid == 1L)
        {
            String deploymentId = ParamUtil.getString(resourceRequest, "deploymentId");
            List processList = rs.createProcessDefinitionQuery().deploymentId(deploymentId).list();
            str = JSON.toJSONString(processList, new SerializerFeature[] {
                SerializerFeature.IgnoreNonFieldGetter
            });
        } else
        if(eid == 2L)
        {
            String processDefinitionId = ParamUtil.getString(resourceRequest, "processDefinitionId");
            StartFormData sf = formService.getStartFormData(processDefinitionId);
            ProcessDefinition pro = (ProcessDefinition)rs.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
            resourceRequest.getPortletSession().setAttribute("pro", JSON.toJSONString(pro, new SerializerFeature[] {
                SerializerFeature.IgnoreNonFieldGetter
            }), 1);
            List list = sf.getFormProperties();
            resourceRequest.getPortletSession().setAttribute("list", list, 1);
            if(list != null && list.size() > 0)
                str = JSON.toJSONString(list);
        }
        out.print(str);
    }

    public void goFormHtml(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String processDefinitionId = ParamUtil.getString(actionRequest, "proId");
        ProcessDefinition pro = (ProcessDefinition)rs.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        actionRequest.getPortletSession().setAttribute("procDefId", processDefinitionId, 1);
        actionRequest.getPortletSession().setAttribute("proc", pro, 1);
        Object str = formService.getRenderedStartForm(processDefinitionId);
        actionRequest.getPortletSession().setAttribute("htmlContent", str, 1);
        actionResponse.setRenderParameter("mvcPath", "/html/processStart/fromContent.jsp");
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void startProcess(ActionRequest actionRequest, ActionResponse actionResponse)
        throws ParseException, PortalException, SystemException
    {
        String procDefId = ParamUtil.getString(actionRequest, "procDefId");
        String data = ParamUtil.getString(actionRequest, "data");
        JSONArray json = JSONArray.parseArray(data);
        Map map = new HashMap();
        for(int i = 0; i < json.size(); i++)
        {
            JSONObject o = json.getJSONObject(i);
            if(!o.get("name").toString().equals("data"))
                map.put(o.get("name").toString(), o.get("value").toString());
        }

        String screenName = PortalUtil.getUser(actionRequest).getScreenName();
        identityService.setAuthenticatedUserId(screenName);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, map);
        if(processInstance != null)
            actionRequest.getPortletSession().setAttribute("alertContent", "\u6210\u529F", 1);
        else
            actionRequest.getPortletSession().setAttribute("alertContent", "\u5931\u8D25", 1);
        actionResponse.setRenderParameter("mvcPath", "/html/processStart/procStart.jsp");
    }

    ProcessEngine engine;
    FormService formService;
    RepositoryService rs;
    RuntimeService runtimeService;
    IdentityService identityService;
    JdbcTemplate jdbcTemplate;
}
