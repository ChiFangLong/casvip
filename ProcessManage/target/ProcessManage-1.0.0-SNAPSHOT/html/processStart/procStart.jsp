<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" import="javax.portlet.ActionRequest"%>
<portlet:defineObjects />
<c:set var="path" value="<%=request.getContextPath()%>" />

<portlet:resourceURL var="getAllType" />
<portlet:actionURL var="goFormHtml" name="goFormHtml">
</portlet:actionURL>
<!DOCTYPE html>
<html>
<link href="${path }/css/bootstrap.min.css" rel="stylesheet"
	media="screen">
<link rel="stylesheet" type="text/css"
	href="${path }/css/MyCss/index.css" />
<link rel="stylesheet" type="text/css" href="${path }/css/procStart.css" />
</head>
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

<body>
	<div class="container-fluid">
		<div style="margin-bottom: 2%;" class="row-fluid" id="topImg">
			<div class="Img_content">
				<img src="${path }/img/纸飞机%20(2).png" />
				<p>发起流程</p>
			</div>
			<div class="top_dataType">
				<a href="">全部流程</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp; <a href="">我的收藏</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;
				<a href="">常用流程</a>&nbsp;&nbsp;
			</div>
		</div>
		<div class="row-fluid">
			<div class="span3 lspan3 content_line"></div>
			<div class="span3 lspan3 content_line"></div>
			<div class="span3 lspan3 content_line"></div>
			<div class="span3 lspan3 content_line">
				<!-- <div class="top_connent">
					<p class="title">
						<span class="titleContent"> <img src="img/个人信息.png">
							<span class="typeName">风险管理</span>
						</span>
					</p>
					<div class="processDiv">
						<p class="processClass">
							<a href="#"><img src="img/右箭头%20(1).png" /><span
								class="processName">风险识别统计表</span></a>
						</p>
					</div>
				</div> -->
			</div>
		</div>
	</div>

</body>
<script src="${path }/js/jquery-1.9.1.min.js"></script>
<script src="${path }/js/bootstrap.min.js"></script>
<script>
	$(function() {
		
		var a = "${alertContent}";
		if(a.length > 0){
			if(a == "成功"){
				alert("恭喜,您已经成功发起流程!");
				
			} else {
				alert("流程发起有误,请重新尝试");
			}
			<%
				request.getSession().removeAttribute("alertContent");
			%>
		}
		getAllType();
		
		$(".processA").on("click",function(){
			// 获取点击的流程Id
			var processDefinitionId = $(this).parents("p").find("input").val();
		});
	})

	function getAllType() {
		$.ajaxSettings.async = false;
		var str = "";
		$.getJSON("${getAllType}","eid=0",
						function(data) {
							$(data).each(function(index, result) {
												str = "";
												var name = result["NAME_"];
												var typeCount = result["count"];
												var deploymentId = result["id"];
												str += "<div class='top_connent'> "
														+ "	<p class='title'> "
														+ " <span class='titleContent'><img src='${path}/img/个人信息.png'> <span class='typeName'>"
														+ name
														+ "</span>"
														+ "</span> " + "	</p>";
												$.getJSON("${getAllType}","eid=1&deploymentId="+ deploymentId,function(nameData) {
																	str += "<div class='processDiv'>";
																	$(nameData).each(function(nameIndex,nameResult) {
																		 				var id = nameResult["id"];
																						str += "<p class='processClass'>"
																								+ "<a href='${goFormHtml}&proId="+id+"' class='processA'><img src='${path }/img/右箭头 (1).png' /><span "
																								+"class='processName' >"
																								+ nameResult["name"] + "</span></a>"
																								+"<input type='hidden'  class='processId' value='"+id+"'/>"
																								+ "</p>";
																					});
																	str += "</div>";
																	str += "</div>";
																	if (index < 4) {
																		$(".lspan3").eq(index).append(str);
																	} else {
																		var num = index % 4;
																		$(".lspan3").eq(num).append(str);
																	}
																});
											});
						});
		$.ajaxSettings.async = true;
	}
	
</script>
</html>