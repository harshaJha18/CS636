<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
	<title>Murach's Java Servlets and JSP: alternative page</title>
	<link rel="stylesheet" href="styles/main.css" type="text/css"/>
</head>
<body>
	<h1>Playing sample using HTML5 &lt;audio&gt; element</h1>
	<p>Playing ${title} </p>
	<audio controls autoplay>
		<source src="${src}" />
	</audio>

	<p><a href="?action=viewAlbums">View list of albums</a></p>

	<p><a href="?action=viewCookies">View all cookies and session variables</a></p>

</body>
</html>