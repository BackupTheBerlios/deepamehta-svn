package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
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
public class CalendarTopic extends LiveTopic {



	// *****************
	// *** Constants ***
	// *****************



	private static final String ITEM_CREATE_EVENT = "Create Event";
	private static final String  CMD_CREATE_EVENT = "createEvent";
	private static final String ICON_CREATE_EVENT = "event.png";



	// *******************
	// *** Constructor ***
	// *******************



	public CalendarTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public CorporateCommands contextCommands(String topicmapID, String viewmode, Session session,
																	CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		int editorContext = as.editorContext(topicmapID);
		//
		commands.addNavigationCommands(this, editorContext, session);
		// --- "Create Event" command ---
		commands.addSeparator();
		int cmdState = session.isDemo() ? COMMAND_STATE_DISABLED : COMMAND_STATE_DEFAULT;
		commands.addCommand(ITEM_CREATE_EVENT, CMD_CREATE_EVENT,
			FILESERVER_ICONS_PATH, ICON_CREATE_EVENT, cmdState);
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
		if (actionCommand.equals(CMD_CREATE_EVENT)) {
			createChildTopic(TOPICTYPE_EVENT, SEMANTIC_CALENDAR_EVENT, session, directives);
			return directives;
		} else {
			return super.executeCommand(actionCommand, session, topicmapID, viewmode);
		}
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public Vector disabledProperties(Session session) {
		Vector props = super.disabledProperties(session);
		// Note: if this property is already added by the superclass it is added
		// here again and thus are contained twice in the vector, but this is no problem
		props.addElement(PROPERTY_DESCRIPTION);
		return props;
	}



	// *****************
	// *** Utilities ***
	// *****************



	void update() {
		StringBuffer html = new StringBuffer("<html><head></head><body>");
		Vector events = getEvents();
		System.out.println(">>> Calender \"" + getName() + "\" updated (" + events.size() + " events)");
		Enumeration e = events.elements();
		while (e.hasMoreElements()) {
			BaseTopic event = (BaseTopic) e.nextElement();
			Hashtable props = as.getTopicProperties(event);
			String name = (String) props.get(PROPERTY_NAME);	// note: event.getName() doesn't work here, because the
			// propertiesChanged() hook is triggered after the properties are updated in CM but _before_ the topic name
			// is updated in CM.
			String description = getHTMLBodyContent((String) props.get(PROPERTY_DESCRIPTION));
			String beginDate = (String) props.get(PROPERTY_BEGIN_DATE);
			String beginTime = (String) props.get(PROPERTY_BEGIN_TIME);
			String endDate = (String) props.get(PROPERTY_END_DATE);
			String endTime = (String) props.get(PROPERTY_END_TIME);
			html.append("<p>" + timeRange(beginDate, beginTime, endDate, endTime) +
				" <b>" + name + "</b></p>" + description);
		}
		html.append("</body></html>");
		setTopicData(PROPERTY_DESCRIPTION, html.toString());
	}

	private Vector getEvents() {
		String[] sortProps = {PROPERTY_BEGIN_DATE, PROPERTY_BEGIN_TIME};
		return cm.getRelatedTopics(getID(), SEMANTIC_CALENDAR_EVENT, TOPICTYPE_EVENT, 2, sortProps, true);	// descending=true
	}

	// ---

	private String timeRange(String beginDate, String beginTime, String endDate, String endTime) {
		StringBuffer range = new StringBuffer(beginDate);
		if (isSet(beginTime)) {
			range.append(" " + beginTime);
		}
		if (isSet(endDate) || isSet(endTime)) {
			range.append(" -");
		}
		if (isSet(endDate)) {
			range.append(" " + endDate);
		}
		if (isSet(endTime)) {
			range.append(" " + endTime);
		}
		return range.toString();
	}

	private String getHTMLBodyContent(String html) {
		int i1 = html.indexOf("<body>");
		int i2 = html.indexOf("</body>");
		if (i1 == -1 || i2 == -1) {
			throw new DeepaMehtaException("no HTML body content found in \"" + html + "\"");
		}
		return html.substring(i1 + 6, i2);
	}

	private boolean isSet(String dateOrTime) {
		return !dateOrTime.equals("-/-/-") && !dateOrTime.equals("-:-");
	}
}
