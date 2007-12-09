package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Hashtable;
import java.util.Vector;



/**
 * Last functional change: 9.12.2007 (2.0b8)<br>
 * Last documentation update: 7.3.2004 (2.0b3-pre1)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class PersonTopic extends LiveTopic {



	private static final String ITEM_SEND_TO_PERSON = "Compose Email";
	private static final String ICON_SEND_TO_PERSON = "composeEmail.gif";
	private static final String CMD_SEND_TO_PERSON = "createNewMail";

	private static final String ITEM_MAKE_APPOINTMENT = "Make Appointment";
	private static final String ICON_MAKE_APPOINTMENT = "appointment.gif";
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
		// navigation commands
		int editorContext = as.editorContext(topicmapID);
		commands.addNavigationCommands(this, editorContext, session);
		//
		// custom commands
		commands.addSeparator();
		commands.addCommand(ITEM_SEND_TO_PERSON, CMD_SEND_TO_PERSON, FILESERVER_IMAGES_PATH, ICON_SEND_TO_PERSON);
		commands.addCommand(ITEM_MAKE_APPOINTMENT, CMD_MAKE_APPOINTMENT, FILESERVER_ICONS_PATH, ICON_MAKE_APPOINTMENT);
		//
		// standard commands
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		if (command.equals(CMD_SEND_TO_PERSON)) {
			CorporateDirectives directives = new CorporateDirectives();
			//
			String emailID = as.getNewTopicID();
			String personID = getID();
			PresentableTopic email = new PresentableTopic(emailID, 1, TOPICTYPE_EMAIL, 1, "", personID);
			// set recipient address
			String emailAdress = as.getEmailAddress(personID);
			if (emailAdress != null) {
				Hashtable props = new Hashtable();
				props.put(PROPERTY_TO, emailAdress);
				email.setProperties(props);
			}
			//
			String assocID = as.getNewAssociationID();
			PresentableAssociation assoc = new PresentableAssociation(assocID, 1, ASSOCTYPE_RECIPIENT, 1, "",
																	  emailID, 1, personID, 1 );
			directives.add(DIRECTIVE_SHOW_TOPIC, email, Boolean.TRUE);
			directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
			directives.add(DIRECTIVE_SELECT_TOPIC, emailID);
			//
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

	public Vector getCalendarAppointments() {
		return cm.getRelatedTopics(getID(), SEMANTIC_APPOINTMENT_ATTENDEE, TOPICTYPE_APPOINTMENT, 1);
	}
}
