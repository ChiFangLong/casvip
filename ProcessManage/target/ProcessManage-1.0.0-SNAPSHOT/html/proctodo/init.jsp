<%@page import="javax.portlet.RenderRequest"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<c:set var="path" value="<%=request.getContextPath()%>" />


<link rel="stylesheet" type="text/css"
	href="${path}/css/MyCss/index.css" />
<link href="${path}/bootstrap/css/bootstrap.min.css" rel="stylesheet"
	media="screen">
<script src="${path}/js/jquery-1.9.1.min.js"></script>
<script src="${path}/bootstrap/js/bootstrap.min.js"></script>

<portlet:defineObjects />
