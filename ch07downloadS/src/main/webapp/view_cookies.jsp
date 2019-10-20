<%@page contentType="text/html" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css"/>
</head>
<body>

<h1>Cookies</h1>

<p>Here's a table with all of the cookies that this 
browser is sending to the current server.</p>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<table>
		<tr>
			<th>Name</th>
			<th>Value</th>
		</tr>
		<c:forEach var="ck" items="${cookie}">
			<tr>
				<td>${ck.value.name}</td>
				<td>${ck.value.value}</td>
			</tr>
		</c:forEach>
	</table>

	<h1>Session variables in this session</h1>
	<p>Here's a table with all of the session variables that the 
server is maintaining for this user session.</p>
	
	<table>
		<tr>
			<th>Name</th>
			<th>Value</th>
		</tr>
		<!-- sessionScope is an implicit object of JSP, a map of session vars -->
		<c:forEach var="v" items="${sessionScope}">
			<!-- v is a map entry, with key (var name) and value (var object) -->
			<tr>
				<td>${v.key}</td>
				<td>${v.value}</td>
			</tr>
		</c:forEach>
	</table>
	<p><a href="download?action=viewAlbums">View list of albums</a></p>

<p><a href="download?action=deleteCookies">Delete all persistent cookies</a></p>
<p><a href="?action=logout">Logout (terminate session)</a></p>

</body>
</html>