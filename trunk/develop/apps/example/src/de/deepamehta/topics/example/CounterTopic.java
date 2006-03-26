package de.deepamehta.topics.example;

import java.util.Hashtable;
import java.util.Vector;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.LiveTopic;

public class CounterTopic extends LiveTopic {

	private static final String CMD_INCREASE_COUNTER  = "increaseCounter";
	private static final String CMD_RESET_COUNTER     = "resetCounter";
	
	private static final String ICON_INCREASE_COUNTER = "example-increase.png";
	private static final String ICON_RESET_COUNTER    = "example-reset.png";
	
	private static final String PROPERTY_VALUE        = "Value";
	
	/**
	 * Standard topic constructor
	 * @param topic
	 * @param as
	 */
	public CounterTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}
	
	
	// **********************
	// *** Defining Hooks ***
	// **********************
	
	// --------------------------
	// --- Providing Commands ---
	// --------------------------
	
	/* (non-Javadoc)
	 * @see de.deepamehta.topics.LiveTopic#contextCommands(java.lang.String, java.lang.String, de.deepamehta.service.Session, de.deepamehta.service.CorporateDirectives)
	 */
	public CorporateCommands contextCommands(String topicmapID, String viewmode, Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		commands.addNavigationCommands(this, as.editorContext(topicmapID), session);
		commands.addSeparator();
		commands.addCommand(Messages.getString("CounterTopic.increase"), CMD_INCREASE_COUNTER, //$NON-NLS-1$
				FILESERVER_ICONS_PATH, ICON_INCREASE_COUNTER);
		commands.addCommand(Messages.getString("CounterTopic.reset"), CMD_RESET_COUNTER,       //$NON-NLS-1$
				FILESERVER_ICONS_PATH, ICON_RESET_COUNTER);
		commands.addStandardCommands(this, as.editorContext(topicmapID), viewmode, session, directives);
		return commands;
	}
	
	// --------------------------
	// --- Executing Commands ---
	// --------------------------
	
	/* (non-Javadoc)
	 * @see de.deepamehta.topics.LiveTopic#executeCommand(java.lang.String, de.deepamehta.service.Session, java.lang.String, java.lang.String)
	 */
	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		if (command.equals(CMD_INCREASE_COUNTER)) {
			directives.add(increaseCounter());
		} else if (command.equals(CMD_RESET_COUNTER)) {
			directives.add(resetCounter());
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
	}
	
	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------
	
	/* (non-Javadoc)
	 * @see de.deepamehta.topics.LiveTopic#disabledProperties(de.deepamehta.service.Session)
	 */
	public Vector disabledProperties(Session session) {
		Vector properties = new Vector();
		properties.addElement(PROPERTY_VALUE);
		return properties;
	}
	
	// *******************
	// *** API Methods ***
	// *******************

	/**
	 * @return Returns the current value of the counter.
	 */
	public int getValue() {
		int current;
		try {
			current = Integer.parseInt(getProperty(PROPERTY_VALUE));
		} catch (NumberFormatException e) {
			current = 0;
		}
		return current; 
	}
	
	/**
	 * Increases the counter by one.
	 * @return Returns the directives that result from the action.
	 */
	public CorporateDirectives increaseCounter() {
		int current = getValue() + 1;
		return setTopicData(PROPERTY_VALUE, Integer.toString(current), null, null);
	}
	
	/**
	 * Resets the counter to zero.
	 * @return Returns the directives that result from the action.
	 */
	public CorporateDirectives resetCounter() {
		return setTopicData(PROPERTY_VALUE, "0", null, null);
	}
	
	// ***********************
	// *** Private Methods ***
	// ***********************
	
}

