package de.deepamehta.topics;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.util.DeepaMehtaUtils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * Last functional change: 17.9.2007 (2.0b8)<br>
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

	// actions
	private static final String ACTION_REVEAL_EVENT = "revealEvent";
	private static final String ACTION_SELECT_DAY_MODE = "selectDayMode";
	private static final String ACTION_SELECT_WEEK_MODE = "selectWeekMode";
	private static final String ACTION_SELECT_MONTH_MODE = "selectMonthMode";
	private static final String ACTION_GO_BACK = "goBack";
	private static final String ACTION_GO_FORWARD = "goForward";

	// properties
	private static final String PROPERTY_DISPLAY_MODE = "Display Mode";
	private static final String PROPERTY_DISPLAY_DATE = "Display Date";

	// property values
	private static final String DISPLAY_MODE_DAY = "Day";
	private static final String DISPLAY_MODE_WEEK = "Week";
	private static final String DISPLAY_MODE_MONTH = "Month";



	// *******************
	// *** Constructor ***
	// *******************



	public CalendarTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		// setup calendar for today
		setTopicData(PROPERTY_DISPLAY_MODE, DISPLAY_MODE_WEEK);
		setTopicData(PROPERTY_DISPLAY_DATE, DeepaMehtaUtils.getDate());
		// initial rendering
		updateView(directives);
		//
		return directives;
	}



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



	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_CREATE_EVENT)) {
			createChildTopic(TOPICTYPE_EVENT, SEMANTIC_CALENDAR_EVENT, session, directives);
			return directives;
		} else if (cmd.equals(CMD_FOLLOW_HYPERLINK)) {
			String url = st.nextToken();
			String urlPrefix = "http://";
			if (!url.startsWith(urlPrefix)) {
				System.out.println("*** CalendarTopic.executeCommand(): URL \"" + url + "\" not recognized by " +
					"CMD_FOLLOW_HYPERLINK");
				return directives;
			}
			String action = url.substring(urlPrefix.length());
			if (action.startsWith(ACTION_REVEAL_EVENT)) {
				String eventID = action.substring(ACTION_REVEAL_EVENT.length() + 1);	// +1 to skip /
				revealEvent(eventID, directives);
			} else if (action.equals(ACTION_SELECT_DAY_MODE)) {
				selectDayMode(directives);
			} else if (action.equals(ACTION_SELECT_WEEK_MODE)) {
				selectWeekMode(directives);
			} else if (action.equals(ACTION_SELECT_MONTH_MODE)) {
				selectMonthMode(directives);
			} else if (action.equals(ACTION_GO_BACK)) {
				navigate(-1, directives);
			} else if (action.equals(ACTION_GO_FORWARD)) {
				navigate(1, directives);
			} else {
				System.out.println("*** CalendarTopic.executeCommand(): URL \"" + url + "\" not recognized by " +
					"CMD_FOLLOW_HYPERLINK");
			}
			return directives;
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	// ### to be dropped
	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps,
											String topicmapID, String viewmode, Session session) {
		CorporateDirectives directives = super.propertiesChanged(newProps, oldProps,
			topicmapID, viewmode, session);
		// --- "Display Date" ---
		String prop = (String) newProps.get(PROPERTY_DISPLAY_DATE);
		if (prop != null) {
			System.out.println(">>> \"" + PROPERTY_DISPLAY_DATE + "\" property has changed -- update calendar view");
			updateView(directives);
		}
		// --- "Display Mode" ---
		prop = (String) newProps.get(PROPERTY_DISPLAY_MODE);
		if (prop != null) {
			System.out.println(">>> \"" + PROPERTY_DISPLAY_MODE + "\" property has changed -- update calendar view");
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

	public static Vector hiddenProperties(TypeTopic type) {
		Vector props = new Vector();
		props.addElement(PROPERTY_DISPLAY_MODE);
		props.addElement(PROPERTY_DISPLAY_DATE);
		props.addElement(PROPERTY_ICON);
		return props;
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	void updateView(CorporateDirectives directives) {
		Vector events = getEvents();
		System.out.println(">>> Update view of calender \"" + getName() + "\" (" + events.size() + " events)");
		// ### String html = renderListView(events);
		// ### setTopicData(PROPERTY_DESCRIPTION, html);
		// ### directives.add(as.setTopicProperty(getID(), 1, PROPERTY_DESCRIPTION, html, topicmapID, viewmode, session));
		String html;
		String displayMode = getProperty(PROPERTY_DISPLAY_MODE);
		if (displayMode.equals(DISPLAY_MODE_DAY)) {
			html = renderDayView(events);
		} else if (displayMode.equals(DISPLAY_MODE_WEEK)) {
			html = renderWeekView(events);
		} else if (displayMode.equals(DISPLAY_MODE_MONTH)) {
			html = renderMonthView(events);
		} else {
			throw new DeepaMehtaException("unexpected calendar display mode: \"" + displayMode + "\"");
		}
		//
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DESCRIPTION, html);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
	}

	private String renderDayView(Vector events) {
		String displayDateString = getProperty(PROPERTY_DISPLAY_DATE);
		StringBuffer html = new StringBuffer("<html><head><link href=\"stylesheets/calendar.css\" rel=\"stylesheet\" " +
			"type=\"text/css\"></head><body>");
		html.append(renderTimeControls());
		html.append("<p>The day view is not yet implemented. But you already can scroll the date day-wise.</p>");
		html.append("<p>Current date: " + displayDateString + "</p>");
		html.append("</body></html>");
		return html.toString();
	}

	private String renderWeekView(Vector events) {
		String displayDateString = getProperty(PROPERTY_DISPLAY_DATE);
		// error check ### to be droppped
		if (!isSet(displayDateString)) {
			System.out.println("  > \"Display Date\" not set completely -- rendering not yet possible");
			return "<html><body></body></html>";
		}
		//
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
		StringBuffer html = new StringBuffer("<html><head><link href=\"stylesheets/calendar.css\" rel=\"stylesheet\" " +
			"type=\"text/css\"></head><body>");
		html.append(renderTimeControls());
		// heading
		DateFormat df = DateFormat.getDateInstance();
		Calendar cal = (Calendar) displayDate.clone();
		html.append("<table><tr><td>Mon-Sun</td>");
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
					html.append("<td class=\"event\" rowspan=\"" + cell.occupiedSegments + "\">");
					html.append("<b>" + cell.eventBegin + "</b><br>");
					html.append("<a href=\"http://" + ACTION_REVEAL_EVENT + "/" + cell.eventID + "\">" + cell.eventName + "</a>");
					html.append("</td>");
				}
			}
			html.append("</tr>");
		}
		html.append("</table></body></html>");
		return html.toString();
	}

	private String renderMonthView(Vector events) {
		String displayDateString = getProperty(PROPERTY_DISPLAY_DATE);
		StringBuffer html = new StringBuffer("<html><head><link href=\"stylesheets/calendar.css\" rel=\"stylesheet\" " +
			"type=\"text/css\"></head><body>");
		html.append(renderTimeControls());
		html.append("<p>The month view is not yet implemented. But you already can scroll the date month-wise.</p>");
		html.append("<p>Current date: " + displayDateString + "</p>");
		html.append("</body></html>");
		return html.toString();
	}

	private String renderListView(Vector events) {
		StringBuffer html = new StringBuffer("<html><body>");
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

	// ---

	private String renderTimeControls() {
		String displayMode = getProperty(PROPERTY_DISPLAY_MODE);
		StringBuffer html = new StringBuffer();
		html.append("<a href=\"http://" + ACTION_GO_BACK + "\"><img src=\"images/button-arrow-left.png\" border=\"0\"></a>");
		html.append("<a href=\"http://" + ACTION_SELECT_DAY_MODE + "\"><img src=\"images/button-day" + (displayMode.equals(DISPLAY_MODE_DAY) ? "-activated" : "") + ".png\" border=\"0\"></a>");
		html.append("<a href=\"http://" + ACTION_SELECT_WEEK_MODE + "\"><img src=\"images/button-week" + (displayMode.equals(DISPLAY_MODE_WEEK) ? "-activated" : "") + ".png\" border=\"0\"></a>");
		html.append("<a href=\"http://" + ACTION_SELECT_MONTH_MODE + "\"><img src=\"images/button-month" + (displayMode.equals(DISPLAY_MODE_MONTH) ? "-activated" : "") + ".png\" border=\"0\"></a>");
		html.append("<a href=\"http://" + ACTION_GO_FORWARD + "\"><img src=\"images/button-arrow-right.png\" border=\"0\"></a>");
		return html.toString();
	}

	// ---

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
			// 1) add beginning segment to model
			String eventName = getProperty(event, PROPERTY_NAME);	// note: event.getName() doesn't work here, because the
			// propertiesChanged() hook is triggered after the properties are updated in CM but _before_ the topic name
			// is updated in CM.
			gridModel[dayOfWeek][beginSegment] = new GridCell(event.getID(), eventName, beginTime, segmentCount);
			// 2) add ocupied segments to model
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
		Vector events;
		// 1) add events directly connected to this calendar
		// ### sorting not needed anymore
		String[] sortProps = {PROPERTY_BEGIN_DATE, PROPERTY_BEGIN_TIME};
		events = cm.getRelatedTopics(getID(), SEMANTIC_CALENDAR_EVENT, TOPICTYPE_EVENT, 2, sortProps, true);	// descending=true
		// 2) add events of the persons connected to this calendar
		Enumeration e = getCalendarPersons().elements();
		while (e.hasMoreElements()) {
			BaseTopic person = (BaseTopic) e.nextElement();
			Vector personEvents = ((PersonTopic) as.getLiveTopic(person)).getCalendarEvents();
			events.addAll(personEvents);
		}
		return events;
	}

	private Vector getCalendarPersons() {
		return cm.getRelatedTopics(getID(), SEMANTIC_CALENDAR_PERSON, TOPICTYPE_PERSON, 2);
	}

	// ---

	private void revealEvent(String eventID, CorporateDirectives directives) {
		PresentableTopic event = new PresentableTopic(as.getLiveTopic(eventID, 1), getID());
		BaseAssociation a = cm.getAssociation(SEMANTIC_CALENDAR_EVENT, getID(), eventID);
		Boolean evoke = Boolean.FALSE;
		if (a == null) {
			// create a "virtual" association of type "Search Result" if not yet exist
			a = cm.getAssociation(SEMANTIC_CONTAINER_HIERARCHY, getID(), eventID);
			if (a == null) {
				String assocID = as.getNewAssociationID();
				a = new BaseAssociation(assocID, 1, SEMANTIC_CONTAINER_HIERARCHY, 1, "", getID(), 1, eventID, 1);
				evoke = Boolean.TRUE;
			}
		}
		//
		PresentableAssociation assoc = new PresentableAssociation(a);
		directives.add(DIRECTIVE_SHOW_TOPIC, event);
		directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, evoke);
		directives.add(DIRECTIVE_SELECT_TOPIC, eventID);
	}

	// ---

	private void selectDayMode(CorporateDirectives directives) {
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DISPLAY_MODE, DISPLAY_MODE_DAY);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
	}

	private void selectWeekMode(CorporateDirectives directives) {
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DISPLAY_MODE, DISPLAY_MODE_WEEK);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
	}

	private void selectMonthMode(CorporateDirectives directives) {
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DISPLAY_MODE, DISPLAY_MODE_MONTH);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
	}

	// ---

	private void navigate(int amount, CorporateDirectives directives) {
		String displayDateString = getProperty(PROPERTY_DISPLAY_DATE);
		// error check ### to be droppped
		if (!isSet(displayDateString)) {
			System.out.println("  > \"Display Date\" not set completely -- navigation not yet possible");
			return;
		}
		//
		Calendar displayDate = DeepaMehtaUtils.getCalendar(displayDateString);
		//
		String displayMode = getProperty(PROPERTY_DISPLAY_MODE);
		int field;
		if (displayMode.equals(DISPLAY_MODE_DAY)) {
			field = Calendar.DATE;
		} else if (displayMode.equals(DISPLAY_MODE_WEEK)) {
			field = Calendar.WEEK_OF_YEAR;
		} else if (displayMode.equals(DISPLAY_MODE_MONTH)) {
			field = Calendar.MONTH;
		} else {
			throw new DeepaMehtaException("unexpected calendar display mode: \"" + displayMode + "\"");
		}
		//
		displayDate.add(field, amount);
		//
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DISPLAY_DATE, DeepaMehtaUtils.getDate(displayDate));
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
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
