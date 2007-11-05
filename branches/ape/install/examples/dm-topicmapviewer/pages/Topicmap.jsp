<%@ include file="TopicmapViewer.jsp" %>

<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	String topicmapName = (String) session.getAttribute("topicmapName");
	String topicmapImage = (String) session.getAttribute("topicmapImage");
	Vector geometryData = (Vector) session.getAttribute("geometryData");
%>
<html>
<head>
	<title>DeepaMehta Topicmap Viewer / <%= topicmapName %></title>
	<link href="css/default.css" rel="stylesheet" type="text/css" media="all" />
	<script type="text/javascript">
		var currentTopicID;
		function showTopicInfo(topicID) {
			if (currentTopicID) {
				document.getElementById(currentTopicID).style.display = "none";
			}
			document.getElementById(topicID).style.display = "block";
			currentTopicID = topicID;
		}
		function closeTopicInfo() {
			document.getElementById(currentTopicID).style.display = "none";
		}
	</script>
</head>
<body>
	<h2><%= topicmapName %></h2>
	<img src="<%= topicmapImage %>" usemap="#topicmap">
	<map name="topicmap">
	<%
		Enumeration e = geometryData.elements();
		while (e.hasMoreElements()) {
			TopicGeometry tg = (TopicGeometry) e.nextElement();
			out.println("<area shape=\"rect\" coords=\"" + tg.x1 + ", " + tg.y1 + ", " + tg.x2 + ", " + tg.y2 +
				"\" href=\"javascript:showTopicInfo('" + tg.topicID + "')\" alt=\"alt text\" title=\"Show info of topic " +
				tg.topicID + "\" />");
		}
	%>
	</map>
	<%
		e = geometryData.elements();
		while (e.hasMoreElements()) {
			TopicGeometry tg = (TopicGeometry) e.nextElement();
			out.println("<div class=\"topicinfo\" id=\"" + tg.topicID + "\">");
			out.println("<a href=\"javascript:closeTopicInfo()\"><img class=\"closebox\" src=\"images/close.png\"></a>");
			out.println(html.info(tg.topicID));
			out.println("</div>");
		}
	%>
<% end(session, out); %>
