<%@ include file="TopicmapViewer.jsp" %>

<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	Vector workspaces = (Vector) session.getAttribute("workspaces");
	Hashtable topicmaps = (Hashtable) session.getAttribute("topicmaps");
%>
<html>
<head>
	<title>DeepaMehta Topicmap Viewer</title>
	<link href="css/default.css" rel="stylesheet" type="text/css" media="all" />
</head>
<body>
	<h2>DeepaMehta Topicmap Viewer</h2>
	<dl>
	<%
		Enumeration e = workspaces.elements();
		while (e.hasMoreElements()) {
			BaseTopic workspace = (BaseTopic) e.nextElement();
			out.println("<dt>" + workspace.getName() + "</dt>");
			Enumeration e2 = ((Vector) topicmaps.get(workspace.getID())).elements();
			while (e2.hasMoreElements()) {
				BaseTopic topicmap = (BaseTopic) e2.nextElement();
				out.println("<dd><a href=\"?action=" + TopicmapViewer.ACTION_SHOW_TOPICMAP +
					"&topicmapID=" + topicmap.getID() + "\">" + topicmap.getName() + "</a></dd>");
			}
		}
	%>
	</dl>

<% end(session, out); %>
