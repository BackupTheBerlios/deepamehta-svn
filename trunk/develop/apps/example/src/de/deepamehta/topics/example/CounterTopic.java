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
	
	/* (non-Javadoc)
	 * @see de.deepamehta.topics.LiveTopic#disabledProperties(de.deepamehta.service.Session)
	 */
	public Vector disabledProperties(Session session) {
		Vector properties = new Vector();
		properties.addElement(PROPERTY_VALUE);
		return properties;
	}

	/* (non-Javadoc)
	 * @see de.deepamehta.topics.LiveTopic#contextCommands(java.lang.String, java.lang.String, de.deepamehta.service.Session, de.deepamehta.service.CorporateDirectives)
	 */
	public CorporateCommands contextCommands(String topicmapID, String viewmode, Session session, CorporateDirectives directives) {
		CorporateCommands commands = super.contextCommands(topicmapID, viewmode, session, directives);
		commands.addSeparator();
		commands.addCommand(Messages.getString("CounterTopic.increase"), CMD_INCREASE_COUNTER, //$NON-NLS-1$
				FILESERVER_ICONS_PATH, ICON_INCREASE_COUNTER);
		commands.addCommand(Messages.getString("CounterTopic.reset"), CMD_RESET_COUNTER,       //$NON-NLS-1$
				FILESERVER_ICONS_PATH, ICON_RESET_COUNTER);
		return commands;
	}

	/* (non-Javadoc)
	 * @see de.deepamehta.topics.LiveTopic#executeCommand(java.lang.String, de.deepamehta.service.Session, java.lang.String, java.lang.String)
	 */
	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		Hashtable props = new Hashtable();
		if (command.equals(CMD_INCREASE_COUNTER)) {
			int value = increaseCounter();
			props.put(PROPERTY_VALUE, Integer.toString(value));
			directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
		} else if (command.equals(CMD_RESET_COUNTER)) {
			resetCounter();
			props.put(PROPERTY_VALUE, "0");
			directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
	}

	/**
	 * MISSDOC No documentation for method increaseCounter of type CounterTopic
	 */
	public int increaseCounter() {
		int current;
		try {
			current = Integer.parseInt(getProperty(PROPERTY_VALUE));
		} catch (NumberFormatException e) {
			current = 0;
		}
		current = current + 1;
		cm.setTopicData(getID(), 1, PROPERTY_VALUE, Integer.toString(current));
		return current;
	}
	
	/**
	 * MISSDOC No documentation for method resetCounter of type CounterTopic
	 */
	public void resetCounter() {
		cm.setTopicData(getID(), 1, PROPERTY_VALUE, "0");
	}
}

