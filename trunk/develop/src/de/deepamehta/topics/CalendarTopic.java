package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaUtils;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.ApplicationService;
//
import java.text.DateFormat;
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



	// preferences
	private static final int CALENDAR_DAY_START_HOUR = 9;
	private static final int CALENDAR_DAY_END_HOUR = 22;
	private static final int CALENDAR_HOUR_SEGMENTS = 4;
	//
	private static final int CALENDAR_SEGMENT_SIZE = 60 / CALENDAR_HOUR_SEGMENTS;
	private static final int CALENDAR_DAY_SEGMENTS = CALENDAR_HOUR_SEGMENTS * (CALENDAR_DAY_END_HOUR - CALENDAR_DAY_START_HOUR);

	// commands
	private static final String ITEM_CREATE_EVENT = "Create Event";
	private static final String  CMD_CREATE_EVENT = "createEvent";
	private static final String ICON_CREATE_EVENT = "event.png";

	// properties
	private static final String PROPERTY_DISPLAY_DATE = "Display Date";



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



	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps,
											String topicmapID, String viewmode, Session session) {
		CorporateDirectives directives = super.propertiesChanged(newProps, oldProps,
			topicmapID, viewmode, session);
		// --- "Display Date" ---
		String prop = (String) newProps.get(PROPERTY_DISPLAY_DATE);
		if (prop != null) {
			System.out.println(">>> \"" + PROPERTY_DISPLAY_DATE + "\" property has changed " +
			"-- render calendar view");
			updateView(directives);
		}
		//
		return directives;
	}

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



	void updateView(CorporateDirectives directives) {
		Vector events = getEvents();
		System.out.println(">>> Update view of calender \"" + getName() + "\" (" + events.size() + " events)");
		// ### String html = renderEventsAsList(events);
		String html = renderWeekView(events);
		// ### setTopicData(PROPERTY_DESCRIPTION, html);
		// ### directives.add(as.setTopicProperty(getID(), 1, PROPERTY_DESCRIPTION, html, topicmapID, viewmode, session));
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DESCRIPTION, html);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
	}

	private String renderEventsAsList(Vector events) {
		StringBuffer html = new StringBuffer("<html><head></head><body>");
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
		return html.toString();
	}

	private String renderWeekView(Vector events) {
		String displayDateString = getProperty(PROPERTY_DISPLAY_DATE);
		if (!isSet(displayDateString)) {
			System.out.println("  > \"Display Date\" not set completely -- calendar view is not rendered");
			return "<html><head></head><body></body></html>";
		}
		Calendar displayDate = DeepaMehtaUtils.getCalendar(displayDateString);
		// display range: begin date
		int delta = (displayDate.get(Calendar.DAY_OF_WEEK) + 5) % 7;
		displayDate.add(Calendar.DATE, -delta);
		// display range: end date
		Calendar lastDisplayDate = (Calendar) displayDate.clone();
		lastDisplayDate.add(Calendar.DATE, 6);
		// --- make model ---
		GridCell[][] gridModel = makeGridModel(events, displayDate, lastDisplayDate);
		// --- rendering ---
		StringBuffer html = new StringBuffer("<html><head></head><body><table border=\"1\">");
		// heading
		DateFormat df = DateFormat.getDateInstance();
		Calendar cal = (Calendar) displayDate.clone();
		html.append("<tr><td>Mon-Sun</td>");
		for (int day = 0; day < 7; day++) {
			html.append("<td>" + df.format(cal.getTime()) + "</td>");
			cal.add(Calendar.DATE, 1);
		}
		html.append("</tr>");
		// body
		for (int seg = 0; seg < CALENDAR_DAY_SEGMENTS; seg++) {
			html.append("<tr valign=\"top\"><td>" + (seg % CALENDAR_HOUR_SEGMENTS == 0 ?
				CALENDAR_DAY_START_HOUR + seg / CALENDAR_HOUR_SEGMENTS + ":00" : "") + "</td>");
			for (int day = 0; day < 7; day++) {
				GridCell cell = gridModel[day][seg];
				if (cell == null) {
					html.append("<td></td>");
				} else if (cell.type == GridCell.BEGIN_OF_EVENT) {
					html.append("<td rowspan=\"" + cell.occupiedSegments + "\">");
					html.append("<b>" + cell.eventBegin + "</b><br>" + cell.eventName);
					html.append("</td>");
				}
			}
			html.append("</tr>");
		}
		html.append("</table></body></html>");
		return html.toString();
	}

	private GridCell[][] makeGridModel(Vector events, Calendar displayDate, Calendar lastDisplayDate) {
		GridCell[][] gridModel = new GridCell[7][CALENDAR_DAY_SEGMENTS];
		//
		String displayDateString = DeepaMehtaUtils.getDate(displayDate);
		String lastDisplayDateString = DeepaMehtaUtils.getDate(lastDisplayDate);
		//
		System.out.println("CalendarTopic.makeGridModel(): displayDate=" + displayDate.getTime() + "(" + displayDateString + ")");
		System.out.println("                           lastDisplayDate=" + lastDisplayDate.getTime() + "(" + lastDisplayDateString + ")");
		//
		int withinDayRange = 0;		// for diagnostics only
		int eventsDisplayed = 0;	// for diagnostics only
		//
		Enumeration e = events.elements();
		while (e.hasMoreElements()) {
			BaseTopic event = (BaseTopic) e.nextElement();
			String beginDate = getProperty(event, PROPERTY_BEGIN_DATE);
			int c1 = beginDate.compareTo(displayDateString);
			int c2 = beginDate.compareTo(lastDisplayDateString);
			// ignore events outside day range
			if (c1 < 0 || c2 > 0) {
				continue;
			}
			withinDayRange++;
			String beginTime = getProperty(event, PROPERTY_BEGIN_TIME);
			String endTime = getProperty(event, PROPERTY_END_TIME);
			// ignore events with unset time fields
			if (!isSet(beginTime) || !isSet(endTime)) {
				continue;
			}
			int beginMinuteOfDay = DeepaMehtaUtils.getMinutes(beginTime);
			int endMinuteOfDay = DeepaMehtaUtils.getMinutes(endTime);
			int beginSegment = (beginMinuteOfDay - 60 * CALENDAR_DAY_START_HOUR) / CALENDAR_SEGMENT_SIZE;
			int segmentCount = Math.max((endMinuteOfDay - beginMinuteOfDay) / CALENDAR_SEGMENT_SIZE, 1);
			int toIndex = beginSegment + segmentCount - 1;
			// ignore events outside time range
			if (toIndex < 0 || beginSegment >= CALENDAR_DAY_SEGMENTS) {
				continue;
			}
			eventsDisplayed++;
			// adjust boundings for events partially outside time range
			if (beginSegment < 0) {
				segmentCount += beginSegment;
				beginSegment = 0;
			}
			if (toIndex >= CALENDAR_DAY_SEGMENTS) {
				segmentCount += CALENDAR_DAY_SEGMENTS - toIndex - 1;
			}
			// --- add to model ---
			Calendar eventBegin = DeepaMehtaUtils.getCalendar(beginDate);
			int dayOfWeek = (eventBegin.get(Calendar.DAY_OF_WEEK) + 5) % 7;	// Mon=0 ... Sun=6
			// add beginning segment to model
			gridModel[dayOfWeek][beginSegment] = new GridCell(event.getID(), event.getName(), beginTime, segmentCount);
			// add ocupied segments to model
			for (int seg = beginSegment + 1; seg < beginSegment + segmentCount; seg++) {
				gridModel[dayOfWeek][seg] = new GridCell();
			}
		}
		System.out.println("events within day range:                  " + withinDayRange);
		System.out.println("displayed events (reasonable time range): " + eventsDisplayed);
		//
		return gridModel;
	}

	// ---

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
		// ### return !dateOrTime.equals("-/-/-") && !dateOrTime.equals("-:-");
		return dateOrTime.length() > 0 && dateOrTime.indexOf(VALUE_NOT_SET) == -1;
	}



	// *********************
	// *** Inner Classes ***
	// *********************



	private class GridCell {

		static final int BEGIN_OF_EVENT = 0;
		static final int OCCUPIED_BY_EVENT = 1;

		int type;
		String eventID, eventName, eventBegin;
		int occupiedSegments;

		GridCell() {
			type = OCCUPIED_BY_EVENT;
		}

		GridCell(String eventID, String eventName, String eventBegin, int occupiedSegments) {
			type = BEGIN_OF_EVENT;
			this.eventID = eventID;
			this.eventName = eventName;
			this.eventBegin = eventBegin;
			this.occupiedSegments = occupiedSegments;
		}
	}
}
