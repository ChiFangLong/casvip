<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="init.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<portlet:resourceURL var="getProcessPng" />

</head>
<body>
	
	<div id="img"></div>
	
</body>


<script src="${path}/js/jquery-1.9.1.min.js" type="text/javascript"
	charset="utf-8"></script>
<script type="text/javascript">
	$(function(){
		var arry = ${byteList};  // 获取字节流
		var str12 = arrayBufferToBase64(arry); //转换字符串
	    console.log(str12);
	   	var outputImg = document.createElement('img');
	    outputImg.src = 'data:image/png;base64,'+str12;
		$("#img").append(outputImg);
	    console.log(outputImg);
	});
	
	
    function arrayBufferToBase64( buffer ) {
	    var binary = '';
	    var bytes = new Uint8Array( buffer );
	    var len = bytes.byteLength;
	    for (var i = 0; i < len; i++) {
	        binary += String.fromCharCode( bytes[ i ] );
	    }
	    return window.btoa( binary );
	}
</script>
</html>

