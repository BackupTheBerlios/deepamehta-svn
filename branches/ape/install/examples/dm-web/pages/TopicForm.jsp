<%@ include file="WebFrontend.jsp" %>

<% begin(session, out); %>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	String typeID = (String) session.getAttribute("typeID");
	String topicID = (String) session.getAttribute("topicID");
%>
<%
	if (topicID != null) {
		out.println("<h3>Edit Topic</h3>");
		out.println(html.form(typeID, WebFrontend.ACTION_UPDATE_TOPIC, topicID));
	} else {
		out.println("<h3>Create Topic</h3>");
		out.println(html.form(typeID, WebFrontend.ACTION_CREATE_TOPIC));
	}
%>
<% end(session, out); %>
