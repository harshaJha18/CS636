<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/includes/header.jsp" />
<jsp:include page="/includes/sidebar.jsp" />

<section>

	<form action="initializeDB.html" method="post">
		<input type="submit" value="Initialize DataBase">
	</form>

	<ul>
		<li><a href="toppings"> Manage Toppings</a></li>
		<li><a href="sizes"> Manage Pizza Sizes</a></li>
		<li><a href="orders"> Manage Orders</a></li>
		<li><a href="days"> Manage Days</a></li>
	</ul>
</section>
<jsp:include page="/includes/footer.jsp" />