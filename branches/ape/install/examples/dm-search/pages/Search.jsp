<%@ page import="de.deepamehta.BaseTopic" %>
<%@ page import="de.deepamehta.PresentableTopic" %>
<%@ page import="de.deepamehta.service.web.HTMLGenerator" %>
<%@ page import="de.deepamehta.webfront.Search" %>

<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Hashtable" %>

<%!
	void begin(HttpSession session, JspWriter out) throws IOException {
		out.println("<HTML>\r<HEAD>\r<TITLE>DeepaMehta</TITLE>\r</HEAD>\r" +
			"<BODY bgcolor=\"white\">");
	}

	void end(HttpSession session, JspWriter out) throws IOException {
		HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
		out.println("<P>\r<HR>\r<SMALL>" + html.message("Footer", new Date()) + "</SMALL>\r</BODY>\r</HTML>");
	}

	// ###

	void tableOfTopics(Vector topics, JspWriter out) throws IOException {
		int count = topics.size();
		if (count > 0) {
			out.println("<TABLE>");
			for (int i = 0; i < count; i++) {
				PresentableTopic topic = (PresentableTopic) topics.elementAt(i);
				String name = topic.getName();
				Hashtable props = topic.getProperties();
				out.println("<TR><TD WIDTH=\"30\"><IMG SRC=\"icons/" + props.get("iconfile") + "\"></TD>" +
					"<TD WIDTH=\"300\"><A HREF=\"controller?action=" + Search.ACTION_SHOW_TOPIC_INFO + "&topicID=" +
					topic.getID() + "\">" + (name.equals("") ? "<I>unnamed</I>" : name) + "</A></TD>" +
					"<TD><SMALL>" + props.get("typename") + "</SMALL></TD></TR>");
			}
			out.println("</TABLE>");
		}
	}
%>
