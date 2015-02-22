<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.*, java.lang.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<link rel="stylesheet" type="text/css"
	href="./bootstrap-dist/css/bootstrap.min.css">
<title>Home Page</title>
<style>
body {
	background-color: #ffffff;
	overflow-x: hidden;
	overflow-y: auto;
	text-align: center;
	padding-top: 96px;
}

.logoimage {
	width: 200px;
	height: 200px;
}

.form-heading {
	font-size: 36px;
	margin-bottom: 12px;
	color: #333333;
}
</style>
</head>
<body>
	<div class="row">
		<div class="col-md-*">
			<img src="./media/inverted_launcher.png" class="logoimage">
		</div>
	</div>
	<div class="row">
		<div class="col-md-*">
			<% String error_msg = (String) request.getAttribute("error_msg"); %>
			<% if(error_msg==null) error_msg = "Oops! Something went wrong..."; %>
			<p class="form-heading"><% out.print(error_msg); %></p>
		</div>
	</div>
	<div class="row">
		<div class="col-md-*">
			<a class="btn btn-danger btn-lg"
				href="/ClickerServer/index.html">Go to Login</a>
		</div>
	</div>
</body>
</html>