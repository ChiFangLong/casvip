<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file = "/html/init.jsp" %>

<portlet:resourceURL var="getAllSuccessProcess" />
<portlet:actionURL var="goToReadRequestProcessAct" name="goToReadRequestProcessAct" />
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
	    <meta http-equiv="X-UA-Compatible" content="ie=edge">
	    <link rel="stylesheet" type="text/css" href="${path}/css/bootstrap.min.css"/>
	    <link rel="stylesheet" type="text/css" href="${path}/css/procDone.css?v=<%=Math.random() %>"/>
	    <link rel="stylesheet" type="text/css" href="${path}/css/page.css?v=<%=Math.random() %>"/>
		<title>已办流程</title>
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
		<div class="container-fluid" >
			<div style="margin-bottom: 2%;" class="row-fluid" id="topImg">
				<div class="Img_content">
					<img src="${path}/img/文档.png" />
					<p>已办事宜</p>
				</div>
				<div class="top_dataType">
					<a style="color:red;" id="allData" class="typeButton" href="javascript:void(0)">全部</a>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
					<a class="typeButton" id="startData" href="javascript:void(0)">未完成</a>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
					<a class="typeButton" id="endData" href="javascript:void(0)">已完成</a>&nbsp;&nbsp;
				</div>
			</div>
			<div class="row-fluid" id="tableContent">
				<table id="dataTable" class="table">
					<thead>
						<tr>
							<td width="25%">请求标题</td>
							<td width="25%">创建日期</td>
							<td width="25%">当前节点</td>
							<td width="25%">未操作者</td>
						</tr>
					</thead>
					<tbody id="t_tbody">
						<tr>
							<td><a href="#">加班申请表</a></td>
							<td>2019-03-12 14： 20 ：42</td>
							<td>人力资源共享服务中心审核</td>
							<td>显示</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		
		<!-- 分页按钮 -->
		<div style="text-align: center;margin:50px auto;">
			<div id="pager" class="pager clearfix"></div>
		</div>
		
</body>
	<script src="${path}/js/jquery-1.9.1.min.js" type="text/javascript" charset="utf-8"></script>
	<script src="${path}/js/bootstrap.min.js" type="text/javascript" charset="utf-8"></script>
    <script src="${path}/js/done/jquery.z-pager.js?v=<%=Math.random() %>" type="text/javascript"  charset="utf-8"></script>
	<script type="text/javascript">
	
		$(function(){
			getAllSuccessProcess(1);
		});	
	
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
		
		/* 页面动画 */
		$("#advancedSearchPage").hide();
		
		$(".searchContent").click(function(){
			$("#tableContent").slideUp(1000);
			$("#advancedSearchPage").show(1500);
			$("#pager").hide();
		});
		
		$(".closeAdvanceSearch").click(function(){
			$("#advancedSearchPage").hide();
			$("#tableContent").fadeIn(1000);
			$("#pager").fadeIn(1000);
		});
	
		 /* 切换显示数据类型 */
		$(".pageType a").click(function(){
			$(".pageType a").css("color","black");
			$(this).css("color","red").css("text-decoration","none");
		});
		
		var searchType = "全部";
		
        /* 切换显示数据类型 */
		$(".top_dataType a").click(function(){
			$(".top_dataType a").css("color","#6C6B70");
			$(this).css("color","red").css("text-decoration","none");
		});
        
        /* 
        	进行后端交互 
        */
        function getAllSuccessProcess(page){
        	$.ajaxSettings.async = false;
        	$.post("${getAllSuccessProcess}","resourceType=1&currPage="+page+"&searchType="+searchType,function(data){
        		if(data !=null && data.length > 0){
        			$("#t_tbody").html(data);
            		$.getJSON("${getAllSuccessProcess}","resourceType=2",function(data){
            			var setTotalCount = data["setTotalCount"];
                		var totalPages = data["totalPages"];
                		/* showPage(setTotalCount,totalPages); */   
                		pageFunction(setTotalCount);
            		})
        		} else{
        			var str = "<tr><td style='color:red;'>该用户暂无相关流程信息</td><td></td><td></td><td></td><td></td><td></td></tr>";
        			$("#t_tbody").html(str);
        		}
        	});
        	$.ajaxSettings.async = true;
        }
        
       function getAllSuccessProcess2(page){
        	$.post("${getAllSuccessProcess}","resourceType=1&currPage="+page+"&searchType="+searchType,function(data){
        		$("#t_tbody").html(data);
        	});
        }
        
        
        /* 进行分类(已完成、未完成) */
		$("#allData").click(function(){
        	searchType = $(this).text();
        	getAllSuccessProcess(1);
        });
		$("#startData").click(function(){
			searchType = $(this).text(); 
			getAllSuccessProcess(1);
        });
		$("#endData").click(function(){
			searchType = $(this).text(); 
			getAllSuccessProcess(1);
        });
		
		/* 查看表单详情 */
		$("#t_tbody").on("click","tr td a",function(){
			var taskId = $(this).parent().find("input").val(); 
			window.location.href = "${goToReadRequestProcessAct}&processTaskIdForRequest="+taskId;
		})
		
		</script>
</html>
