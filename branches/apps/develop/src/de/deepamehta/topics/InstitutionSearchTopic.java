package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



// ### copy of PersonSearchTopic

/**
 * An institution search.
 * <p>
 * <hr>
 * Last change: 29.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class InstitutionSearchTopic extends TopicContainerTopic {



	// *************
	// *** Field ***
	// *************



	// commands
	private static final String ITEM_CREATE_RECIPIENT_LIST = "Create Recipient List";
	private static final String CMD_CREATE_RECIPIENT_LIST = "createRecipientList";
	private static final String ICON_CREATE_RECIPIENT_LIST = "authentificationsource.gif";



	// *******************
	// *** Constructor ***
	// *******************



	public InstitutionSearchTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public CorporateCommands contextCommands(String topicmapID, String viewmode,
									Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		commands.addCommand(ITEM_CREATE_RECIPIENT_LIST, CMD_CREATE_RECIPIENT_LIST,
			FILESERVER_ICONS_PATH, ICON_CREATE_RECIPIENT_LIST);
		commands.addSeparator();
		commands.add(super.contextCommands(topicmapID, viewmode, session, directives));
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		if (command.equals(CMD_CREATE_RECIPIENT_LIST)) {
			createRecipientList(session, directives);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	private void createRecipientList(Session session, CorporateDirectives directives) {
		RecipientListTopic recipientList = (RecipientListTopic) createChildTopic(TOPICTYPE_RECIPIENT_LIST,
			SEMANTIC_CONTAINER_HIERARCHY, session, directives);
		//
		Enumeration e = getContent().elements();
		while (e.hasMoreElements()) {
			String institutionID = ((String[]) e.nextElement())[0];
			recipientList.selectRecipient(institutionID, directives, false);		// doUpdateView=false
		}
		//
		recipientList.updateRendering(directives, true);	// doUpdateView=true
	}
}
