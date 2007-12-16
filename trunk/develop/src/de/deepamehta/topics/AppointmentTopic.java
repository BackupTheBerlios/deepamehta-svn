package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * Last functional change: 15.12.2007 (2.0b8)<br>
 * Last documentation update: 6.7.2007 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class AppointmentTopic extends LiveTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public AppointmentTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps,
											String topicmapID, String viewmode, Session session) {
		CorporateDirectives directives = super.propertiesChanged(newProps, oldProps,
			topicmapID, viewmode, session);
		// 1) update the calendar directly connected to this appointment
		BaseTopic calendar = getCalendar();
		if (calendar != null) {
			((CalendarTopic) as.getLiveTopic(calendar)).updateView(directives);
		}
		// 2) update the calendars of the attendees of this appointment
		updateCalendars(directives);
		//
		return directives;
	}



	// -----------------------------
	// --- Handling Associations ---
	// -----------------------------



	public void associated(String assocTypeID, String relTopicID, Session session, CorporateDirectives directives) {
		LiveTopic topic = as.getLiveTopic(relTopicID, 1);
		if (assocTypeID.equals(SEMANTIC_APPOINTMENT_ATTENDEE) && topic.getType().equals(TOPICTYPE_PERSON)) {
			System.out.println(">>> AppointmentTopic.associated(): " + this + " associated with " + topic + " -- update calendars of " + getAttendees().size() + " attendees");
			// update the calendars of the attendees of this appointment
			updateCalendars(directives);
		}
	}

	public void associationRemoved(String assocTypeID, String relTopicID, Session session, CorporateDirectives directives) {
		LiveTopic topic = as.getLiveTopic(relTopicID, 1);
		if (assocTypeID.equals(SEMANTIC_APPOINTMENT_ATTENDEE) && topic.getType().equals(TOPICTYPE_PERSON)) {
			System.out.println(">>> AppointmentTopic.associationRemoved(): " + this + " disassociated from " + topic + " -- update calendars of " + getAttendees().size() + " attendees");
			// update the calendars of the attendees of this appointment
			updateCalendars(directives);
		}
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	private BaseTopic getCalendar() {
		return as.getRelatedTopic(getID(), SEMANTIC_CALENDAR_APPOINTMENT, TOPICTYPE_CALENDAR, 1, true);	// emptyAllowed=true
	}

	public BaseTopic getLocation() {
		return as.getRelatedTopic(getID(), SEMANTIC_APPOINTMENT_LOCATION, TOPICTYPE_LOCATION, 2, true);	// emptyAllowed=true
	}

	public Vector getAttendees() {
		return cm.getRelatedTopics(getID(), SEMANTIC_APPOINTMENT_ATTENDEE, TOPICTYPE_PERSON, 2);
	}

	// ---

	/**
	 * Updates the calendars of the attendees of this appointment.
	 */
	private void updateCalendars(CorporateDirectives directives) {
		// ### Note: this way most calendars are updated more than once. Possible optimization: collect the
		// calendars first, remove the doublettes, and update only the remaining calendars
		Enumeration e = getAttendees().elements();
		while (e.hasMoreElements()) {
			BaseTopic person = (BaseTopic) e.nextElement();
			Vector calendars = ((PersonTopic) as.getLiveTopic(person)).getCalendars();
			updateCalendars(calendars, directives);
		}
	}

	// ### copy in EventTopic
	private void updateCalendars(Vector calendars, CorporateDirectives directives) {
		Enumeration e = calendars.elements();
		while (e.hasMoreElements()) {
			BaseTopic calendar = (BaseTopic) e.nextElement();
			((CalendarTopic) as.getLiveTopic(calendar)).updateView(directives);
		}
	}
}
