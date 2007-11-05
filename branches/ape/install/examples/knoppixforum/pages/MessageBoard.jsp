<HTML>
<HEAD>
	<TITLE><%= session.getAttribute("messageboard") %></TITLE>
</HEAD>
<BODY BGCOLOR="#E8E8C8" LINK="#004080" ALINK="#FF0000" VLINK="#004080">
<H2><%= session.getAttribute("messageboard") %></H2>

<%@ page import="de.deepamehta.BaseTopic" %>
<%@ page import="de.deepamehta.service.web.HTMLGenerator" %>
<%@ page import="de.deepamehta.service.web.TopicTree" %>
<%@ page import="de.deepamehta.messageboard.MessageBoard" %>
<%@ page import="java.util.Vector" %>
<%!
	static String[] hiddenPropsInfo = {MessageBoard.PROPERTY_ICON,
									MessageBoard.PROPERTY_LAST_REPLY_DATE, MessageBoard.PROPERTY_LAST_REPLY_TIME};
	static String[] hiddenPropsForm = {MessageBoard.PROPERTY_ICON, MessageBoard.PROPERTY_DATE,
									MessageBoard.PROPERTY_LAST_REPLY_DATE, MessageBoard.PROPERTY_LAST_REPLY_TIME};

	String link(String action, String params, String text, boolean bold) {
		return (bold ? "<B>" : "") + "<A HREF=\"controller?action=" + action +
			(params != null ? "&" + params : "") + "\">" + text + "</A>" + (bold ? "</B>" : "");
	}
%>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	String mode = (String) session.getAttribute("mode");
	String messageID = (String) session.getAttribute("messageID");
	BaseTopic toplevelMessage = (BaseTopic) session.getAttribute("toplevelMessage");
	TopicTree messageTree = (TopicTree) session.getAttribute("messages");
	Vector extendedNodes = (Vector) session.getAttribute("extendedNodes");
	int pageNr = ((Integer) session.getAttribute("pageNr")).intValue();
	int pageSize = ((Integer) session.getAttribute("pageSize")).intValue();
	//
	out.println("<table>\r<TR VALIGN=\"top\"><TD WIDTH=300>");
	out.println(html.tree(messageTree, null, MessageBoard.ACTION_SHOW_MESSAGE, extendedNodes, "tree=messages", messageID, pageNr, pageSize));
	out.println("</TD>");
	if (messageID != null) {
		out.println("<TD BGCOLOR=\"#F0F0F0\">");
		out.println(html.info(messageID, hiddenPropsInfo, true));
		out.println("</TD>");
	}
	out.println("</TR><TR>");
	if (mode.equals(MessageBoard.MODE_WRITE_TOPLEVEL_MESSAGE)) {
		out.println("<TD COLSPAN=2><BR><H3>Begin a discussion</H3>");
		out.println(html.form(MessageBoard.TOPICTYPE_MESSAGE, MessageBoard.ACTION_CREATE_MESSAGE, hiddenPropsForm, true));
	} else if (mode.equals(MessageBoard.MODE_WRITE_REPLY_MESSAGE)) {
		out.println("<TD COLSPAN=2><BR><H3>Contribute to \"" + toplevelMessage.getName() + "\"</H3>");
		out.println(html.form(MessageBoard.TOPICTYPE_MESSAGE, MessageBoard.ACTION_CREATE_MESSAGE, hiddenPropsForm, true));
	} else {
		out.println("<TD><BR>");
		out.println(link(MessageBoard.ACTION_WRITE_TOPLEVEL_MESSAGE, null, "Begin a discussion", true));
		if (messageID != null) {
			out.println("</TD><TD><BR>");
			out.println(link(MessageBoard.ACTION_WRITE_REPLY_MESSAGE, null, "Contribute to \"" + toplevelMessage.getName() + "\"", true));
		}
	}
	out.println("</TD></TR></table>");
	//
	String webpageName = (String) session.getAttribute("webpageName");
	if (webpageName != null) {
		out.println("<br><br><br><hr>");
		out.println("Go to " + html.staticLink(webpageName, (String) session.getAttribute("webpageURL")));
	}
%>

</BODY>
</HTML>
