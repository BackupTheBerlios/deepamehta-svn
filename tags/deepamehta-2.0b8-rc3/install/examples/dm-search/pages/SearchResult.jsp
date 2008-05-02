<%@ include file="Search.jsp" %>

<% begin(session, out); %>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	Vector topics = (Vector) session.getAttribute("topics");
	String search = request.getParameter("search");
	//
	out.println("<H2>" + html.message("Title") + "</H2>");
	out.println("<H3>" + html.message("SearchResult.header", search) + "</H3>");
	out.println("<SMALL>" + html.message("SearchResult.list", "SearchResult.listno",
		"SearchResult.listone", "SearchResult.listmany", new Integer(topics.size())) + "</SMALL>");
	tableOfTopics(topics, out);
%>
<% end(session, out); %>
