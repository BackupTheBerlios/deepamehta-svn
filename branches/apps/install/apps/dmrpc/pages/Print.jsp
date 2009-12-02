<%@ include file="Service.jsp" %>

<%@page contentType="text/html" pageEncoding="iso-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
    String html = (String) session.getAttribute("html");
    String impressumURL = (String) session.getAttribute("impressumURL");
    begin(session, out);
    out.println(html);
    footerArea(impressumURL);
%>