<%@ page import="de.deepamehta.BaseTopic" %>
<%@ page import="de.deepamehta.PresentableTopic" %>
<%@ page import="de.deepamehta.service.TopicBean" %>
<%@ page import="de.deepamehta.service.TopicBeanField" %>
<%@ page import="de.deepamehta.service.web.HTMLGenerator" %>

<%@ page import="de.deepamehta.webfrontend.WebFrontend" %>
<%@ page import="de.deepamehta.webfrontend.Topic" %>
<%@ page import="de.deepamehta.webfrontend.RelatedTopic" %>

<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Hashtable" %>

<%!
	void begin(HttpSession session, JspWriter out) throws IOException {
		out.println("<html>\r<head>\r<title>DeepaMehta Web Frontend</title>\r</head>\r<body>\r");
		out.println("<h2>DeepaMehta Web Frontend</h2>");
		out.println("Logged in as \"" + ((BaseTopic) session.getAttribute("user")).getName() + "\".<br>");
		out.println("Go to my <a href=\"controller?action=goHome\">homepage</a>.");
	}

	void end(HttpSession session, JspWriter out) throws IOException {
		out.println("<br><p>\r<hr>\r<small>Generated by DeepaMehta on " + new Date() + "</small>\r</body>\r</html>");
	}

	// ---

	void topicList(Vector topics, HttpSession session, JspWriter out) throws IOException {
		String baseURL = (String) session.getAttribute("baseURL");
		//
		int count = topics.size();
		if (count > 0) {
			out.println("<table>");
			for (int i = 0; i < count; i++) {
				Topic topic = (Topic) topics.elementAt(i);
				out.println("<tr valign=\"top\">" +
					"<td width=\"30\">" +
						"<img src=\"" + baseURL + "icons/" + topic.icon + "\"/>" +
					"</td>" +
					"<td width=\"250\">" +
						"<a href=\"controller?action=showTopicInfo&topicID=" + topic.id + "\">" + topic.name + "</a>" +
					"</td>" +
					"<td width=\"150\">" +
						"<small>" + topic.typeName +
							(topic instanceof RelatedTopic ? " (" + ((RelatedTopic) topic).assocTypeName + ")" : "") +
						"</small>" +
					"</td>" +
				"</tr>");
			}
			out.println("</table>");
		}
	}
%>
