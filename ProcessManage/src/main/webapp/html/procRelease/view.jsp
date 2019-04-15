<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="path" value="<%=request.getContextPath() %>" />

<portlet:defineObjects />
<portlet:renderURL var="deployJsp">
	<portlet:param name="mvcPath" value="/html/procRelease/deploy.jsp"/>
</portlet:renderURL>

<portlet:resourceURL var="getData" />

<body>
	<h4>ProcessList</h4>
	<table border="1" style="width:100%;border-collapse:collapse;border-spacing:0;" >
		<tr align="center" style="color:red;font-weight:bold;" height="35px">
			<td width="25%">流程定义ID</td>
			<td width="8%">部署ID</td>
			<td width="24%">流程定义名称</td>
			<td width="17%">流程定义KEY</td>
			<td width="10%">版本号</td>
			<td width="8%">BPMN</td>
			<td width="8%">图片资源</td>
		</tr>
		<tbody id="t_tbody">
			
		</tbody>
		<%-- <c:forEach var="item" items="${ProcessDefinition }">
			<tr align="center" height="30px">
				<td>${item.getId() }</td>
				<td>${item.getDeploymentId() }</td>
				<td>${item.getName() }</td>
				<td>${item.getKey() }</td>
				<td>${item.getVersion() }</td>
				<td><input type="text" value="${item.getResourceName() }" /></td>
				<td><input type="text" value="${item.getDiagramResourceName() }" /></td>
			</tr>
		</c:forEach> --%>
	</table>
	<p style="margin-left:20px;margin-top: 20px">
		<a href="${deployJsp }"><button style="width:110px;border-radius:5px;border:0px;height:30px;background-color:burlywood;" type="button">部署资源</button></a>
	</p>
	
	<script type="text/javascript" src="${path }/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript">
		$(function(){
			$.getJSON("${getData}", function(data){
				$(data).each(function(index,item){
					$("#t_tbody").append("<tr align='center' height='30px'><td>"+item.id+"</td><td>"+item.deploymentId+"</td><td>"+item.name+"</td><td>"+item.key+"</td><td>"+item.version+"</td><td><input type='text' value='"+item.resourceName+"' /></td><td><input type='text' value='"+item.diagramResourceName+"' /></td></tr>");
				})
			})
		})
	</script>
</body>