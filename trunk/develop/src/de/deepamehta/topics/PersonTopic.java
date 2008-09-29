package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Hashtable;
import java.util.Vector;



/**
 * Last change: 29.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class PersonTopic extends LiveTopic {



	// *****************
	// *** Constants ***
	// *****************



	private static final String ITEM_MAKE_APPOINTMENT = "Make Appointment";
	private static final String ICON_MAKE_APPOINTMENT = "make-appointment.gif";
	private static final String CMD_MAKE_APPOINTMENT = "makeAppointment";

	
	
	// *******************
	// *** Constructor ***
	// *******************



	public PersonTopic(BaseTopic topic, ApplicationService as) {
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
		// "Compose Email" ### copy in InstitutionTopic
		String emailAddress = as.getEmailAddress(getID());
		int cmdState = emailAddress != null && emailAddress.length() > 0 ? COMMAND_STATE_DEFAULT : COMMAND_STATE_DISABLED;
		commands.addCommand(as.string(ITEM_COMPOSE_EMAIL), CMD_COMPOSE_EMAIL, FILESERVER_IMAGES_PATH, ICON_COMPOSE_EMAIL, cmdState);
		// "Make Appointment"
		commands.addCommand(ITEM_MAKE_APPOINTMENT, CMD_MAKE_APPOINTMENT, FILESERVER_IMAGES_PATH, ICON_MAKE_APPOINTMENT);
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
			// ### copy in InstitutionTopic
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
		} else if (command.equals(CMD_MAKE_APPOINTMENT)) {
			CorporateDirectives directives = new CorporateDirectives();
			createChildTopic(TOPICTYPE_APPOINTMENT, SEMANTIC_APPOINTMENT_ATTENDEE, true, session, directives);	// reverseAssocDir=true
			return directives;
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps,
											String topicmapID, String viewmode, Session session) {
		CorporateDirectives directives = super.propertiesChanged(newProps, oldProps,
			topicmapID, viewmode, session);
		// Simplification: if this person is attendee in any appointment update all calendars
		Vector appointments = getCalendarAppointments();
		if (appointments.size() > 0) {
			CalendarTopic.updateAllCalendars(directives, as);
		}
		//
		return directives;
	}

	public String getNameProperty() {
		return null;
	}

	/**
	 * @param	props		contains only changed properties
	 *
	 * @see		de.deepamehta.service.ApplicationService#setTopicData
	 */
	public String getTopicName(Hashtable props, Hashtable oldProps) {
		// ### Note: a non-null value is only returned if topic name actually changes
		// ### probably the code tend to be too complicated and we should always return the name
		String forename = (String) props.get(PROPERTY_FIRST_NAME);
		String surname = (String) props.get(PROPERTY_NAME);
		String oldfore = (String) oldProps.get(PROPERTY_FIRST_NAME);
		String oldsur = (String) oldProps.get(PROPERTY_NAME);
		boolean f = forename != null;
		boolean s = surname != null;
		boolean of = oldfore != null;
		boolean os = oldsur != null;
		if (f && s) {
			return forename + " " + surname;
		} else if (f) {
			return forename + (os ? " " + oldsur : "");
		} else if (s) {
			return (of ? oldfore + " " : "") + surname;
		} else {
			return null;
		}
	}

	public static void propertyLabel(PropertyDefinition propertyDef, ApplicationService as, Session session) {
		String propName = propertyDef.getPropertyName();
		if (propName.equals(PROPERTY_NAME)) {
			propertyDef.setPropertyLabel("Last Name");
		}
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	public Vector getCalendars() {
		return cm.getRelatedTopics(getID(), SEMANTIC_CALENDAR_PERSON, TOPICTYPE_CALENDAR, 1);
	}

	/**
	 * @see		CalendarTopic#getAppointments
	 */
	public Vector getCalendarAppointments() {
		return cm.getRelatedTopics(getID(), SEMANTIC_APPOINTMENT_ATTENDEE, TOPICTYPE_APPOINTMENT, 1);
	}
}
