<%@ page import="de.deepamehta.DeepaMehtaConstants" %>
<%@ page import="de.deepamehta.service.TopicBean" %>

<%@ page import="de.deepamehta.BaseTopic" %>
<%@ page import="de.deepamehta.service.TopicBean" %>
<%@ page import="de.deepamehta.service.TopicBeanField" %>
<%@ page import="de.deepamehta.BaseAssociation" %>
<%@ page import="de.deepamehta.DeepaMehtaException" %>
<%@ page import="de.deepamehta.PresentableTopic" %>
<%@ page import="de.deepamehta.PropertyDefinition" %>
<%@ page import="de.deepamehta.service.Session" %>
<%@ page import="de.deepamehta.topics.TypeTopic" %>
<%@ page import="de.deepamehta.service.web.HTMLGenerator" %>

<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.awt.Point" %>

<%!
	// --- header area ---

	// browse
	void begin(HttpSession session, JspWriter out) throws IOException {
		String stylesheet = (String) session.getAttribute("stylesheet");
		String siteLogo = (String) session.getAttribute("siteLogo");
		String homepageURL = (String) session.getAttribute("homepageURL");
		String impressumURL = (String) session.getAttribute("impressumURL");
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\r" +
			"<html>" +
			"\r<head>\n" +
			"<meta http-equiv=\"content-type\" content=\"text/html; charset=iso-8859-1\">" +
			"\r<title>Homepage of a DeepaMehta JSON RPC Service</title>" +
			"\r<style type=\"text/css\">\r" + stylesheet + "\r</style>\r" +
			"</head>\r" +
			"<body>\r\r");
		//
		out.println("<div class=\"header-area\">");			// --- begin header area
		out.println("<table cellpadding=\"0\" width=\"100%\"><tr valign=\"top\">");
		out.println("<td>");
		out.println("<a href=\"" + homepageURL + "\" target=\"_blank\"><img src=\"" + siteLogo + "\" border=\"0\"></a>");
		out.println("</td>");
		//
		out.println("</table>");
		out.println("</div>");								// --- end header area
		out.println();
		out.println("<div class=\"content-area\">");		// --- begin content area
	}

	// --- footer area ---

	// browse
	void end(HttpSession session, JspWriter out) throws IOException {
		String impressumURL = (String) session.getAttribute("impressumURL");
		out.println(footerArea(impressumURL));
	}

	private String footerArea(String impressumURL) {
		return "</div>\r" +									// --- end content area
			"\r" +
			"<div class=\"footer-area\">\r" +				// --- begin footer area
			"<table width=\"100%\"><tr>\r" +
			"<td class=\"secondary-text\">Powered by<br><a href=\"http://www.deepamehta.de/\" target=\"_blank\"><b>DeepaMehta</b></a></td>\r" +
			"<td class=\"secondary-text\" align=\"right\"><a href=\"" + impressumURL + "\" target=\"_blank\">Impressum +<br>Haftungshinweise</a></td>\r" +
			"</tr></table>\r" +
			"</div>\r\r" +									// --- begin footer area
			"</body>\r</html>\r";
	}

	// ---

	void topicImages(Vector cats, HTMLGenerator html, JspWriter out) throws IOException {
		for (int i = 0; i < cats.size(); i++) {
			BaseTopic cat = (BaseTopic) cats.elementAt(i);
			out.println(html.imageTag(cat, true));		// withTooltip=true
		}
	}

	void topicList(Vector topics, String action, HTMLGenerator html, JspWriter out) throws IOException {
		out.println("<table>");
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			out.println("<tr><td>" + html.imageTag(topic) + "</td><td><a href=\"controller?action=" + action +
				"&id=" + topic.getID() + "\">" + topic.getName() + "</td></tr>");
		}
		out.println("</table>");
	}

    // ---

    //void comment(Comment comment, JspWriter out) throws IOException {
    //    comment(comment, false, out);
    //}

    //void comment(Comment comment, boolean includeEmailAddress, JspWriter out) throws IOException {
    //    String email = comment.email;
      //  out.println("<br>");
        ////
        //out.println("<span class=\"small\">");
        //if (isSet(comment.author)) {
            //out.println(comment.author + commentEmail(includeEmailAddress, email) + " schrieb am ");
        //} else {
