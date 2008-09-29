package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Hashtable;



/**
 * Last change: 29.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class InstitutionTopic extends LiveTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public InstitutionTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



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
		// "Compose Email" ### copy in PersonTopic
		String emailAddress = as.getEmailAddress(getID());
		int cmdState = emailAddress != null && emailAddress.length() > 0 ? COMMAND_STATE_DEFAULT : COMMAND_STATE_DISABLED;
		commands.addCommand(as.string(ITEM_COMPOSE_EMAIL), CMD_COMPOSE_EMAIL, FILESERVER_IMAGES_PATH, ICON_COMPOSE_EMAIL, cmdState);
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
		if (command.equals(CMD_COMPOSE_EMAIL)) {
			// ### copy in PersonTopic
			CorporateDirectives directives = new CorporateDirectives();
			LiveTopic email = createChildTopic(TOPICTYPE_EMAIL, SEMANTIC_EMAIL_RECIPIENT, true, session, directives);	// reverseAssocDir=true
			// set recipient address
			String emailAddress = as.getEmailAddress(getID());
			if (emailAddress != null && emailAddress.length() > 0) {
				Hashtable props = new Hashtable();
				props.put(PROPERTY_TO, emailAddress);
				directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, email.getID(), props, new Integer(1));
			}
			return directives;
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
	}
}
