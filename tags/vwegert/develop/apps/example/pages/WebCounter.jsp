<html>
  <head>
    <title>Web Counter</title>
  </head>
  <body>
    <h1>Web Counter</h1>
<%@ page import="de.deepamehta.topics.example.CounterTopic" %>
<%
  String counterName = (String) session.getAttribute("counterName");
  int newValue = (int) session.getAttribute("newValue");

  out.println("<p>The current value of the counter <i>" + counterName + 
              "</i> is now " + newValue + ".");
%>    
  </body>
</html>
