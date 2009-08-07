<%@ include file="WebFrontend.jsp" %>

<% begin(session, out); %>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	Vector topics = (Vector) session.getAttribute("topics");
	String mode = (String) session.getAttribute("mode");
	String search = (String) session.getAttribute("search");
	String typeID = (String) session.getAttribute("typeID");
%>
<%
	if (mode.equals(WebFrontend.MODE_BY_NAME)) {
		out.println("<h3>" + topics.size() + " \"" + search + "\" topics</h3>");
	} else if (mode.equals(WebFrontend.MODE_BY_TYPE)) {
		out.println("<h3>" + topics.size() + " topics of type " + typeID + "</h3>");
	}
	topicList(topics, session, out);
%>
<% end(session, out); %>
