package de.deepamehta.kompetenzstern;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.web.RequestParameter;
import de.deepamehta.service.web.DeepaMehtaServlet;
import de.deepamehta.kompetenzstern.topics.KompetenzsternTopic;
import de.deepamehta.kompetenzstern.topics.KriteriumTopic;
//
import javax.servlet.ServletException;
//
import java.io.IOException;
import java.util.*;



/**
 * <P>
 * <HR>
 * Last sourcecode change: 29.10.2004 (2.0b3)<BR>
 * Last documentation update: 15.1.2003 (2.0a17-pre6)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class KSServlet extends DeepaMehtaServlet implements KS {

	protected String performAction(String action, RequestParameter params, Session session) throws ServletException {
		if (action == null) {
			return PAGE_HOME;
		} else if (action.equals(ACTION_GO_HOME)) {
			return PAGE_HOME;
		// --- Kompetenzstern ---
		} else if (action.equals(ACTION_SHOW_KS_FORM)) {
			return PAGE_KS_FORM;
		} else if (action.equals(ACTION_CREATE_KS)) {
			String ksID = createTopic(TOPICTYPE_KOMPETENZSTERN, params, session);
			setKS(ksID, session);
			return PAGE_HOME;
		} else if (action.equals(ACTION_EDIT_KS)) {
			String ksID = params.getValue("id");
			setKS(ksID, session);
			return PAGE_KS_FORM;
		} else if (action.equals(ACTION_UPDATE_KS)) {
			updateTopic(TOPICTYPE_KOMPETENZSTERN, params, session);
			return PAGE_HOME;
		} else if (action.equals(ACTION_DELETE_KS)) {
			String ksID = params.getValue("id");
			deleteTopic(ksID);
			return PAGE_HOME;
		// --- Kriterium ---
		} else if (action.equals(ACTION_EDIT_CRITERIA)) {
			String critID = params.getValue("id");
			setCriteria(critID, session);
			return PAGE_CRITERIA_FORM;
		} else if (action.equals(ACTION_UPDATE_CRITERIA)) {
			String ksID = getKSID(session);
			updateTopic(getKSCritTypeID(session), params, session, ksID, VIEWMODE_USE);
			return PAGE_KS_FORM;
		} else {
			return super.performAction(action, params, session);
		}
	}

	protected void preparePage(String page, RequestParameter params, Session session) {
		if (page.equals(PAGE_HOME)) {
	   		// get all kompetenzsterne
			Vector ksList = cm.getTopics(TOPICTYPE_KOMPETENZSTERN);
			session.setAttribute("ksList", ksList);
			session.setAttribute("ksID", null);
		}
	}



	// *************************
	// *** Session Utilities ***
	// *************************



	// --- Methods to store data in the session
	// --- Note: data should be stored into the session only by using this methods
	// --- All the session attributes are explained in SCIDOC/KS/docs/ks.txt

	private void setKS(String ksID, Session session) {
		if (ksID != null) {
			KompetenzsternTopic ks = (KompetenzsternTopic) as.getLiveTopic(ksID, 1);
			// Note: 4 attributes are set here (redundant)
			session.setAttribute("ksID", ksID);
			session.setAttribute("ks", ks);
			session.setAttribute("crits", ks.getKriterien());	// main criterias
			session.setAttribute("critTypeID", ks.getKriteriumTypeID());
			System.out.println("> \"ks\" stored in session: " + ks);
		} else {
			System.out.println("> \"ksID\" read from session: \"" + getKSID(session) + "\"");
		}
	}

	private void setCriteria(String critID, Session session) {
		KriteriumTopic crit = (KriteriumTopic) as.getLiveTopic(critID, 1);
		Vector subCrits = crit.getSubCriteria();
		session.setAttribute("critID", critID);
		session.setAttribute("crit", crit);
		session.setAttribute("subCrits", subCrits);
		System.out.println("> \"crit\" stored in session: " + crit);
	}

	// ---

	private BaseTopic getKS(Session session) {
		return (BaseTopic) session.getAttribute("ks");
	}

	private String getKSID(Session session) {
		return (String) session.getAttribute("ksID");
	}

	private String getKSCritTypeID(Session session) {
		return (String) session.getAttribute("critTypeID");
	}
}
