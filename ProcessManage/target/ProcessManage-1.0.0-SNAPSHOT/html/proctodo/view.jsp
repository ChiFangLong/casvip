<%@ include file="init.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<portlet:actionURL var="requestSubmission" name="requestSubmission"/>

<portlet:resourceURL var="getData" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<link rel="stylesheet" type="text/css" href="${path}/css/bootstrap.min.css"/>
	    <link rel="stylesheet" type="text/css" href="${path}/css/procTodo.css?v=<%=Math.random() %>"/>
	    <link rel="stylesheet" type="text/css" href="${path}/css/page.css?v=<%=Math.random() %>"/>
		<title>待办流程</title>
	</head>
	<style>
		.aui .table td{
			background-color: white;
			line-height: 35px;
		}
		#t_tbody tr td a{
			color: #0042FF;
		}
		#t_tbody tr td a:hover{
			color: #C5243A;
			text-decoration: none;
		}
	</style>
	<body>
		<div class="container-fluid">
			<div style="margin-bottom: 2%;" class="row-fluid" id="topImg">
				<div class="Img_content">
					<img src="${path}/img/系统日志.png" />
					<p>待办流程</p>
				</div>
			</div>
			<div class="row-fluid">
				<table id="dataTable" class="table">
					<thead>
						<tr>
							<td width="25%">请求标题</td>
							<td width="25%">创建人</td>
							<td width="25%">创建日期</td>
							<td width="25%">操作</td>
						</tr>
					</thead>
					<tbody id="t_tbody">
					</tbody>
				</table>
			</div>
		</div>
		<div style="text-align: center;margin:50px auto;">
			<div id="pager" class="pager clearfix">
			</div>
		</div>
	</body>
	
	<script src="${path}/js/jquery.z-pager.js?v=<%=Math.random() %>" type="text/javascript"  charset="utf-8"></script>
	<script type="text/javascript">
		$(function(){
			getData(1);
		});
		
		function getData(page){
			$.ajaxSettings.async = false;
        	$.getJSON("${getData}", "resourceNum=2&page="+page, function(data){
        		if(data !=null && data.length > 0){
        			//$("#t_tbody").html(data);
        			var content = "";
        			$(data).each(function(index,result){
        				var str = "<tr><td>"+result["processName"]+"</td><td>"+result["processCreator"]+"</td><td>"+result["processCreateTime"]+"</td><td><a href='${requestSubmission}&processTaskId="+result["processTaskId"]+" '><input type='button'  value='查看'/></a></td></tr>";
        				content += str;
        			})
        			console.log(content);
        			$("#t_tbody").html(content);
        			$.post("${getData }","resourceNum=3",function(data){
            			var setTotalCount = data;
                		pageFunction(setTotalCount);
            		})
        		} else{
        			var str = "<tr><td style='color:red;'>该用户暂无相关流程信息</td><td></td><td></td><td></td><td></td><td></td></tr>";
        			$("#t_tbody").html(str);
        		}
        	});
        	$.ajaxSettings.async = true;
		}
		
		function getData2(page){
			$.ajaxSettings.async = false;
        	$.getJSON("${getData}", "resourceNum=2&page="+page, function(data){
        		if(data !=null && data.length > 0){
        			//$("#t_tbody").html(data);
        			var content = "";
        			$(data).each(function(index,result){
        				var str = "<tr><td>"+result["processName"]+"</td><td>"+result["processCreator"]+"</td><td>"+result["processCreateTime"]+"</td><td><a href='${requestSubmission}&processTaskId="+result["processTaskId"]+" '><input type='button'  value='查看'/></a></td></tr>";
        				content += str;
        			})
        			$("#t_tbody").html(content);
        		} else{
        			var str = "<tr><td style='color:red;'>该用户暂无相关流程信息</td><td></td><td></td><td></td><td></td><td></td></tr>";
        			$("#t_tbody").html(str);
        		}
        	});
        	$.ajaxSettings.async = true;
		}
		
		function pageFunction(totalData){
			// 分页插件
			$("#pager").zPager({
				totalData: totalData,
				htmlBox: $('#wraper'),
				btnShow: true,
				ajaxSetData: false,
				dataRender: function(data) {
					console.log(data + '---data-2');
				},
			});
		}
		
		
	</script>
</html>