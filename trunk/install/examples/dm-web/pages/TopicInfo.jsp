<%@ include file="WebFrontend.jsp" %>

<% begin(session, out); %>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	String baseURL = (String) session.getAttribute("baseURL");
	Topic topic = (Topic) session.getAttribute("topic");
	Vector relTopics = (Vector) session.getAttribute("relTopics");
%>
<%
	out.println("<h3>" + topic.name + " <img src=\"" + baseURL + "icons/" + topic.icon + "\"/> " + 
		"(<small>" + topic.typeName + "</small>)</h3>");
	out.println(html.info(topic.id));
%>
	<br>
	<table border="0">
		<tr bgcolor="#e8e8e8">
			<td width="50" height="50">
				<a href="controller?action=showTopicForm&typeID=<%= topic.typeID %>&topicID=<%= topic.id %>">
					<img src="images/edit.gif" border="0"/>
				</a>
			</td>
			<td width="50" height="50">
				<a href="controller?action=deleteTopic&topicID=<%= topic.id %>">
					<img src="images/trash.gif" border="0"/>
				</a>
			</td>
			<% if (topic.typeID.equals(WebFrontend.TOPICTYPE_TOPICTYPE)) { %>
				<td width="50">
					<a href="controller?action=showTopics&typeID=<%= topic.id %>">
						<img src="images/eye.gif" border="0"/>
					</a>
				</td>
				<td width="50">
					<a href="controller?action=showTopicForm&typeID=<%= topic.id %>">
						<img src="images/create.gif" border="0"/>
					</a>
				</td>
			<% } %>
		</tr>
	</table>
<%
	out.println("<h3>Related Topics</h3>");
	topicList(relTopics, session, out);
%>
<% end(session, out); %>