//            out.println("Anonymer Kommentar" + commentEmail(includeEmailAddress, email) + " vom ");
  //      }
//        out.println(comment.date + ":</span><br>");
        //
 //       out.println(comment.text + "<br>");
  //  }

	// ---

	private String commentEmail(boolean includeEmailAddress, String email) {
		if (includeEmailAddress) {
			return isSet(email) ? " (<a href=\"mailto:" + email + "\">" + email + "</a>)" : " (Emailadresse unbekannt)";
		} else {
			return "";
		}
	}

	// ---

	String mapLink(String street, String postalCode, String city) throws IOException {
		// ### System.out.println(">>> mapLink(): street=\"" + street + "\" postalCode=\"" + postalCode + "\" city=\"" + city + "\"");
		StringBuffer html = new StringBuffer();
		// render fahr-info link if address is in berlin
		if (city.startsWith("Berlin") && isSet(street)) {
			String mapURL = "http://www.fahrinfo-berlin.de/Stadtplan/index?query=" + street + "&search=Suchen&formquery=&address=true";
			String imageLink = " <a href=\"" + mapURL + "\" target=\"_blank\"><img src=\"../images/fahrinfo.gif\" border=\"0\" " +
				"hspace=\"20\"></a>";
			html.append(street + imageLink + "<br>" + postalCode + " " + city + googleLink(street, postalCode, city));
			return html.toString();
		} else {
			html.append(isSet(street) ? street + "<br>" : "");
			html.append(isSet(postalCode) ? postalCode + " " : "");
			html.append(isSet(city) ? city + googleLink(street, postalCode, city) : "");
			return html.toString();
		}
	}
	
	String googleLink(String street, String postalCode, String city) throws IOException {
		// ### System.out.println(">>> googleLink(): street=\"" + street + "\" postalCode=\"" + postalCode + "\" city=\"" + city + "\"");
		StringBuffer html = new StringBuffer();
		// render googlelink if address is complete
		if (isSet(city) && isSet(street) && isSet(postalCode)) {
			String mapURL = "http://maps.google.de/maps?q=" + street + ", " + postalCode + " " + city + "&mrt=loc&lci=lmc:panoramio,lmc:wikipedia_en&layer=tc&t=h";
			String imageLink = " <a href=\"" + mapURL + "\" target=\"_blank\"><img src=\"../images/google_logo_small.png\" alt=\"Ansicht in Google \" hspace=\"20\" border=\"0\"></a>";
			html.append(imageLink);
			return html.toString();
		} else {
		    return "";
		}
	}
	
	String mailtoUrl(Vector mailboxes) {
		Enumeration e = mailboxes.elements();
		StringBuffer url = new StringBuffer();
		url.append("mailto:");
		//
		while(e.hasMoreElements()) {
			String mail = (String) e.nextElement();
			if(mail != null && !mail.equals("")) {
			    url.append(mail);
			}
			if (e.hasMoreElements()) {
			    url.append(",");
			}
		}
		//
		return url.toString();
	}
	
	// --
	
	String fieldOptions(TopicBean bean, String[] hiddenProps, String[] hiddenPropsContaining, String checked) {
		StringBuffer html = new StringBuffer();
		for (int j = 0; j < hiddenProps.length; j++) {
		    bean.removeField(hiddenProps[j].toString());
		}
		for (int k = 0; k < hiddenPropsContaining.length; k++) {
		    bean.removeFieldsContaining(hiddenPropsContaining[k].toString());
		}
		for (int i = 0; i < bean.fields.size(); i++) {
		    TopicBeanField field = (TopicBeanField) bean.fields.get(i);
		    html.append("<option value=\""+ field.name +"\"");
		    if (field.name.equals(checked)) {
			html.append(" selected=\"selected\"");
		    }
		    html.append(">" + field.label + "</option> \n ");
		}
		return html.toString();
	}
	
	// ---

	boolean isSet(String str) {
		return str != null && str.length() > 0;
	}
%>
