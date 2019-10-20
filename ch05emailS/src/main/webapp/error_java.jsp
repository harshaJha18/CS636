<%@page contentType="text/html" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css"/>
</head>
<body>

<h1>Java Error</h1>
<p>Sorry, Java has thrown an exception.</p>
<p>To continue, click the Back button.</p>

<h2>Details</h2>
<!-- Note that use of pageContext here requires Java's access to jsp-api.jar in tomcat's lib-->
<!-- (no problem for deployed project: it has that access already) -->
<!-- (need to add it as library to your IDE's project to avoid a compiler error) -->
<p>Type: ${pageContext.exception["class"]}</p>
<p>Message: ${pageContext.exception.message}</p>

</body>
</html>