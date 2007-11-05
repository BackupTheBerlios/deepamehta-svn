<%@ include file="WebFrontend.jsp" %>

<% begin(session, out); %>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	String baseURL = (String) session.getAttribute("baseURL");
	TopicBean topicBean = (TopicBean) session.getAttribute("topicBean");
	Vector relTopics = (Vector) session.getAttribute("relTopics");
%>
<%
	out.println("<h3>" + topicBean.name + " <img src=\"" + baseURL + "icons/" + topicBean.icon + "\"/> " + 
		"(<small>" + topicBean.typeName + "</small>)</h3>");
	out.println(html.info(topicBean));
%>
	<br>
	<table border="0">
		<tr bgcolor="#e8e8e8">
			<td width="50" height="50">
				<a href="controller?action=showTopicForm&typeID=<%= topicBean.typeID %>&topicID=<%= topicBean.id %>">
					<img src="images/edit.gif" border="0"/>
				</a>
			</td>
			<td width="50" height="50">
				<a href="controller?action=deleteTopic&topicID=<%= topicBean.id %>">
					<img src="images/trash.gif" border="0"/>
				</a>
			</td>
			<% if (topicBean.typeID.equals(WebFrontend.TOPICTYPE_TOPICTYPE)) { %>
				<td width="50">
					<a href="controller?action=showTopics&typeID=<%= topicBean.id %>">
						<img src="images/eye.gif" border="0"/>
					</a>
				</td>
				<td width="50">
					<a href="controller?action=showTopicForm&typeID=<%= topicBean.id %>">
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
