<%@ include file="KS.jsp" %>

<% begin(session, out); %>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	BaseTopic ks = (BaseTopic) session.getAttribute("ks");
	Vector crits = (Vector) session.getAttribute("crits");
	String ksID = (String) session.getAttribute("ksID");
	//
	out.println("<H2>Kompetenzstern</H2>");
	if (ksID != null) {
		out.println("<H3>Kompetenzstern \"" + ks.getName() + "\" bearbeiten</H3>");
		out.println("<TABLE CELLPADDING=15><TR VALIGN=\"TOP\"><TD>");
		out.println(html.form(KS.TOPICTYPE_KOMPETENZSTERN, KS.ACTION_UPDATE_KS, ksID));
		out.println("</TD><TD BGCOLOR=\"#F0F0F0\">");
		out.println(html.linkList(crits, KS.ACTION_EDIT_CRITERIA));
		out.println("</TD></TR></TABLE>");
	} else {
		out.println("<H3>Kompetenzstern erzeugen</H3>");
		out.println(html.form(KS.TOPICTYPE_KOMPETENZSTERN, KS.ACTION_CREATE_KS));
	}
	out.println("<P><HR>");
	out.println(html.link("Zur&uuml;ck zur &Uuml;bersicht", KS.ACTION_GO_HOME));
%>
<% end(out); %>
