<%@ include file="init.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<portlet:actionURL var="goToReadRequestProcess"
	name="goToReadRequestProcessAct" />
<portlet:resourceURL var="getData" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<link rel="stylesheet" type="text/css" href="${path}/css/bootstrap.min.css"/>
	    <link rel="stylesheet" type="text/css" href="${path}/css/procRequest.css?v=<%=Math.random() %>"/>
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
					<img src="${path}/img/数据.png" />
					<p>我的请求</p>
				</div>
				<div class="top_dataType">
					<a style="color:red;" id="allData" class="typeButton" href="javascript:void(0)">全部</a>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
					<a class="typeButton" id="startData" href="javascript:void(0)">未完成</a>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
					<a class="typeButton" id="endData" href="javascript:void(0)">已完成</a>&nbsp;&nbsp;
				</div>
			</div>
			<div class="row-fluid">
				<table id="dataTable" class="table">
					<thead>
						<tr>
							<td width="20%">请求标题</td>
							<td width="20%">创建人</td>
							<td width="20%">创建日期</td>
							<td width="20%">未操作人</td>
							<td width="20%">操作</td>
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
		var searchType;
		$(function(){
			getData(1);
		});
		
		function getData(page){
			$.ajaxSettings.async = false;
        	$.getJSON("${getData}", "resourceNum=1&page="+page+"&searchType="+searchType, function(data){
        		if(data !=null && data.length > 0){
        			//$("#t_tbody").html(data);
        			var content = "";
        			$(data).each(function(index,result){
        				var str = "<tr><td>"+result["processName"]+"</td><td>"+result["processCreator"]+"</td><td>"+result["processCreateTime"]+"</td><td><a href='${requestSubmission}&processTaskId="+result["processTaskId"]+" '><input type='button'  value='查看'/></a></td></tr>";
        				content += str;
        			})
        			console.log(content);
        			$("#t_tbody").html(content);
        			$.post("${getData }","resourceNum=2",function(data){
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
        	$.getJSON("${getData}", "resourceNum=1&page="+page+"&searchType="+searchType, function(data){
        		if(data !=null && data.length > 0){
        			var content = "";
        			$(data).each(function(index,result){
        				var str = "<tr><td>"+result["processName"]+"</td><td>"+result["processCreator"]+"</td><td>"+result["processCreateTime"]+"</td><td>"+result["processNextAssignee"]+"</td><td><a href='${goToReadRequestProcess}&taskId="+result["processTaskId"]+" '><input type='button'  value='查看'/></a></td></tr>";
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
		
		/* 进行分类(已完成、未完成) */
		$("#allData").click(function(){
        	searchType = $(this).text();
        	getData(1);
        });
		$("#startData").click(function(){
			searchType = $(this).text(); 
			getData(1);
        });
		$("#endData").click(function(){
			searchType = $(this).text(); 
			getData(1);
        });
		
		 /* 切换显示数据类型 */
		$(".top_dataType a").click(function(){
			$(".top_dataType a").css("color","#6C6B70");
			$(this).css("color","red").css("text-decoration","none");
		});
		
	</script>
</html>