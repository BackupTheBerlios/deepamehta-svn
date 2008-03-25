package de.deepamehta.messageboard.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.messageboard.MessageBoard;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.LiveTopic;
import de.deepamehta.util.DeepaMehtaUtils;

import java.util.Vector;



/**
 * A Message on a Message Board.
 * <p>
 * <hr>
 * Last functional change: 25.3.2008 (2.0b8)<br>
 * Last documentation update: 17.9.2002 (2.0a16-pre3)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class MessageTopic extends LiveTopic implements MessageBoard {



	// **************
	// *** Fields ***
	// **************



	// commands
	static final String ITEM_REPLY_TO_MESSAGE = "Reply";
	static final String CMD_REPLY_TO_MESSAGE = "replyToMessage";



	// *******************
	// *** Constructor ***
	// *******************



	public MessageTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		// set date
		String date = DeepaMehtaUtils.getDate();
		String time = DeepaMehtaUtils.getTime();
		setProperty(PROPERTY_DATE, date);
		setProperty(PROPERTY_LAST_REPLY_DATE, date);
		setProperty(PROPERTY_LAST_REPLY_TIME, time);
		// set author
		String user = session.getUserName();
		if (user != null) {		// ### Note: user is null when evoked through web interface
			setProperty(PROPERTY_FROM, user);
		}
		//
		return super.evoke(session, topicmapID, viewmode);
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public Vector disabledProperties(Session session) {
		Vector props = super.disabledProperties(session);
		// Note: if these 3 properties are already added by the superclass they are added
		// here again and thus are contained twice in the vector, but this is no problem
		props.addElement(PROPERTY_DATE);
		props.addElement(PROPERTY_LAST_REPLY_DATE);
		props.addElement(PROPERTY_LAST_REPLY_TIME);
		return props;
	}

	public static void propertyLabel(PropertyDefinition propDef, ApplicationService as, Session session) {
		String propName = propDef.getPropertyName();
		if (propName.equals(PROPERTY_NAME)) {
			propDef.setPropertyLabel(PROPERTY_SUBJECT);
		} else if (propName.equals(PROPERTY_DESCRIPTION)) {
			propDef.setPropertyLabel("Message");
		}
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		int editorContext = as.editorContext(topicmapID);
		//
		commands.addNavigationCommands(this, editorContext, session);
		// --- "Reply" ---
		int cmdState = isToplevelMessage() ? COMMAND_STATE_DEFAULT : COMMAND_STATE_DISABLED;
		commands.addSeparator();
		commands.addCommand(ITEM_REPLY_TO_MESSAGE, CMD_REPLY_TO_MESSAGE, cmdState);
		//
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String actionCommand, Session session,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		if (actionCommand.equals(CMD_REPLY_TO_MESSAGE)) {
			createChildTopic(TOPICTYPE_MESSAGE, SEMANTIC_MESSAGE_HIERARCHY, session, directives);
			return directives;
		} else {
			return super.executeCommand(actionCommand, session, topicmapID, viewmode);
		}
	}



	// **********************
	// *** Public Methods ***
	// **********************



	public BaseTopic getToplevelMessage() {
		if (isToplevelMessage()) {
			return this;
		}
		BaseTopic message = as.getRelatedTopic(getID(), SEMANTIC_MESSAGE_HIERARCHY,
			TOPICTYPE_MESSAGE, 1, false);		// emptyAllowed=false ### throws DME, ASE
		return message;
	}

	public boolean isToplevelMessage() {
		BaseTopic messageBoard = as.getRelatedTopic(getID(), SEMANTIC_MESSAGE_HIERARCHY,
			TOPICTYPE_MESSAGE_BOARD, 1, true);		// emptyAllowed=true ### throws ASE
		return messageBoard != null;
	}
}
