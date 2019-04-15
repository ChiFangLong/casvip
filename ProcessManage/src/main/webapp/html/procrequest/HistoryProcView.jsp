<%@ include file="init.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>

/* .row {
			max-width: 100%;
		}
		.container-fluid {
			margin-left: 4%;
		} */
.aui .row {
	margin-left: 0px;
}
</style>
<portlet:resourceURL var="goToSubmitUrl">
	<portlet:param name="processTaskId" value="${toDoProTaskId}" />
</portlet:resourceURL>
<portlet:actionURL var="goToreutrnAct" name="goToreutrnAction" />

<portlet:actionURL var="goProcessPngJSP" name="goProcessPngJSP">
	<portlet:param name="mvcPath" value="/html/procrequest/showProcessPng.jsp"/>
</portlet:actionURL>

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