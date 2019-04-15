<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<h3><em>流程模板部署</em></h3>

<portlet:actionURL var="uploadURL" name="deployUploadAction" />
            
	<form action="${uploadURL }" method="post" enctype="multipart/form-data" style="margin-top:1em;">
       <input type="file" name="file" /><br><br>
      	 部署名: <input type="text" name="<portlet:namespace/>depName" /><br><br>
       <p>
       		<input style="width:120px;border-radius:5px;border:0px;height:35px;font-weight: bold;" type="submit" value="文件部署" class="btn" />
       		
       		<input style="width:80px;border-radius:5px;border:0px;height:35px;font-weight: bold;" onclick="javascript:history.back(-1);" type="button" value="返回">
       </p>
	</form>
<hr/>