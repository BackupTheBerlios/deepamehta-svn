package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;



/**
 * A list of email recipients.
 * <p>
 * <hr>
 * Last change: 6.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class RecipientListTopic extends LiveTopic {



	// *****************
	// *** Constants ***
	// *****************



	private static Logger logger = Logger.getLogger("de.deepamehta");

	// preferences
	private static final String IMAGE_CHECKBOX = "checkbox.png";
	private static final String IMAGE_CHECKBOX_SELECTED = "checkbox-selected.png";
	private static final String IMAGE_CHECKBOX_DISABLED = "checkbox-disabled.png";

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



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_FOLLOW_HYPERLINK)) {
			String url = st.nextToken();
			String urlPrefix = "http://";
			if (!url.startsWith(urlPrefix)) {
				logger.warning("URL \"" + url + "\" not recognized by CMD_FOLLOW_HYPERLINK");
				return directives;
			}
			String action = url.substring(urlPrefix.length());
			if (action.startsWith(ACTION_SELECT_RECIPIENT)) {
				String topicID = action.substring(ACTION_SELECT_RECIPIENT.length() + 1);	// +1 to skip /
				selectRecipient(topicID, directives);
			} else {
				// delegate to super class to handle ACTION_REVEAL_TOPIC
				return super.executeCommand(command, session, topicmapID, viewmode);
			}
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
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



	public Vector getSelectedRecipients() {
		return cm.getRelatedTopics(getID(), SEMANTIC_SELECTED_RECIPIENT, 2);
	}

	// ---

	private void selectRecipient(String recipientID, CorporateDirectives directives) {
		as.toggleAssociation(getID(), recipientID, SEMANTIC_SELECTED_RECIPIENT);
		updateView(directives);
	}

	private void updateView(CorporateDirectives directives) {
		String html = renderRecipientList();
		//
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DESCRIPTION, html);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
	}

	// ---

	private String renderRecipientList() {
		Vector persons = getPersons();
		Vector institutions = getInstitutions();
		Vector selectedRecipients = getSelectedRecipients();
		StringBuffer html = new StringBuffer("<html><head></head><body>");
		//
		// persons
		Enumeration e = persons.elements();
		while (e.hasMoreElements()) {
			BaseTopic recipient = (BaseTopic) e.nextElement();
			renderRecipient(recipient, selectedRecipients, html);
		}
		// institutions
		e = institutions.elements();
		while (e.hasMoreElements()) {
			BaseTopic recipient = (BaseTopic) e.nextElement();
			renderRecipient(recipient, selectedRecipients, html);
		}
		//
		html.append("</body></html>");
		return html.toString();
	}

	private void renderRecipient(BaseTopic recipient, Vector selectedRecipients, StringBuffer html) {
		// decide checkbox image
		String emailAddress = as.getEmailAddress(recipient.getID());
		boolean isEnabled = emailAddress != null && emailAddress.length() > 0;
		boolean isSelected = selectedRecipients.contains(recipient);
		String checkboxImage = FILESERVER_IMAGES_PATH + (isEnabled ? isSelected ?
			IMAGE_CHECKBOX_SELECTED : IMAGE_CHECKBOX : IMAGE_CHECKBOX_DISABLED);
		// render checkbox
		html.append(isEnabled ? "<a href=\"http://" + ACTION_SELECT_RECIPIENT + "/" + recipient.getID() + "\">" : "");
		html.append("<img src=\"" + checkboxImage + "\" border=\"0\">");
		html.append(isEnabled ? "</a>" : "");
		// render link
		html.append(" <a href=\"http://" + ACTION_REVEAL_TOPIC + "/" + recipient.getID() + "\">" +
			recipient.getName() + "</a><br>");
	}

	// ---

	private Vector getPersons() {
		return cm.getTopics(TOPICTYPE_PERSON);			// get all persons in corporate memory ### may be big
	}

	private Vector getInstitutions() {
		return cm.getTopics(TOPICTYPE_INSTITUTION);		// get all persons in corporate memory ### may be big
	}
}
