<%@ include file="WebFrontend.jsp" %>

<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	Vector workspaces = (Vector) session.getAttribute("workspaces");
	BaseTopic user = (BaseTopic) session.getAttribute("user");
%>
<html>
<head>
	<title>DeepaMehta Web Frontend</title>
</head>
<body>
	<h2>DeepaMehta Web Frontend</h2>
	Logged in as "<%= user.getName() %>".<br>
	<h3>Your Workspaces</h3>
	<% topicList(workspaces, session, out); %>
	<h3>Search Topics</h3>
	<form>
		<input type="text" name="search"/>
		<input type="submit" value="Search"/>
		<input type="hidden" name="action" value="search"/>
	</form>
<% end(session, out); %>
