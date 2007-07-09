package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.ApplicationService;
//
import java.util.*;



/**
 * Last functional change: 6.7.2007 (2.0b8)<br>
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
		getCalendar().update();
		return directives;
	}



	// *****************
	// *** Utilities ***
	// *****************



	private CalendarTopic getCalendar() {
		BaseTopic calendar = as.getRelatedTopic(getID(), SEMANTIC_CALENDAR_EVENT, TOPICTYPE_CALENDAR, 1, false);	// emptyAllowed=false
		return (CalendarTopic) as.getLiveTopic(calendar);
	}
}
