<html>
<head>
	<title><%= session.getAttribute("messageboard") %></title>
	<link href="pages/messageboard.css" rel="stylesheet" type="text/css">
</head>
<body>
<h2><%= session.getAttribute("messageboard") %></h2>

<%@ page import="de.deepamehta.BaseTopic" %>
<%@ page import="de.deepamehta.service.web.HTMLGenerator" %>
<%@ page import="de.deepamehta.service.web.TopicTree" %>
<%@ page import="de.deepamehta.messageboard.MessageBoard" %>
<%@ page import="de.deepamehta.messageboard.Message" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Enumeration" %>
<%!
	String message(Message message) {
		return "<h4>" + message.subject + "</h4>\r" +
			message.text + "\r" +
			"<p>From: " + message.from + "<br>\r" + 
			"Date: " + message.date + "\r" +
			"</p>";
	}

	String button(String label, String action) {
		return "<form>\r" +
			"\t<input type=\"hidden\" name=\"action\" value=\"" + action + "\">\r" + 
			"\t<input type=\"submit\" value=\"" + label + "\">\r" +
			"</form>";
	}

	String form() {
		return "<form>\r" +
			"\t<small>Subject</small><br>\r" +
			"\t<input type=\"Text\" name=\"Name\" value=\"\" size=\"54\"><br>\r" +
			"\t<small>Message</small><br>\r" +
			"\t<div class=\"supplement\">Interpreted tags:" + allowedTags() + "<br>\r" +
			"\tOther tags will appear verbatim in the message</div>\r" +
			"\t<textarea name=\"Description\" rows=\"12\" cols=\"54\"></textarea><br>\r" +
			"\t<small>From</small><br>\r" +
			"\t<input type=\"Text\" name=\"From\" value=\"\" size=\"54\"><br>\r" +
			"\t<input type=\"Hidden\" name=\"action\" value=\"createMessage\">\r" +
			"\t<input type=\"Submit\" value=\"OK\" name=\"button\">\r" +
			"\t<input type=\"Submit\" value=\"Cancel\" name=\"button\">\r" +
			"</form>";
	}

	String allowedTags() {
		StringBuffer allowedTags = new StringBuffer();
		Enumeration e = MessageBoard.ALLOWED_TAGS.elements();
		while (e.hasMoreElements()) {
			allowedTags.append(" &lt;" + e.nextElement() + ">");
		}
		return allowedTags.toString();
	}
%>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	String mode = (String) session.getAttribute("mode");
	String messageID = (String) session.getAttribute("messageID");
	Message message = (Message) session.getAttribute("message");
	BaseTopic toplevelMessage = (BaseTopic) session.getAttribute("toplevelMessage");
	TopicTree messageTree = (TopicTree) session.getAttribute("messages");
	Vector extendedNodes = (Vector) session.getAttribute("extendedNodes");
	int messageCount = ((Integer) session.getAttribute("messageCount")).intValue();
	int pageNr = ((Integer) session.getAttribute("pageNr")).intValue();
	int pageSize = ((Integer) session.getAttribute("pageSize")).intValue();
	//
	out.println("<table>\r<tr valign=\"top\"><td width=\"400\">");
	out.println("<p class=\"supplement\">The forum contains " + messageCount + " topics</p>");
	out.println(html.tree(messageTree, null, MessageBoard.ACTION_SHOW_MESSAGE, extendedNodes, "tree=messages", messageID, pageNr, pageSize));
	out.println("</td>");
	if (messageID != null) {
		out.println("<td class=\"message\">");
		out.println(message(message));
		out.println("</td>");
	}
	out.println("</tr><tr>");
	if (mode.equals(MessageBoard.MODE_WRITE_TOPLEVEL_MESSAGE)) {
		out.println("<td colspan=2><br><h3>" + MessageBoard.STRING_CREATE_TOPIC + "</h3>");
		out.println(form());
	} else if (mode.equals(MessageBoard.MODE_WRITE_REPLY_MESSAGE)) {
		out.println("<td colspan=2><br><h3>" + MessageBoard.STRING_REPLY_TO_TOPIC + "\"" + toplevelMessage.getName() + "\"</h3>");
		out.println(form());
	} else {
		out.println("<td><br>");
		out.println(button(MessageBoard.STRING_CREATE_TOPIC, MessageBoard.ACTION_WRITE_TOPLEVEL_MESSAGE));
		if (messageID != null) {
			out.println("</td><td><br>");
			out.println(button(MessageBoard.STRING_REPLY_TO_TOPIC + "&quot;" + toplevelMessage.getName() + "&quot;",
				MessageBoard.ACTION_WRITE_REPLY_MESSAGE));
		}
	}
	out.println("</td></tr></table>");
	//
	String webpageName = (String) session.getAttribute("webpageName");
	if (webpageName != null) {
		out.println("<br><br><hr>");
		out.println("Go to " + html.staticLink(webpageName, (String) session.getAttribute("webpageURL")));
	}
%>

</body>
</html>
