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
<portlet:renderURL var="goToreutrn">
	<portlet:param name="mvcPath" value="/html/proctodo/view.jsp" />
</portlet:renderURL>

<portlet:actionURL var="goToreutrnAct" name="goToreutrnAct">
	<portlet:param name="mvcPath" value="/html/proctodo/view.jsp"/>
</portlet:actionURL>

<portlet:actionURL var="goProcessPngJSP" name="goProcessPngJSP">
	<portlet:param name="mvcPath" value="/html/proctodo/showProcessPng.jsp"/>
</portlet:actionURL>

<div class="container-fluid">
		<div style="margin-bottom: 2%;" class="row" id="topImg">
			<div class="Img_content">
				<img src="${path}/img/icon.png" />
				<p>&nbsp;&nbsp;正在处理&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="${goProcessPngJSP }">流程图</a></p>
			</div>
		</div>
		<div class="row">
			<div class="span4 lspan4"></div>
			<div class="span4 lspan4"></div>
			<div class="span4 lspan4"></div>
		</div>
	</div>
	<form id="UpdateForm">
		<div>${processForm}</div>
		<div>
			<p>签字意见：(此处要设置为必填！)</p><br>
			<input type="text" name="signatureOpinion" >
			<input id="submit" type="button" value="提交">
			<input onclick="javascript:history.back(-1);" type="button" value="返回">
		</div>
	</form>
<div>
<table>
<tr>
<th>流转意见</th>
</tr>
<c:forEach items="${processHisOpinions}" var="hisOpinion">
	<tr>
		<td>${hisOpinion.assignee}</td>
		<td></td><td></td><td></td><td></td><td></td><td></td>
		<td>${hisOpinion.EndTime}</td>
		<td></td><td></td><td></td><td></td><td></td><td></td>
		<td>${hisOpinion.signatureOpinion}</td>
	</tr>
</c:forEach>
</table>
</div>

<script>
$(function() {
  $('#submit').click(function() {
    var d = {};
    var t = $('form').serializeArray();
    $.each(t, function() {
      d[this.name] = this.value;
    });
    var data=JSON.stringify(d);
    $.post("${goToSubmitUrl}&resourceNum="+1, 
    		{"<portlet:namespace/>NewForm":data}, 
    		function() {
    			window.location.href="<%=goToreutrn%>";
			});
		});
	});

	
	
</script>