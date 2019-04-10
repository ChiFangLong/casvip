<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<c:set var="path" value="<%=request.getContextPath()%>" />
<script src="${path}/js/jquery-1.11.1.min.js"></script>
<portlet:defineObjects />

<link rel="stylesheet" type="text/css"
	href="${path}/css/MyCss/index.css" />
<link href="${path}/css/bootstrap.min.css" rel="stylesheet" media="screen">
<style>
.aui .row {
	margin-left: 0px;
}
</style>
<portlet:resourceURL var="goToSubmitUrl">
	<portlet:param name="processTaskId" value="${toDoProTaskId}" />
</portlet:resourceURL>

<portlet:actionURL var="goProcessPngJSP" name="goProcessPngJSP">
	<portlet:param name="mvcPath" value="/html/process/showProcessPng.jsp"/>
</portlet:actionURL>


<portlet:renderURL var="goToreutrnAct">
	<portlet:param name="mvcPath" value="/html/process/view.jsp" />
</portlet:renderURL>

<body>
	<div class="container-fluid">
	<div style="margin-bottom: 2%;" class="row" id="topImg">
		<div class="Img_content">
			<img src="${path}/img/icon.png" />
			<p>&nbsp;&nbsp;我的请求&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="${goProcessPngJSP }">流程图</a></p>
		</div>
	</div>
	<div class="row">
		<div class="span4 lspan4"></div>
		<div class="span4 lspan4"></div>
		<div class="span4 lspan4"></div>
	</div>
</div>
<form id="HistoryForm">
	<div>${processFormForRequest}</div>
</form>
<div>
	<table>
		<tr>
			<th>流转意见</th>
		</tr>
		<c:forEach items="${processHisOpinionsForRequest}" var="hisOpinion">
			<tr>
				<td>${hisOpinion.assignee}</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td>${hisOpinion.EndTime}</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td>${hisOpinion.signatureOpinion}</td>
			</tr>
		</c:forEach>
	</table>
</div>

<div>
	<input onclick="javascript:history.back(-1);" type="button" value="返回">
</div>
</body>