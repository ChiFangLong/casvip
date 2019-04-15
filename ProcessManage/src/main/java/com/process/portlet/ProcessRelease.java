package com.process.portlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.process.util.ActivitiUtil;

/**
 * Portlet implementation class SearchPortlet
 */
public class ProcessRelease extends MVCPortlet {
	ProcessEngine engine = ActivitiUtil.getProcessEngine();
	RepositoryService rs = engine.getRepositoryService();
	
	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {
		super.serveResource(resourceRequest, resourceResponse);
		List<ProcessDefinition> list = rs.createProcessDefinitionQuery().orderByProcessDefinitionVersion().desc().list();
		PrintWriter out = resourceResponse.getWriter();     
		String str = JSON.toJSONString(list, SerializerFeature.IgnoreNonFieldGetter);
		out.print(str);
	}

	public void deployUploadAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception{
        UploadPortletRequest upr = PortalUtil.getUploadPortletRequest(actionRequest);
        String fileFieldName = "file";
        
        if (upr.getSize(fileFieldName) == 0)
        {
            throw new Exception("空文件!");
        }
        String fileName = upr.getFileName(fileFieldName);
        try {
        	String depName = ParamUtil.getString(actionRequest, "depName");
    		String depKey = depName + "_Key";
            // 得到输入流（字节流）对象
            InputStream fileInputStream = upr.getFileAsStream(fileFieldName);//file.getInputStream();
            // 文件的扩展名
//            String extension = FilenameUtils.getExtension(fileName);
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            // zip或者bar类型的文件用ZipInputStream方式部署
            DeploymentBuilder deployment = rs.createDeployment();
            if (extension.equals("zip") || extension.equals("bar")) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                deployment.name(depName).key(depKey).addZipInputStream(zip);
            } else if (extension.equals("xml") || extension.equals("bpmn")){
                // xml类型的文件
                deployment.name(depName).key(depKey).addInputStream(fileName, fileInputStream);
            }
            deployment.deploy();
            System.out.println(fileName +" deploy success!");
        } catch (Exception e) {
            System.out.println(fileName + " error on deploy process, because of file input stream");
        }

        // 重新获取数据
        List<ProcessDefinition> list = rs.createProcessDefinitionQuery().orderByProcessDefinitionVersion().desc().list();
		actionRequest.getPortletSession().setAttribute("ProcessDefinition", list,PortletSession.APPLICATION_SCOPE);
    }
	
	
}
