<%@ include file="Search.jsp" %>

<% begin(session, out); %>
<%
	HTMLGenerator html = (HTMLGenerator) session.getAttribute("html");
	out.println("<H2>" + html.message("Title") + "</H2>");
	out.println("<H3>" + html.message("SearchForm.header") + "</H3>");
%>
<form>
	<input TYPE="Text" NAME="search">
	<input TYPE="Submit" VALUE="<%= html.message("SearchForm.button") %>">
	<input TYPE="Hidden" NAME="action" VALUE="<%= Search.ACTION_SEARCH %>">
</form>
<% end(session, out); %>
