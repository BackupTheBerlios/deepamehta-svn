package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * A list of email recipients.
 * <p>
 * <hr>
 * Last change: 2.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class RecipientListTopic extends LiveTopic {



	// *****************
	// *** Constants ***
	// *****************



	// actions
	private static final String ACTION_SELECT_RECIPIENT = "selectRecipient";



	// *******************
	// *** Constructor ***
	// *******************



	public RecipientListTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		// initial rendering
		updateView(directives);
		//
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public Vector disabledProperties(Session session) {
		Vector props = super.disabledProperties(session);
		props.addElement(PROPERTY_DESCRIPTION);
		return props;
	}

	public static Vector hiddenProperties(TypeTopic type) {
		Vector props = new Vector();
		props.addElement(PROPERTY_NAME);
		props.addElement(PROPERTY_ICON);
		return props;
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	private void updateView(CorporateDirectives directives) {
		Vector persons = getPersons();
		Vector institutions = getInstitutions();
		String html = renderRecipientList(persons, institutions);
		//
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DESCRIPTION, html);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
	}

	// ---

	private String renderRecipientList(Vector persons, Vector institutions) {
		StringBuffer html = new StringBuffer("<html><head></head><body>");
		html.append("<form action=\"controller\">");
		// persons
		Enumeration e = persons.elements();
		while (e.hasMoreElements()) {
			BaseTopic recipient = (BaseTopic) e.nextElement();
			renderRecipient(recipient, html);
		}
		//
		html.append("<input type=\"submit\" name=\"action\" value=\"Send\">");
		html.append("</form>");
		//
		html.append("</body></html>");
		return html.toString();
	}

	private void renderRecipient(BaseTopic recipient, StringBuffer html) {
		html.append("<input type=\"checkbox\" name=\"recipient\" value=\"" + recipient.getID() + "\" " +
			"onclick=\"location.href='controller?action=toggleRecipient=t-189512'\">");
		html.append("<a href=\"http://" + ACTION_SELECT_RECIPIENT + "/" + recipient.getID() + "\">" +
			recipient.getName() + "</a><br>");
	}

	// ---

	private Vector getPersons() {
		return cm.getTopics(TOPICTYPE_PERSON);
	}

	private Vector getInstitutions() {
		return cm.getTopics(TOPICTYPE_INSTITUTION);
	}
}
