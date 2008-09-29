package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
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
 * Last change: 29.9.2008 (2.0b8)<br>
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
	private static final String IMAGE_CHECKBOX_DISABLED_SELECTED = "checkbox-disabled-selected.png";

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
		updateRendering(directives, true);	// doUpdateView=true
		//
		return directives;
	}



	// ------------------------------------------
	// --- Reacting upon dedicated situations ---
	// ------------------------------------------



	public void clicked(String topicmapID, Session session, CorporateDirectives directives) {
		updateRendering(directives, false);	// doUpdateView=false
		super.clicked(topicmapID, session, directives);
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public CorporateCommands contextCommands(String topicmapID, String viewmode, Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		// --- navigation commands ---
		int editorContext = as.editorContext(topicmapID);
		commands.addNavigationCommands(this, editorContext, session);
		//
		// --- custom commands ---
		commands.addSeparator();
		// "Compose Email"
		commands.addCommand(as.string(ITEM_COMPOSE_EMAIL), CMD_COMPOSE_EMAIL, FILESERVER_IMAGES_PATH, ICON_COMPOSE_EMAIL);
		//
		// --- standard commands ---
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
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
		if (cmd.equals(CMD_COMPOSE_EMAIL)) {
			createChildTopic(TOPICTYPE_EMAIL, SEMANTIC_EMAIL_RECIPIENT, true, session, directives);	// reverseAssocDir=true
		} else if (cmd.equals(CMD_FOLLOW_HYPERLINK)) {
			String url = st.nextToken();
			String urlPrefix = "http://";
			if (!url.startsWith(urlPrefix)) {
				logger.warning("URL \"" + url + "\" not recognized by CMD_FOLLOW_HYPERLINK");
				return directives;
			}
			String action = url.substring(urlPrefix.length());
			if (action.startsWith(ACTION_SELECT_RECIPIENT)) {
				String topicID = action.substring(ACTION_SELECT_RECIPIENT.length() + 1);	// +1 to skip /
				selectRecipient(topicID, directives, true);		// doUpdateView=true
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

	/**
	 * References checked: 13.9.2008 (2.0b8)
	 *
	 * @see		#executeCommand
	 * @see		PersonSearchTopic#createRecipientList
	 */
	void selectRecipient(String recipientID, CorporateDirectives directives, boolean doUpdateView) {
		as.toggleAssociation(getID(), recipientID, SEMANTIC_SELECTED_RECIPIENT);
		if (doUpdateView) {
			updateRendering(directives, true);	// doUpdateView=true
		}
	}

	/**
	 * Renders the result list into the "Description" property.
	 * <p>
	 * References checked: 19.9.2008 (2.0b8)
	 *
	 * @param	doUpdateView	if <code>true</code>, a <code>DIRECTIVE_SHOW_TOPIC_PROPERTIES</code> is build.
	 *							if <code>false</code>, the property is written directly to corporate memory.
	 *
	 * @see		#evoke
	 * @see		#clicked
	 * @see		#selectRecipient
	 * @see		PersonSearchTopic#createRecipientList
	 */
	void updateRendering(CorporateDirectives directives, boolean doUpdateView) {
		String html = renderRecipientList();
		//
		if (doUpdateView) {
			Hashtable props = new Hashtable();
			props.put(PROPERTY_DESCRIPTION, html);
			directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
		} else {
			cm.setTopicData(getID(), 1, PROPERTY_DESCRIPTION, html);
		}
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
			BaseTopic person = (BaseTopic) e.nextElement();
			renderRecipient(person, selectedRecipients, html);
		}
		// institutions
		e = institutions.elements();
		while (e.hasMoreElements()) {
			BaseTopic institution = (BaseTopic) e.nextElement();
			renderRecipient(institution, selectedRecipients, html);
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
		String checkboxImage = FILESERVER_IMAGES_PATH + (isEnabled ? isSelected ? IMAGE_CHECKBOX_SELECTED : IMAGE_CHECKBOX :
			isSelected ? IMAGE_CHECKBOX_DISABLED_SELECTED : IMAGE_CHECKBOX_DISABLED);
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
