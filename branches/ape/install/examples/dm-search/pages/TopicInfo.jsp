<%@ include file="Search.jsp" %>

<% begin(session, out); %>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	BaseTopic topic = (BaseTopic) session.getAttribute("topic");
	//
	out.println("<H2>" + html.message("Title") + "</H2>");
	out.println("<H3>" + html.message("TopicInfo.header", topic.getName()) + "</H3>");
	out.println(html.info(topic.getID()));
%>
<% end(session, out); %>
