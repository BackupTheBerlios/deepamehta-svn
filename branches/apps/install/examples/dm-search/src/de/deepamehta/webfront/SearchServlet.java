package de.deepamehta.webfront;

import de.deepamehta.BaseTopic;
import de.deepamehta.PresentableTopic;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.service.web.DeepaMehtaServlet;
import de.deepamehta.service.web.HTMLGenerator;
import de.deepamehta.service.web.RequestParameter;
import de.deepamehta.topics.LiveTopic;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;



public class SearchServlet extends DeepaMehtaServlet implements Search {

	protected String performAction(String action, RequestParameter params, Session session, CorporateDirectives directives)
																									throws ServletException {
		if (action == null) {
			return PAGE_SEARCH_FORM;
		} else if (action.equals(ACTION_SEARCH)) {
			String searchStr = params.getValue("search");
			Vector topics = actionSearch(searchStr);
			session.setAttribute("topics", topics);
			return PAGE_SEARCH_RESULT;
		} else if (action.equals(ACTION_SHOW_TOPIC_INFO)) {
			String topicID = params.getValue("topicID");
			BaseTopic topic = cm.getTopic(topicID, 1);		// ### version=1
			session.setAttribute("topic", topic);
			return PAGE_TOPIC_INFO;
		} else {
			return super.performAction(action, params, session, directives);
		}
	}

	protected void preparePage(String page, RequestParameter params, Session session, CorporateDirectives directives) {
	}

	protected void addResources(HTMLGenerator html) {
		html.addResource("SearchBundle");
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @return		the found topics as vector of basis type PresentableTopic. the properties
	 *				of each topic contains the keys "iconfile" and "typename"
	 */
	private Vector actionSearch(String searchStr) {
		Vector result = new Vector();
		//
		Vector topics = cm.getTopicsByName(searchStr);
		// ### Hashtable getTopicsByProperty(String searchString);
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			LiveTopic liveTopic = as.getLiveTopic(topic);
			Hashtable props = new Hashtable();
			props.put("iconfile", liveTopic.getIconfile());
			props.put("typename", as.typeName(topic));
			//
			result.addElement(new PresentableTopic(topic, props));
		}
		//
		return result;
	}
}
