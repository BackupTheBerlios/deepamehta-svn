package de.deepamehta.webfrontend;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.service.TopicBean;
import de.deepamehta.service.web.DeepaMehtaServlet;
import de.deepamehta.service.web.RequestParameter;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletException;



public class WebFrontendServlet extends DeepaMehtaServlet implements WebFrontend {

	protected String performAction(String action, RequestParameter params, Session session, CorporateDirectives directives)
																									throws ServletException {
		if (action == null) {
			return PAGE_LOGIN;
		} else if (action.equals(ACTION_TRY_LOGIN)) {
			return actionTryLogin(params, session);
		} else if (action.equals(ACTION_GO_HOME)) {
			return PAGE_HOME;
		} else if (action.equals(ACTION_SEARCH)) {
			String search = params.getValue("search");
			setMode(MODE_BY_NAME, session);
			setSearchText(search, session);
			return PAGE_TOPIC_LIST;
		} else if (action.equals(ACTION_SHOW_TOPIC_FORM)) {
			String typeID = params.getValue("typeID");
			setTypeID(typeID, session);
			// if parameter "topicID" is set the specified topic is to be edited
			String topicID = params.getValue("topicID");	// may be null
			session.setAttribute("topicID", topicID);
			return PAGE_TOPIC_FORM;
		} else if (action.equals(ACTION_SHOW_TOPIC_INFO)) {
			String topicID = params.getValue("topicID");
			Vector relatedTopics = cm.getRelatedTopics(topicID);
			session.setAttribute("topicBean", as.createTopicBean(topicID, 1));
			session.setAttribute("relTopics", createRelatedTopicBeans(relatedTopics, topicID));
			return PAGE_TOPIC_INFO;
		} else if (action.equals(ACTION_SHOW_TOPICS)) {
			String typeID = params.getValue("typeID");
			setMode(MODE_BY_TYPE, session);
			setTypeID(typeID, session);
			return PAGE_TOPIC_LIST;
		} else if (action.equals(ACTION_CREATE_TOPIC)) {
			String typeID = getTypeID(session);
			createTopic(typeID, params, session, directives);
			return PAGE_TOPIC_LIST;
		} else if (action.equals(ACTION_UPDATE_TOPIC)) {
			String typeID = getTypeID(session);
			updateTopic(typeID, params, session, directives);
			return PAGE_TOPIC_LIST;
		} else if (action.equals(ACTION_DELETE_TOPIC)) {
			String topicID = params.getValue("topicID");
			deleteTopic(topicID);
			return PAGE_TOPIC_LIST;
		} else {
			return super.performAction(action, params, session, directives);
		}
	}

	protected void preparePage(String page, RequestParameter params, Session session, CorporateDirectives directives) {
		if (page.equals(PAGE_HOME)) {
			session.setAttribute("baseURL", as.getCorporateWebBaseURL());
			//
			Vector workspaces = as.getWorkspaces(getUserID(session));
			session.setAttribute("workspaces", createTopicBeans(workspaces));
		} else if (page.equals(PAGE_TOPIC_LIST)) {
			String mode = getMode(session);
			if (mode == MODE_BY_NAME) {
				Vector topics = cm.getTopicsByName(getSearchText(session));
				session.setAttribute("topics", createTopicBeans(topics));
			} else if (mode == MODE_BY_TYPE) {
				Vector topics = cm.getTopics(getTypeID(session));
				session.setAttribute("topics", createTopicBeans(topics));
			} else {
				throw new DeepaMehtaException("unexpected mode: \"" + mode + "\"");
			}
		}
	}



	// *****************
	// *** Utilities ***
	// *****************



	private String actionTryLogin(RequestParameter params, Session session) {
		String username = params.getValue(PROPERTY_USERNAME);
		String password = params.getValue(PROPERTY_PASSWORD);
		if (as.loginCheck(username, password)) {
			BaseTopic user = cm.getTopic(TOPICTYPE_USER, username, 1);	// ### version=1
			setUser(user, session);
			return PAGE_HOME;
		}
		return PAGE_LOGIN;
	}

	// ---

	private Vector createTopicBeans(Vector topics) {
		Vector topicBeans = new Vector();
		//
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			topicBeans.addElement(new Topic(topic, as));
		}
		//
		return topicBeans;
	}

	private Vector createRelatedTopicBeans(Vector topics, String sourceTopicID) {
		Vector relatedTopicBeans = new Vector();
		//
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			Vector assocs = as.cm.getAssociations(sourceTopicID, topic.getID(), true);	// ignoreDirection=true
			if (assocs.size() > 1) {
				// ### not yet handled
			}
			BaseAssociation assoc = (BaseAssociation) assocs.firstElement();
			relatedTopicBeans.addElement(new RelatedTopic(topic, assoc, as));
		}
		//
		return relatedTopicBeans;
	}



	// *************************
	// *** Session Utilities ***
	// *************************



	// --- Methods to store data in the session
	// --- Note: data should be stored into the session only by using this methods
	// --- All the session attributes are explained in docs/states.txt

	private void setUser(BaseTopic user, Session session) {
		session.setAttribute("user", user);
		System.out.println("> \"user\" stored in session: " + user);
	}

	private void setMode(String mode, Session session) {
		session.setAttribute("mode", mode);
		System.out.println("> \"mode\" stored in session: \"" + mode + "\"");
	}

	private void setSearchText(String search, Session session) {
		session.setAttribute("search", search);
		System.out.println("> \"search\" stored in session: \"" + search + "\"");
	}

	private void setTypeID(String typeID, Session session) {
		session.setAttribute("typeID", typeID);
		System.out.println("> \"typeID\" stored in session: \"" + typeID + "\"");
	}

	// ---

	private String getMode(Session session) {
		return (String) session.getAttribute("mode");
	}

	private String getSearchText(Session session) {
		return (String) session.getAttribute("search");
	}

	private String getTypeID(Session session) {
		return (String) session.getAttribute("typeID");
	}
}
