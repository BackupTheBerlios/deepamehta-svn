package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * Last functional change: 25.9.2007 (2.0b8)<br>
 * Last documentation update: 6.7.2007 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class EventTopic extends LiveTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public EventTopic(BaseTopic topic, ApplicationService as) {
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
		// 1) update the calendar directly connected to this event
		BaseTopic calendar = getCalendar();
		if (calendar != null) {
			((CalendarTopic) as.getLiveTopic(calendar)).updateView(directives);
		}
		// 2) update the calendars of the attendees of this event
		// ### Note: this way most calendars are updated more than once. Possible optimization: collect the
		// calendars first, remove the doublettes, and update only the remaining calendars
		Enumeration e = getAttendees().elements();
		while (e.hasMoreElements()) {
			BaseTopic person = (BaseTopic) e.nextElement();
			Vector calendars = ((PersonTopic) as.getLiveTopic(person)).getCalendars();
			updateCalendars(calendars, directives);
		}
		return directives;
	}



	// -----------------------------
	// --- Handling Associations ---
	// -----------------------------



	public void associated(String assocTypeID, String relTopicID, Session session, CorporateDirectives directives) {
		LiveTopic topic = as.getLiveTopic(relTopicID, 1);
		if (assocTypeID.equals(SEMANTIC_EVENT_ATTENDEE) && topic.getType().equals(TOPICTYPE_PERSON)) {
			System.out.println(">>> EventTopic.associated(): " + this + " associated with " + topic + " -- update calendars");
			Vector calendars = ((PersonTopic) topic).getCalendars();
			updateCalendars(calendars, directives);
		}
	}

	public void associationRemoved(String assocTypeID, String relTopicID, Session session, CorporateDirectives directives) {
		LiveTopic topic = as.getLiveTopic(relTopicID, 1);
		if (assocTypeID.equals(SEMANTIC_EVENT_ATTENDEE) && topic.getType().equals(TOPICTYPE_PERSON)) {
			System.out.println(">>> EventTopic.associationRemoved(): " + this + " disassociated from " + topic + " -- update calendars");
			Vector calendars = ((PersonTopic) topic).getCalendars();
			updateCalendars(calendars, directives);
		}
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	private BaseTopic getCalendar() {
		return as.getRelatedTopic(getID(), SEMANTIC_CALENDAR_EVENT, TOPICTYPE_CALENDAR, 1, true);	// emptyAllowed=true
	}

	public Vector getAttendees() {
		return cm.getRelatedTopics(getID(), SEMANTIC_EVENT_ATTENDEE, TOPICTYPE_PERSON, 2);
	}

	// ---

	private void updateCalendars(Vector calendars, CorporateDirectives directives) {
		Enumeration e = calendars.elements();
		while (e.hasMoreElements()) {
			BaseTopic calendar = (BaseTopic) e.nextElement();
			((CalendarTopic) as.getLiveTopic(calendar)).updateView(directives);
		}
	}
}
