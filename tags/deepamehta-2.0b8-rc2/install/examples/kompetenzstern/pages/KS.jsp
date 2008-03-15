<%@ page import="de.deepamehta.BaseTopic" %>
<%@ page import="de.deepamehta.BaseAssociation" %>
<%@ page import="de.deepamehta.DeepaMehtaException" %>
<%@ page import="de.deepamehta.PropertyDefinition" %>
<%@ page import="de.deepamehta.service.web.HTMLGenerator" %>
<%@ page import="de.deepamehta.service.web.TopicTree" %>
<%@ page import="de.deepamehta.service.web.Action" %>
<%@ page import="de.deepamehta.service.web.DisplayObject" %>
<%@ page import="de.deepamehta.topics.TypeTopic" %>
<%@ page import="de.deepamehta.kompetenzstern.KS" %>

<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Enumeration" %>

<%!
	void begin(HttpSession session, JspWriter out) throws IOException {
		out.println("<HTML>\r<HEAD>\r<TITLE>Kompetenzstern</TITLE>\r</HEAD>\r" +
			"<BODY bgcolor=\"white\">");
	}

	void end(JspWriter out) throws IOException {
		out.println("<P>\r<HR>\r<SMALL>Current time: " + new java.util.Date() +
			"</SMALL>\r</BODY>\r</HTML>");
	}
%>
