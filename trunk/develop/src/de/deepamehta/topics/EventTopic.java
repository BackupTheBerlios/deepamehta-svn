package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * Last functional change: 14.12.2007 (2.0b8)<br>
 * Last documentation update: 14.12.2007 (2.0b8)<br>
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
		//
		updateCalendars(getCalendars(), directives);
		//
		return directives;
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	private Vector getCalendars() {
		return cm.getTopics(TOPICTYPE_CALENDAR);
	}

	// ### copy in AppointmentTopic
	private void updateCalendars(Vector calendars, CorporateDirectives directives) {
		Enumeration e = calendars.elements();
		while (e.hasMoreElements()) {
			BaseTopic calendar = (BaseTopic) e.nextElement();
			((CalendarTopic) as.getLiveTopic(calendar)).updateView(directives);
		}
	}
}
