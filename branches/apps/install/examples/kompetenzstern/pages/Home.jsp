<%@ include file="KS.jsp" %>

<% begin(session, out); %>
<%!
	static String[] propSel = {KS.PROPERTY_DESCRIPTION, KS.PROPERTY_ZUSAMMENFASSUNG};
	//
	static Action editAction        = new Action(KS.ACTION_EDIT_KS, "edit.gif", "Bearbeiten");
	static Action deleteAction      = new Action(KS.ACTION_DELETE_KS, "trash.gif", "Lšschen");
	static Action[] actions = {editAction, deleteAction};
	//
	static Hashtable colWidths = new Hashtable();
	/* static {
		colWidths.put(KS.PROPERTY_NAME, "200");
		colWidths.put(KS.PROPERTY_PROFILE, "150");
	} */
%>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	String ksID = (String) session.getAttribute("ksID");
	Vector ksList = (Vector) session.getAttribute("ksList");

	out.println("<H2>Kompetenzstern</H2>");
	out.println("<TABLE>");
	out.println("<TR><TD>");
	out.println(html.listHeading(ksList.size(), "", "Kompetenzstern", "Kompetenzsterne"));	// ### deprecated
	out.println("<P>");
	out.println(html.list(ksList, ksID, propSel, true, actions, KS.ACTION_SHOW_KS_INFO, null, colWidths));
	out.println("<P><HR>");
	out.println(html.link("Kompetenzstern erzeugen", KS.ACTION_SHOW_KS_FORM));
	out.println("</TD>");
	out.println("</TR>");
	out.println("</TABLE>");
%>
<% end(out); %>
