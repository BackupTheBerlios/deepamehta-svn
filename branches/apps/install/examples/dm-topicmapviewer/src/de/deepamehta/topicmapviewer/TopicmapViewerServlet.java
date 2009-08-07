package de.deepamehta.topicmapviewer;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.service.web.DeepaMehtaServlet;
import de.deepamehta.service.web.RequestParameter;
import de.deepamehta.topics.TopicMapTopic;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;



public class TopicmapViewerServlet extends DeepaMehtaServlet implements TopicmapViewer {

	protected String performAction(String action, RequestParameter params, Session session, CorporateDirectives directives)
																									throws ServletException {
		if (action == null) {
			return PAGE_TOPICMAP_LIST;
		} else if (action.equals(ACTION_SHOW_TOPICMAP)) {
			String topicmapID = params.getValue("topicmapID");
			//
			TopicMapTopic topicmap = (TopicMapTopic) as.getLiveTopic(topicmapID, 1);
			String topicmapImage = FILESERVER_DOCUMENTS_PATH + "topicmap-" + topicmapID + ".png";
			Vector geometryData = topicmap.exportToPNG();
			//
			session.setAttribute("topicmapName", topicmap.getName());
			session.setAttribute("topicmapImage", as.getCorporateWebBaseURL() + topicmapImage);
			session.setAttribute("geometryData", geometryData);
			return PAGE_TOPICMAP;
		} else {
			return super.performAction(action, params, session, directives);
		}
	}

	protected void preparePage(String page, RequestParameter params, Session session, CorporateDirectives directives) {
		if (page.equals(PAGE_TOPICMAP_LIST)) {
			Vector workspaces = cm.getTopics(TOPICTYPE_WORKSPACE);
			Hashtable topicmaps = getTopicmaps(workspaces);
			session.setAttribute("workspaces", workspaces);
			session.setAttribute("topicmaps", topicmaps);
		}
	}



	// *****************
	// *** Utilities ***
	// *****************



	private Hashtable getTopicmaps(Vector workspaces) {
		Hashtable topicmaps = new Hashtable();
		//
		Enumeration e = workspaces.elements();
		while (e.hasMoreElements()) {
			BaseTopic workspace = (BaseTopic) e.nextElement();
			BaseTopic topicmap = as.getWorkspaceTopicmap(workspace.getID());
			if (topicmap == null) {
				System.out.println("*** " + workspace + " has no workspace topicmap");
				topicmaps.put(workspace.getID(), new Vector());
				continue;
			}
			Vector maps = cm.getTopics(TOPICTYPE_TOPICMAP, new Hashtable(), topicmap.getID());
			topicmaps.put(workspace.getID(), maps);
		}
		//
		return topicmaps;
	}
}
