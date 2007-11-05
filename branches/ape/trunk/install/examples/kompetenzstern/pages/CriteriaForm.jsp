<%@ include file="KS.jsp" %>

<% begin(session, out); %>
<%!
	static String[] propSel = {KS.PROPERTY_NAME, KS.PROPERTY_HELP};
%>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	BaseTopic ks = (BaseTopic) session.getAttribute("ks");
	String critTypeID = (String) session.getAttribute("critTypeID");
	BaseTopic crit = (BaseTopic) session.getAttribute("crit");
	Vector subCrits = (Vector) session.getAttribute("subCrits");
	//
	out.println("<H2>Kompetenzstern \"" + ks.getName() + "\"</H2>");
	out.println("<H3>Kriterium \"" + crit.getName() + "\" bearbeiten</H3>");
	out.println("<TABLE CELLPADDING=15><TR VALIGN=\"TOP\"><TD>");
	out.println(html.form(critTypeID, KS.ACTION_UPDATE_CRITERIA, crit.getID(), propSel, true));
	out.println("</TD><TD" + (subCrits.size() > 0 ? " BGCOLOR=\"#F0F0F0\"" : "") + ">");
	out.println(html.linkList(subCrits, KS.ACTION_EDIT_CRITERIA));
	out.println("</TD></TR></TABLE>");
	out.println("<P><HR>");
	out.println(html.link("Zur&uuml;ck", KS.ACTION_EDIT_KS));
%>
<% end(out); %>
