<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<portlet:defineObjects />
<c:set var="path" value="<%=request.getContextPath()%>" />
<portlet:actionURL var="startProcess" name="startProcess" />
	
<!DOCTYPE html>
<html>
<link rel="stylesheet" type="text/css"
	href="${path }/css/MyCss/index.css" />
<link href="${path }/css/bootstrap.min.css" rel="stylesheet" media="screen">
<style>
	.aui .row {
		margin-left: 0px;
	}
</style>

<body>
<div class="row" >
	<div class="span3 offset3">
		<form  method="post" class="form-horizontal" id="proForm" action="${startProcess }"> 
            <input type="hidden" name="procDefId" value="${procDefId}"/>
            <input type="hidden" name="data"  id="data"/>
            <div class="control-group">
					<label class="control-label" for="inputEmail">流程名称</label>
					<div class="controls">
						<input  type="text"  name="procName" readonly="readonly" value="${proc.name}" 
                            class="width-100 form-control"  />
					</div>
				</div>
		
            <c:forEach items="${list}" var="prop">
                <!--设置隐藏域，统一处理-->
                <div class="control-group">
                    <div class="control-label" >${prop.name}</div>
                    <div class="controls">
                        <c:choose>
                            <c:when test="${prop.type.name=='string'}">
                                <input  type="text" id="${prop.id}" name="${prop.id}" placeholder="${prop.value}" 
                                        class="width-100 form-control"  />
                            </c:when>
                            <c:when test="${prop.type.name=='long'}">
                                <input  type="text" id="${prop.id}" name="${prop.id}" placeholder="${prop.value}" 
                                        class="width-100 form-control"  />
                            </c:when>
                            <c:when test="${prop.type.name=='date'}">
                                <div class="input-group">
                                    <input  type="date" id="${prop.id}"  name="${prop.id}" value="${prop.value}"  
                                            class="width-100 form-control date-picker-o"  data-date-format="${prop.type.getInformation('datePattern')}"/>
                                </div>
                            </c:when>
                            <c:when test="${prop.type.name=='enum'}">
                                <select id="${prop.id}" name="${prop.id}">
                                    <option value="-1">----请选择---</option>
                                    <c:forEach  items="${prop.type.getInformation('values')}" var="item">
                                        <option value="${item.key}">${item.value}</option>
                                    </c:forEach>
                                </select>
                            </c:when>
 
                            <c:when test="${prop.type.name=='boolean'}">
                                <input  type="checkbox" id="${prop.id}" name="${prop.id}" value="${prop.value}" />
                            </c:when>
                        </c:choose>

                    </div>
                </div>
            </c:forEach>
           	<div class="control-group">
				<div class="controls">
					<button class="btn btn-primary" id="startBtn">启动流程</button>
				</div>
			</div>
        </form>
	</div>
</div>
</body>
<script src="http://code.jquery.com/jquery.js"></script>
<script src="${path }/js/bootstrap.min.js"></script>
<script>
	$(function(){
		$("#startBtn").click(function(){
			jquerySerializeUrl();
		});
	})
	
	function jquerySerializeUrl(){
		var serialzeUrl = $("#proForm").serializeArray();
		$("#data").val(serialzeUrl);
	}
</script>
</html>