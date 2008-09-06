package de.deepamehta.topics;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateMemory;
import de.deepamehta.service.Session;
import de.deepamehta.util.DeepaMehtaUtils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * Last change: 6.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
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

	// actions
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



	// ************************
	// *** Overriding Hooks ***
	// ************************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		// setup calendar for today
		setProperty(PROPERTY_DISPLAY_MODE, DISPLAY_MODE_WEEK);
		setProperty(PROPERTY_DISPLAY_DATE, DeepaMehtaUtils.getDate());
		// initial rendering
		updateView(directives);
		//
		return directives;
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
		if (cmd.equals(CMD_FOLLOW_HYPERLINK)) {
			String url = st.nextToken();
			String urlPrefix = "http://";
			if (!url.startsWith(urlPrefix)) {
				System.out.println("*** CalendarTopic.executeCommand(): URL \"" + url + "\" not recognized by " +
					"CMD_FOLLOW_HYPERLINK");
				return directives;
			}
			String action = url.substring(urlPrefix.length());
			if (action.equals(ACTION_SELECT_DAY_MODE)) {
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
				// delegate to super class to handle ACTION_REVEAL_TOPIC
				return super.executeCommand(command, session, topicmapID, viewmode);
			}
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		//
		return directives;
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



	// -----------------------------
	// --- Handling Associations ---
	// -----------------------------



	public void associated(String assocTypeID, String relTopicID, Session session, CorporateDirectives directives) {
		LiveTopic topic = as.getLiveTopic(relTopicID, 1);
		if (assocTypeID.equals(SEMANTIC_CALENDAR_PERSON) && (topic.getType().equals(TOPICTYPE_PERSON) ||
															 topic.getType().equals(TOPICTYPE_APPOINTMENT))) {
			System.out.println(">>> CalendarTopic.associated(): " + this + " associated with " + topic + " -- update this calendar");
			updateView(directives);
		}
	}

	public void associationRemoved(String assocTypeID, String relTopicID, Session session, CorporateDirectives directives) {
		LiveTopic topic = as.getLiveTopic(relTopicID, 1);
		if (assocTypeID.equals(SEMANTIC_CALENDAR_PERSON) && (topic.getType().equals(TOPICTYPE_PERSON) ||
															 topic.getType().equals(TOPICTYPE_APPOINTMENT))) {
			System.out.println(">>> CalendarTopic.associationRemoved(): " + this + " disassociated from " + topic + " -- update this calendar");
			updateView(directives);
		}
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	/**
	 * @see		#updateAllCalendars
	 */
	static Vector getAllCalendars(CorporateMemory cm) {
		return cm.getTopics(TOPICTYPE_CALENDAR);
	}

	/**
	 * @see		EventTopic#die
	 * @see		EventTopic#propertiesChanged
	 * @see		PersonTopic#propertiesChanged
	 */
	static void updateAllCalendars(CorporateDirectives directives, ApplicationService as) {
		Vector calendars = getAllCalendars(as.cm);
		updateCalendars(calendars, directives, as);
	}

	/**
	 * @see		#updateAllCalendars
	 * @see		AppointmentTopic#updateCalendars
	 */
	static void updateCalendars(Vector calendars, CorporateDirectives directives, ApplicationService as) {
		Enumeration e = calendars.elements();
		while (e.hasMoreElements()) {
			BaseTopic calendar = (BaseTopic) e.nextElement();
			((CalendarTopic) as.getLiveTopic(calendar)).updateView(directives);
		}
	}

	// ---

	/**
	 * Refreshs the view (HTML rendering) of this calendar.
	 * All content is retrieved from the corporate memory, the display model is build, and HTML is rendered.
	 * <p>
	 * References checked: 20.4.2008 (2.0b8)
	 *
	 * @see		evoke
	 * @see		propertiesChanged
	 * @see		associated
	 * @see		associationRemoved
	 * @see		updateCalendars
	 */
	private void updateView(CorporateDirectives directives) {
		Vector appointments = getAppointments();
		Vector events = getEvents();
		System.out.println(">>> Update calender \"" + getName() + "\" (" + appointments.size() + " appointments, " +
			events.size() + " events)");
		// ### String html = renderListView(appointments);
		String html;
		String displayMode = getProperty(PROPERTY_DISPLAY_MODE);
		if (displayMode.equals(DISPLAY_MODE_DAY)) {
			html = renderDayView(appointments);
		} else if (displayMode.equals(DISPLAY_MODE_WEEK)) {
			html = renderWeekView(appointments, events);
		} else if (displayMode.equals(DISPLAY_MODE_MONTH)) {
			html = renderMonthView(appointments, events);
		} else {
			throw new DeepaMehtaException("unexpected calendar display mode: \"" + displayMode + "\"");
		}
		//
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DESCRIPTION, html);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
	}

	private String renderDayView(Vector appointments) {
		String displayDateString = getProperty(PROPERTY_DISPLAY_DATE);
		Calendar displayDate = DeepaMehtaUtils.getCalendar(displayDateString);
		StringBuffer html = new StringBuffer("<html><head><link href=\"stylesheets/calendar.css\" rel=\"stylesheet\" " +
			"type=\"text/css\"></head><body>");
		html.append(renderTimeControls());
		html.append("<p>The day view is not yet implemented. But you already can scroll the date day-wise.</p>");
		html.append("<p>Current date: " + displayDateString + "</p>");
		html.append("<p>Week of Month: " + displayDate.get(Calendar.WEEK_OF_MONTH) + "</p>");
		html.append("</body></html>");
		return html.toString();
	}

	private String renderWeekView(Vector appointments, Vector events) {
		String displayDateString = getProperty(PROPERTY_DISPLAY_DATE);
		Calendar displayDate = DeepaMehtaUtils.getCalendar(displayDateString);
		// display range: begin date
		int delta = (displayDate.get(Calendar.DAY_OF_WEEK) + 5) % 7;
		displayDate.add(Calendar.DATE, -delta);
		// display range: end date
		Calendar lastDisplayDate = (Calendar) displayDate.clone();
		lastDisplayDate.add(Calendar.DATE, 6);
		// --- make model ---
		Vector[] weekAppointmentModel = makeWeekAppointmentModel(appointments, displayDate, lastDisplayDate);
		Vector weekEventModel = makeWeekEventModel(events, displayDate, lastDisplayDate);
		// --- rendering ---
		StringBuffer html = new StringBuffer("<html><head><link href=\"stylesheets/calendar.css\" rel=\"stylesheet\" " +
			"type=\"text/css\"></head><body>");
		html.append(renderTimeControls());
		// - heading -
		DateFormat df = DateFormat.getDateInstance();
		Calendar cal = (Calendar) displayDate.clone();
		html.append("<table><tr valign=\"top\"><td>Mon-Sun</td>");
		for (int day = 0; day < 7; day++) {
			int daySlotCount = daySlotCount(day, weekAppointmentModel);
			html.append("<td colspan=\"" + daySlotCount + "\">" + df.format(cal.getTime()) + "</td>");
			cal.add(Calendar.DATE, 1);
		}
		html.append("</tr>");
		// - body -
		renderWeekEvents(weekEventModel, weekAppointmentModel, html);
		renderWeekAppointments(weekAppointmentModel, html);
		//
		html.append("</table></body></html>");
		return html.toString();
	}

	private void renderWeekAppointments(Vector[] weekAppointmentModel, StringBuffer html) {
		for (int seg = 0; seg < CALENDAR_DAY_SEGMENTS; seg++) {
			html.append("<tr valign=\"top\"><td>" + (seg % CALENDAR_HOUR_SEGMENTS == 0 ?
				CALENDAR_DAY_START_HOUR + seg / CALENDAR_HOUR_SEGMENTS + ":00" : "") + "</td>");
			for (int day = 0; day < 7; day++) {
				Vector daySlots = weekAppointmentModel[day];
				for (int slot = 0; slot < daySlots.size(); slot++) {
					WeekAppointmentModel[] daySlot = (WeekAppointmentModel[]) daySlots.elementAt(slot);
					WeekAppointmentModel cell = daySlot[seg];
					renderWeekAppointment(cell, html);
				}
			}
			html.append("</tr>");
		}
	}

	private void renderWeekAppointment(WeekAppointmentModel cell, StringBuffer html) {
		if (cell == null) {
			html.append("<td></td>");
		} else if (cell.type == WeekAppointmentModel.BEGIN_OF_APPOINTMENT) {
			html.append("<td class=\"appointment\" rowspan=\"" + cell.occupiedSegments + "\">");
			html.append("<b>" + cell.appointmentBegin + "</b><br>");
			html.append("<a href=\"http://" + ACTION_REVEAL_TOPIC + "/" + cell.appointmentID + "\">" +
				cell.appointmentName + "</a>");
			// --- location ---
			BaseTopic location = cell.location;
			if (location != null) {
				html.append("<p><a href=\"http://" + ACTION_REVEAL_TOPIC + "/" + location.getID() + "\">" +
					location.getName() + "</a></p>");
			}
			// --- attendees ---
			Vector attendees = cell.attendees;
			if (attendees.size() > 0) {
				html.append("<p>");
				Enumeration e = attendees.elements();
				while (e.hasMoreElements()) {
					BaseTopic attendee = (BaseTopic) e.nextElement();
					html.append("<a href=\"http://" + ACTION_REVEAL_TOPIC + "/" + attendee.getID() + "\">" +
						attendee.getName() + "</a><br>");
				}
				html.append("</p>");
			}
			//
			html.append("</td>");
		}
	}

	private void renderWeekEvents(Vector weekEventModel, Vector[] weekAppointmentModel, StringBuffer html) {
		for (int slot = 0; slot < weekEventModel.size(); slot++) {
			EventModel[] eventSlot = (EventModel[]) weekEventModel.elementAt(slot);
			html.append("<tr valign=\"top\"><td></td>");
			for (int day = 0; day < 7; day++) {
				EventModel cell = eventSlot[day];
				if (cell == null) {
					int daySlotCount = daySlotCount(day, weekAppointmentModel);
					html.append("<td colspan=\"" + daySlotCount + "\"></td>");
				} else if (cell.type == EventModel.BEGIN_OF_EVENT) {
					int daySlotCount = daySlotCount(day, cell.dayCount, weekAppointmentModel);
					html.append("<td class=\"appointment\" colspan=\"" + daySlotCount + "\">");
					html.append("<a href=\"http://" + ACTION_REVEAL_TOPIC + "/" + cell.eventID + "\">" + cell.eventName + "</a>");
					html.append("</td>");
				}
			}
			html.append("</tr>");
		}
	}

	private void renderMonthEvents(Vector monthEventModel, StringBuffer html) {
		for (int slot = 0; slot < monthEventModel.size(); slot++) {
			EventModel[] eventSlot = (EventModel[]) monthEventModel.elementAt(slot);
			html.append("<tr valign=\"top\">");
			for (int day = 0; day < 7; day++) {
				EventModel cell = eventSlot[day];
				if (cell == null) {
					html.append("<td></td>");
				} else if (cell.type == EventModel.BEGIN_OF_EVENT) {
					html.append("<td class=\"appointment\" colspan=\"" + cell.dayCount + "\">");
					html.append("<a href=\"http://" + ACTION_REVEAL_TOPIC + "/" + cell.eventID + "\">" + cell.eventName + "</a>");
					html.append("</td>");
				}
			}
			html.append("</tr>");
		}
	}

	private String renderMonthView(Vector appointments, Vector events) {
		String displayDateString = getProperty(PROPERTY_DISPLAY_DATE);
		Calendar displayDate = DeepaMehtaUtils.getCalendar(displayDateString);
		// display range: begin date
		displayDate.set(Calendar.DAY_OF_MONTH, 1);
		// display range: end date
		Calendar lastDisplayDate = (Calendar) displayDate.clone();
		lastDisplayDate.set(Calendar.DAY_OF_MONTH, lastDisplayDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		// --- make model ---
		Vector[] monthAppointmentModel = makeMonthAppointmentModel(appointments, displayDate);
		Vector[] monthEventModel = makeMonthEventModel(events, displayDate, lastDisplayDate);
		// --- rendering ---
		StringBuffer html = new StringBuffer("<html><head><link href=\"stylesheets/calendar.css\" rel=\"stylesheet\" " +
											 "type=\"text/css\"></head><body>");
		html.append(renderTimeControls());
		// - heading -
		int month = DeepaMehtaUtils.getMonth(displayDateString);
		int year = DeepaMehtaUtils.getYear(displayDateString);
		html.append("<table><tr><td class=\"year-and-month\" colspan=\"7\">" + monthNamesLong[month - 1] + " " + year +
					"</td></tr><tr valign=\"top\">");
		for (int day = 0; day < 7; day++) {
			html.append("<td class=\"weekday\">" + dayNames[day] + "</td>");
		}
		html.append("</tr>");
		// - body -
		int dayOfWeek = (displayDate.get(Calendar.DAY_OF_WEEK) + 5) % 7;	// weekday of 1st day of month, Mon=0 ... Sun=6
		int weekCount = (lastDisplayDate.get(Calendar.DAY_OF_MONTH) + dayOfWeek + 6) / 7;	// number of weeks to render
		int i = -dayOfWeek;
		for (int week = 0; week < weekCount; week++) {
			html.append("<tr valign=\"top\">");
			for (int day = 0; day < 7; day++) {
				if (i < 0 || i >= monthAppointmentModel.length) {
					html.append("<td class=\"out-of-range\"></td>");
					i++;
					continue;
				}
				Vector dayAppointments = monthAppointmentModel[i];
				html.append("<td" + (dayAppointments != null ? " class=\"appointment\"" : "") + ">");
				html.append("<div class=\"day-of-month\">" + (i + 1) + "</div>");
				html.append("</td>");
				i++;
			}
			html.append("</tr>");
			i -= 7;
			//
			renderMonthEvents(monthEventModel[week], html);
			//
			html.append("<tr valign=\"top\">");
			for (int day = 0; day < 7; day++) {
				if (i < 0 || i >= monthAppointmentModel.length) {
					html.append("<td class=\"out-of-range\"></td>");
					i++;
					continue;
				}
				Vector dayAppointments = monthAppointmentModel[i];
				html.append("<td" + (dayAppointments != null ? " class=\"appointment\"" : "") + ">");
				if (dayAppointments != null) {
					html.append("<ul>");
					Enumeration e = dayAppointments.elements();
					while (e.hasMoreElements()) {
						MonthAppointmentModel dayAppointment = (MonthAppointmentModel) e.nextElement();
						html.append("<li><a href=\"http://" + ACTION_REVEAL_TOPIC + "/" + dayAppointment.appointmentID + "\">" +
							dayAppointment.appointmentName + "</a></li>");
					}
					html.append("</ul>");
				}
				html.append("</td>");
				i++;
			}
			html.append("</tr>");
		}
		html.append("</table></body></html>");
		return html.toString();
	}

	// ---

	private String renderListView(Vector appointments) {
		StringBuffer html = new StringBuffer("<html><body>");
		Enumeration e = appointments.elements();
		while (e.hasMoreElements()) {
			BaseTopic appointment = (BaseTopic) e.nextElement();
			Hashtable props = as.getTopicProperties(appointment);
			String description = getHTMLBodyContent((String) props.get(PROPERTY_DESCRIPTION));
			String beginDate = (String) props.get(PROPERTY_BEGIN_DATE);
			String beginTime = (String) props.get(PROPERTY_BEGIN_TIME);
			String endDate = (String) props.get(PROPERTY_END_DATE);
			String endTime = (String) props.get(PROPERTY_END_TIME);
			html.append("<p>" + timeRange(beginDate, beginTime, endDate, endTime) +
				" <b>" + appointment.getName() + "</b></p>" + description);
		}
		html.append("</body></html>");
		return html.toString();
	}

	// ---

	private String renderTimeControls() {
		String displayMode = getProperty(PROPERTY_DISPLAY_MODE);
		StringBuffer html = new StringBuffer();
		html.append("<a href=\"http://" + ACTION_GO_BACK + "\"><img src=\"images/button-arrow-left.png\" border=\"0\"></a>");
		// ### html.append("<a href=\"http://" + ACTION_SELECT_DAY_MODE + "\"><img src=\"images/button-day" + (displayMode.equals(DISPLAY_MODE_DAY) ? "-activated" : "") + ".png\" border=\"0\"></a>");
		html.append("<a href=\"http://" + ACTION_SELECT_WEEK_MODE + "\"><img src=\"images/button-week" + (displayMode.equals(DISPLAY_MODE_WEEK) ? "-activated" : "") + ".png\" border=\"0\"></a>");
		html.append("<a href=\"http://" + ACTION_SELECT_MONTH_MODE + "\"><img src=\"images/button-month" + (displayMode.equals(DISPLAY_MODE_MONTH) ? "-activated" : "") + ".png\" border=\"0\"></a>");
		html.append("<a href=\"http://" + ACTION_GO_FORWARD + "\"><img src=\"images/button-arrow-right.png\" border=\"0\"></a>");
		return html.toString();
	}

	// ---

	private Vector[] makeWeekAppointmentModel(Vector appointments, Calendar displayDate, Calendar lastDisplayDate) {
		Vector[] weekAppointmentModel = initWeekAppointmentModel();
		//
		String displayDateString = DeepaMehtaUtils.getDate(displayDate);
		String lastDisplayDateString = DeepaMehtaUtils.getDate(lastDisplayDate);
		//
		System.out.println("CalendarTopic.makeWeekAppointmentModel(): displayDate=" + displayDate.getTime() + " (" + displayDateString + ")");
		System.out.println("                                      lastDisplayDate=" + lastDisplayDate.getTime() + " (" + lastDisplayDateString + ")");
		//
		int withinDayRange = 0;			// for diagnostics only
		int appointmentsDisplayed = 0;	// for diagnostics only
		//
		Enumeration e = appointments.elements();
		while (e.hasMoreElements()) {
			BaseTopic appointment = (BaseTopic) e.nextElement();
			String beginDate = getProperty(appointment, PROPERTY_BEGIN_DATE);
			// ignore appointments outside day range
			int c1 = beginDate.compareTo(displayDateString);
			int c2 = beginDate.compareTo(lastDisplayDateString);
			if (c1 < 0 || c2 > 0) {
				continue;
			}
			//
			withinDayRange++;
			String beginTime = getProperty(appointment, PROPERTY_BEGIN_TIME);
			String endTime = getProperty(appointment, PROPERTY_END_TIME);
			// ignore appointments with unset time fields
			if (!isSet(beginTime) || !isSet(endTime)) {
				continue;
			}
			int beginMinuteOfDay = DeepaMehtaUtils.getMinutes(beginTime);
			int endMinuteOfDay = DeepaMehtaUtils.getMinutes(endTime);
			int beginSegment = (beginMinuteOfDay - 60 * CALENDAR_DAY_START_HOUR) / CALENDAR_SEGMENT_SIZE;
			int segmentCount = Math.max((endMinuteOfDay - beginMinuteOfDay) / CALENDAR_SEGMENT_SIZE, 1);
			int toIndex = beginSegment + segmentCount - 1;
			// ignore appointments outside time range
			if (toIndex < 0 || beginSegment >= CALENDAR_DAY_SEGMENTS) {
				continue;
			}
			//
			appointmentsDisplayed++;
			// adjust boundings for appointments partially outside time range
			if (beginSegment < 0) {
				segmentCount += beginSegment;
				beginSegment = 0;
			}
			if (toIndex >= CALENDAR_DAY_SEGMENTS) {
				segmentCount += CALENDAR_DAY_SEGMENTS - toIndex - 1;
			}
			// --- add appointment to model ---
			Calendar appointmentBegin = DeepaMehtaUtils.getCalendar(beginDate);
			int dayOfWeek = (appointmentBegin.get(Calendar.DAY_OF_WEEK) + 5) % 7;	// Mon=0 ... Sun=6
			// find free slot for the day (a column)
			WeekAppointmentModel[] daySlot = findFreeDaySlot(weekAppointmentModel, dayOfWeek, beginSegment, segmentCount);
			// 1) add beginning segment to model
			String appointmentName = appointment.getName();
			AppointmentTopic at = (AppointmentTopic) as.getLiveTopic(appointment);
			BaseTopic location = at.getLocation();
			Vector attendees = at.getAttendees();
			System.out.println("add appointment \"" + appointmentName + "\"");
			daySlot[beginSegment] = new WeekAppointmentModel(appointment.getID(), appointmentName, beginTime, location,
				attendees, segmentCount);
			// 2) add ocupied segments to model
			for (int seg = beginSegment + 1; seg < beginSegment + segmentCount; seg++) {
				daySlot[seg] = new WeekAppointmentModel();
			}
		}
		System.out.println("=> appointments within day range:                  " + withinDayRange);
		System.out.println("=> displayed appointments (reasonable time range): " + appointmentsDisplayed);
		//
		return weekAppointmentModel;
	}

	/**
	 * @return	data model for the events of one week, ready for rendering the week view.
	 */
	private Vector makeWeekEventModel(Vector events, Calendar displayDate, Calendar lastDisplayDate) {
		Vector weekEventModel = new Vector();
		//
		String displayDateString     = DeepaMehtaUtils.getDate(displayDate);
		String lastDisplayDateString = DeepaMehtaUtils.getDate(lastDisplayDate);
		//
		int eventsDisplayed = 0;	// for diagnostics only
		//
		Enumeration e = events.elements();
		while (e.hasMoreElements()) {
			BaseTopic event = (BaseTopic) e.nextElement();
			String beginDate = getProperty(event, PROPERTY_BEGIN_DATE);
			String endDate   = getProperty(event, PROPERTY_END_DATE);
			// ignore events with unset date fields
			if (!isSet(beginDate) || !isSet(endDate)) {
				continue;
			}
			// ignore events completely outside display range
			int c1 = beginDate.compareTo(lastDisplayDateString);
			int c2 = endDate.compareTo(displayDateString);
			if (c1 > 0 || c2 < 0) {
				continue;
			}
			// --- add event to model ---
			eventsDisplayed++;
			// adjust event begin date
			c1 = beginDate.compareTo(displayDateString);
			if (c1 < 0) {
				beginDate = displayDateString;
			}
			// adjust event end date
			c2 = endDate.compareTo(lastDisplayDateString);
			if (c2 > 0) {
				endDate = lastDisplayDateString;
			}
			//
			Calendar eventBegin = DeepaMehtaUtils.getCalendar(beginDate);
			Calendar eventEnd   = DeepaMehtaUtils.getCalendar(endDate);
			int beginDay = (eventBegin.get(Calendar.DAY_OF_WEEK) + 5) % 7;	// Mon=0 ... Sun=6
			int endDay   =   (eventEnd.get(Calendar.DAY_OF_WEEK) + 5) % 7;	// Mon=0 ... Sun=6
			//
			addEventToModel(event, beginDay, endDay, weekEventModel);
		}
		System.out.println("=> displayed events:                               " + eventsDisplayed);
		//
		return weekEventModel;
	}

	private Vector[] initWeekAppointmentModel() {
		Vector[] weekAppointmentModel = new Vector[7];
		// for every day add one slot
		for (int i = 0; i < 7; i++) {
			weekAppointmentModel[i] = new Vector();
			weekAppointmentModel[i].addElement(new WeekAppointmentModel[CALENDAR_DAY_SEGMENTS]);
		}
		return weekAppointmentModel;
	}

	private Vector[] initMonthEventModel(int weekCount) {
		Vector[] monthEventModel = new Vector[weekCount];
		// for every day add one slot
		for (int i = 0; i < weekCount; i++) {
			monthEventModel[i] = new Vector();
		}
		return monthEventModel;
	}

	// ---

	private WeekAppointmentModel[] findFreeDaySlot(Vector[] weekAppointmentModel, int dayOfWeek, int beginSegment, int segmentCount) {
		Vector daySlots = weekAppointmentModel[dayOfWeek];
		Enumeration e = daySlots.elements();
		while (e.hasMoreElements()) {
			WeekAppointmentModel[] daySlot = (WeekAppointmentModel[]) e.nextElement();
			boolean isDaySlotFree = true;
			for (int i = beginSegment; i < beginSegment + segmentCount; i++) {
				if (daySlot[i] != null) {
					isDaySlotFree = false;
					break;
				}
			}
			if (isDaySlotFree) {
				return daySlot;
			}
		}
		// no free day slot found -- create new day slot
		WeekAppointmentModel[] daySlot = new WeekAppointmentModel[CALENDAR_DAY_SEGMENTS];
		daySlots.addElement(daySlot);
		System.out.println("     create a new slot for day " + dayOfWeek + ". Slots now: " + daySlots.size());
		return daySlot;
	}

	private EventModel[] findFreeEventSlot(Vector weekEventModel, int beginDay, int endDay) {
		Enumeration e = weekEventModel.elements();
		while (e.hasMoreElements()) {
			EventModel[] eventSlot = (EventModel[]) e.nextElement();
			boolean isEventSlotFree = true;
			for (int i = beginDay; i <= endDay; i++) {
				if (eventSlot[i] != null) {
					isEventSlotFree = false;
					break;
				}
			}
			if (isEventSlotFree) {
				return eventSlot;
			}
		}
		// no free event slot found -- create new event slot
		EventModel[] eventSlot = new EventModel[7];
		weekEventModel.addElement(eventSlot);
		System.out.println("     create a new event slot. Event slots now: " + weekEventModel.size());
		return eventSlot;
	}

	// ---

	private int daySlotCount(int dayOfWeek, Vector[] weekAppointmentModel) {
		return weekAppointmentModel[dayOfWeek].size();
	}

	private int daySlotCount(int beginDay, int dayCount, Vector[] weekAppointmentModel) {
		int daySlotCount = 0;
		for (int day = beginDay; day < beginDay + dayCount; day++) {
			daySlotCount += daySlotCount(day, weekAppointmentModel);
		}
		return daySlotCount;
	}

	// ---

	private Vector[] makeMonthAppointmentModel(Vector appointments, Calendar displayDate) {
		int daysPerMonth = displayDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		Vector[] monthAppointmentModel = new Vector[daysPerMonth];
		//
		String displayDateString = DeepaMehtaUtils.getDate(displayDate);
		//
		Enumeration e = appointments.elements();
		while (e.hasMoreElements()) {
			BaseTopic appointment = (BaseTopic) e.nextElement();
			String beginDate = getProperty(appointment, PROPERTY_BEGIN_DATE);
			// ignore appointments with unset begin date
			if (!isSet(beginDate)) {
				continue;
			}
			// ignore appointments outside current month
			if (!beginDate.substring(0, 7).equals(displayDateString.substring(0, 7))) {
				continue;
			}
			// --- add to model ---
			int dayOfMonth = DeepaMehtaUtils.getDay(beginDate);
			Vector dayModel = monthAppointmentModel[dayOfMonth - 1];
			if (dayModel == null) {
				dayModel = new Vector();
				monthAppointmentModel[dayOfMonth - 1] = dayModel;
			}
			dayModel.addElement(new MonthAppointmentModel(appointment.getID(), appointment.getName()));		// ### could sort appointments by begin time
		}
		//
		return monthAppointmentModel;
	}

	/**
	 * @param	displayDate		the date to display. Set to 1st day of month.
	 * @param	lastDisplayDate	the date to display. Set to last day of month.
	 */
	private Vector[] makeMonthEventModel(Vector events, Calendar displayDate, Calendar lastDisplayDate) {
		int dayOfWeek = (displayDate.get(Calendar.DAY_OF_WEEK) + 5) % 7;	// weekday of 1st day of month, Mon=0 ... Sun=6
		int weekCount = (lastDisplayDate.get(Calendar.DAY_OF_MONTH) + dayOfWeek + 6) / 7;	// number of weeks to render
		Vector[] monthEventModel = initMonthEventModel(weekCount);
		//
		String displayDateString     = DeepaMehtaUtils.getDate(displayDate);
		String lastDisplayDateString = DeepaMehtaUtils.getDate(lastDisplayDate);
		//
		int eventsDisplayed = 0;	// for diagnostics only
		//
		Enumeration e = events.elements();
		while (e.hasMoreElements()) {
			BaseTopic event = (BaseTopic) e.nextElement();
			String beginDate = getProperty(event, PROPERTY_BEGIN_DATE);
			String endDate   = getProperty(event, PROPERTY_END_DATE);
			// ignore events with unset date fields
			if (!isSet(beginDate) || !isSet(endDate)) {
				continue;
			}
			// ignore events completely outside current month
			int c1 = beginDate.compareTo(lastDisplayDateString);
			int c2 = endDate.compareTo(displayDateString);
			if (c1 > 0 || c2 < 0) {
				continue;
			}
			// --- add event to model ---
			eventsDisplayed++;
			// adjust event begin date
			c1 = beginDate.compareTo(displayDateString);
			if (c1 < 0) {
				beginDate = displayDateString;
			}
			// adjust event end date
			c2 = endDate.compareTo(lastDisplayDateString);
			if (c2 > 0) {
				endDate = lastDisplayDateString;
			}
			//
			Calendar eventBegin = DeepaMehtaUtils.getCalendar(beginDate);
			Calendar eventEnd   = DeepaMehtaUtils.getCalendar(endDate);
			int beginDay = (eventBegin.get(Calendar.DAY_OF_WEEK) + 5) % 7;	// Mon=0 ... Sun=6
			int endDay   =   (eventEnd.get(Calendar.DAY_OF_WEEK) + 5) % 7;	// Mon=0 ... Sun=6
			int beginWeek = (DeepaMehtaUtils.getDay(beginDate) + dayOfWeek - 1) / 7;
			int endWeek   =   (DeepaMehtaUtils.getDay(endDate) + dayOfWeek - 1) / 7;
			System.out.println("following event spans " + (endWeek - beginWeek + 1) + " weeks:");
			for (int week = beginWeek; week <= endWeek; week++) {
				int weekBeginDay = week == beginWeek ? beginDay : 0;
				int weekEndDay   =   week == endWeek ? endDay : 6;
				//
				addEventToModel(event, weekBeginDay, weekEndDay, monthEventModel[week]);
			}
		}
		System.out.println("=> displayed events:                               " + eventsDisplayed);
		//
		return monthEventModel;
	}

	// ---

	private void addEventToModel(BaseTopic event, int beginDay, int endDay, Vector eventModel) {
		// find free event slot (a row)
		EventModel[] eventSlot = findFreeEventSlot(eventModel, beginDay, endDay);
		// 1) add beginning day to model
		String eventName = event.getName();
		int dayCount = endDay - beginDay + 1;
		System.out.println("add event \"" + eventName + "\"");
		eventSlot[beginDay] = new EventModel(event.getID(), eventName, dayCount);
		// 2) add ocupied days to model
		for (int day = beginDay + 1; day < beginDay + dayCount; day++) {
			eventSlot[day] = new EventModel();
		}
	}

	// ---

	private Vector getAppointments() {
		Vector appointments = new Vector();
		// add appointments of the persons connected to this calendar
		Enumeration e = getCalendarPersons().elements();
		while (e.hasMoreElements()) {
			BaseTopic person = (BaseTopic) e.nextElement();
			Vector personAppointments = ((PersonTopic) as.getLiveTopic(person)).getCalendarAppointments();
			Enumeration e2 = personAppointments.elements();
			while (e2.hasMoreElements()) {
				BaseTopic appointment = (BaseTopic) e2.nextElement();
				if (!containsID(appointments, appointment)) {
					appointments.addElement(appointment);
				}
			}
		}
		return appointments;
	}

	private Vector getEvents() {
		return cm.getTopics(TOPICTYPE_EVENT);
	}

	private Vector getCalendarPersons() {
		return cm.getRelatedTopics(getID(), SEMANTIC_CALENDAR_PERSON, TOPICTYPE_PERSON, 2);
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

	private boolean containsID(Vector topics, BaseTopic topic) {
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic t = (BaseTopic) e.nextElement();
			if (t.getID().equals(topic.getID())) {
				return true;
			}
		}
		return false;
	}

	private boolean isSet(String dateOrTime) {
		// ### return !dateOrTime.equals("-/-/-") && !dateOrTime.equals("-:-");
		return dateOrTime.length() > 0 && dateOrTime.indexOf(VALUE_NOT_SET) == -1;
	}



	// *********************
	// *** Inner Classes ***
	// *********************



	/**
	 * Data model for rendering one appointment in the week view.
	 */
	private class WeekAppointmentModel {

		static final int BEGIN_OF_APPOINTMENT = 0;
		static final int OCCUPIED_BY_APPOINTMENT = 1;

		int type;
		String appointmentID, appointmentName, appointmentBegin;
		BaseTopic location;		// may be null
		Vector attendees;		// may be empty
		int occupiedSegments;

		WeekAppointmentModel() {
			type = OCCUPIED_BY_APPOINTMENT;
		}

		WeekAppointmentModel(String appointmentID, String appointmentName, String appointmentBegin,
									BaseTopic location, Vector attendees, int occupiedSegments) {
			type = BEGIN_OF_APPOINTMENT;
			this.appointmentID = appointmentID;
			this.appointmentName = appointmentName;
			this.appointmentBegin = appointmentBegin;
			this.location = location;
			this.attendees = attendees;
			this.occupiedSegments = occupiedSegments;
		}
	}

	/**
	 * Data model for rendering one event in the week view.
	 */
	private class EventModel {

		static final int BEGIN_OF_EVENT = 0;
		static final int OCCUPIED_BY_EVENT = 1;

		int type;
		String eventID, eventName;
		int dayCount;

		EventModel() {
			type = OCCUPIED_BY_EVENT;
		}

		EventModel(String eventID, String eventName, int dayCount) {
			type = BEGIN_OF_EVENT;
			this.eventID = eventID;
			this.eventName = eventName;
			this.dayCount = dayCount;
		}
	}

	/**
	 * Data model for rendering one appointment in the month view.
	 */
	private class MonthAppointmentModel {

		String appointmentID, appointmentName;

		MonthAppointmentModel(String appointmentID, String appointmentName) {
			this.appointmentID = appointmentID;
			this.appointmentName = appointmentName;
		}
	}
}
