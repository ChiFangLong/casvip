<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<portlet:defineObjects />
<c:set var="path" value="<%=request.getContextPath()%>" />
<portlet:actionURL var="startProcess" name="startProcess" />
	
<!DOCTYPE html>
<html>
<link href="${path }/css/bootstrap.min.css" rel="stylesheet" />
<style>
	.aui .row {
		margin-left: 0px;
	}
	#startBtn{
		float: right;
	}
	.aui .table td{
		background-color: white;
	}
	#proForm table td{
		font-family: "微软雅黑";
		font-size: 14px;
		color: black;
	}
</style>

<body>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span4 offset4">
				<form  method="post" class="form-horizontal" id="proForm" action="${startProcess }"> 
		            <input type="hidden" name="procDefId" value="${procDefId}"/>
		            <input type="hidden" name="data" id="data"/>
						${htmlContent }
		            <input type="submit" class="btn btn-primary" id="startBtn" value="提交" />
		        </form>
			</div>
		</div> 
	</div> 
</body>
<script src="${path }/js/jquery-1.9.1.min.js"></script>
<script src="${path }/js/bootstrap.min.js"></script>
<script>
	$(function(){
		$("#startBtn").click(function(){
			jquerySerializeUrl();
		});
	})
	
	function jquerySerializeUrl(){
		var serialzeUrl = JSON.stringify($("#proForm").serializeArray());
		$("#data").val(serialzeUrl);
	}
</script>
</html>