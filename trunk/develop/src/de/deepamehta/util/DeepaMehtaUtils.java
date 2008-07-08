package de.deepamehta.util;

import de.deepamehta.Association;
import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.BaseTopicMap;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableTopicMap;
import de.deepamehta.Topic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * <p>
 * <hr>
 * Last change: 4.7.2008 (2.0b8)<br>
 * J&ouml;rg Richter / Malte Rei&szlig;ig<br>
 * jri@deepamehta.de / mre@deepamehta.de
 */
public class DeepaMehtaUtils implements DeepaMehtaConstants {



	// -----------
	// --- I/O ---
	// -----------



	/**
	 * Returns the contents of the specified file as a <code>String</code>.
	 * <p>
	 * References checked: 4.2.2005 (2.0b5)
	 *
	 * @see		de.deepamehta.topics.personalweb.FetchThread#getContent
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#transformTopicmap
	 * @see		de.deepamehta.client.TextEditorPanel#importData
	 */
	public static String readFile(File file) throws IOException {
		int len = (int) file.length();
		char buffer[] = new char[len];
		FileReader in = new FileReader(file);
		in.read(buffer);
		in.close();
		return new String(buffer);
	}

	/* ### public static void copyFile(File srcFile, File dstFile) throws IOException {
		byte buffer[] = new byte[(int) srcFile.length()];
		FileInputStream in = new FileInputStream(srcFile);
		FileOutputStream out = new FileOutputStream(dstFile);
		in.read(buffer);
		out.write(buffer);
		in.close();
		out.close();
	} */

	// ---

	public static Vector readStrings(DataInputStream in) throws IOException {
		Vector strings = new Vector();
		// read number of strings
		int stringCount = in.readInt();
		// read strings
		for (int i = 0; i < stringCount; i++) {
			strings.addElement(in.readUTF());
		}
		return strings;
	}

	public static void writeStrings(Vector strings, DataOutputStream out) throws IOException {
		// write number of strings
		out.writeInt(strings.size());
		// write strings
		for (int i = 0; i < strings.size(); i++) {
			out.writeUTF((String) strings.elementAt(i));
		}
	}

	// ---

	public static Hashtable readHashtable(DataInputStream in) throws IOException {
		Hashtable hashtable = new Hashtable();
		int entryCount = in.readInt();
		for (int i = 0; i < entryCount; i++) {
			String key = in.readUTF();
			String value = in.readUTF();
			hashtable.put(key, value);
		}
		return hashtable;
	}

	/**
	 * Serializes a hashtable and writes it to an output stream for reconstructing it via {@link #readHashtable}.
	 * <p>
	 * Works only for hashtables where both, keys and values are strings.
	 *
	 * @see		de.deepamehta.client.InteractionConnection#changeTopicData
	 * @see		de.deepamehta.service.CorporateDirectives#write
	 */
	public static void writeHashtable(Hashtable hashtable, DataOutputStream out) throws IOException {
		// ### System.out.println(">>> DeepaMehtaUtils.writeHashtable(): " + hashtable.size() + " entries");
		out.writeInt(hashtable.size());
		Enumeration e = hashtable.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String) hashtable.get(key);
			// ### System.out.println("        key=\"" + key + "\" value=\"" + value + "\"");
			out.writeUTF(key);
			out.writeUTF(value);
		}
	}

	// ---

	/**
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}
	 *
	 * @see		PresentationPropertyDefinition#PresentationPropertyDefinition
	 */
	public static Vector readTopics(DataInputStream in) throws IOException {
		Vector topics = new Vector();
		// read number of topics
		int topicCount = in.readInt();
		// read topics
		for (int i = 0; i < topicCount; i++) {
			topics.addElement(new PresentableTopic(in));
		}
		return topics;
	}

	/**
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}
	 *
	 * @see		PresentationPropertyDefinition#PresentationPropertyDefinition
	 */
	public static Vector readAssociations(DataInputStream in) throws IOException {
		Vector assocs = new Vector();
		// read number of associations
		int assocCount = in.readInt();
		// read associations
		for (int i = 0; i < assocCount; i++) {
			assocs.addElement(new PresentableAssociation(in));
		}
		return assocs;
	}

	// ---

	/**
	 * @see		PresentableTopicMap#write	2x
	 * @see		de.deepamehta.service.CorporateDirectives#write
	 */
	public static void writeTopics(Vector topics, DataOutputStream out)
																	throws IOException {
		// write number of topics
		out.writeInt(topics.size());
		// write topics
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			((BaseTopic) e.nextElement()).write(out);
		}
	}

	/**
	 * @see		BaseTopicMap#write
	 */
	public static void writeTopics(Hashtable topics, DataOutputStream out)
																	throws IOException {
		// write number of topics
		out.writeInt(topics.size());
		// write topics
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			((BaseTopic) e.nextElement()).write(out);
		}
	}

	// ---

	/**
	 * @see		de.deepamehta.service.CorporateDirectives#write
	 */
	public static void writeAssociations(Vector associations, DataOutputStream out)
																	throws IOException {
		// write number of associations
		out.writeInt(associations.size());
		// write associations
		Enumeration e = associations.elements();
		while (e.hasMoreElements()) {
			((BaseAssociation) e.nextElement()).write(out);
		}
	}

	/**
	 * @see		BaseTopicMap#write
	 */
	public static void writeAssociations(Hashtable associations, DataOutputStream out)
																	throws IOException {
		// write number of associations
		out.writeInt(associations.size());
		// write associations
		Enumeration e = associations.elements();
		while (e.hasMoreElements()) {
			((BaseAssociation) e.nextElement()).write(out);
		}
	}



	// --------------------------
	// --- Parsing Parameters ---
	// --------------------------



	// --- parseHexColor (4 forms) ---

	public static Color parseHexColor(String color) {
		return parseHexColor(color, 255);
	}

	public static Color parseHexColor(String color, int alpha) {
		try {
			int r = Integer.parseInt(color.substring(1, 3), 16);
			int g = Integer.parseInt(color.substring(3, 5), 16);
			int b = Integer.parseInt(color.substring(5, 7), 16);
			return new Color(r, g, b, alpha);
		} catch (RuntimeException e) {
			throw new DeepaMehtaException("invalid color specification: \"" + color +
				"\", expected format is \"#rrggbb\"");
		}
	}

	public static Color parseHexColor(String color, String defaultColor) {
		return parseHexColor(color, parseHexColor(defaultColor, Color.white));
	}

	/**
	 * @param	color	a string of format #rrggbb, may be <code>null</code> or empty.
	 */
	public static Color parseHexColor(String color, Color defaultColor) {
		try {
			if (color == null || color.length() == 0) {
				return defaultColor;
			}
			//
			int r = Integer.parseInt(color.substring(1, 3), 16);
			int g = Integer.parseInt(color.substring(3, 5), 16);
			int b = Integer.parseInt(color.substring(5, 7), 16);
			//
			return new Color(r, g, b);
		} catch (RuntimeException e) {
			System.out.println("*** DeepaMehtaUtils.parseHexColor(): invalid color specification: \"" + color +
				"\" (expected format is #rrggbb) -- default color used");
			return defaultColor;
		}
	}

	// ---

	public static Date parseDate(String date) {
		return getCalendar(date).getTime();
	}

	public static Point parsePoint(String point) {
		StringTokenizer st = new StringTokenizer(point, ":");
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		return new Point(x, y);
	}

	// ---

	public static boolean isImage(String file) {
		return (file.endsWith(".gif") || file.endsWith(".GIF") ||
				file.endsWith(".jpg") || file.endsWith(".JPG") ||
				file.endsWith(".png") || file.endsWith(".PNG"));
	}

	public static boolean isHTML(String file) {
		return (file.endsWith(".html") || file.endsWith(".HTML") ||
				file.endsWith(".htm") || file.endsWith(".HTM"));
	}



	// ----------------
	// --- Graphics ---
	// ----------------



	public static Rectangle getBounds(PresentableTopicMap topicmap) {
		return initBounds(topicmap, null);
	}

	public static Rectangle initBounds(PresentableTopicMap topicmap, Rectangle returnObject) {
		int xMin = Integer.MAX_VALUE;
		int yMin = Integer.MAX_VALUE;
		int xMax = Integer.MIN_VALUE;
		int yMax = Integer.MIN_VALUE;
		//
		Enumeration e = topicmap.getTopics().elements();
		while (e.hasMoreElements()) {
			PresentableTopic topic = (PresentableTopic) e.nextElement();
			Point p = topic.getGeometry();
			if (p.x < xMin) xMin = p.x;
			if (p.x > xMax) xMax = p.x;
			if (p.y < yMin) yMin = p.y;
			if (p.y > yMax) yMax = p.y;
		}
		//
		if (returnObject == null) {
			returnObject = new Rectangle();
		}
		returnObject.x = xMin;
		returnObject.y = yMin;
		returnObject.width = xMax - xMin;
		returnObject.height = yMax - yMin;
		//
		return returnObject;
	}

	// ---

	/**
	 * References checked: 4.9.2001 (2.0a12-pre1)
	 *
	 * @see		de.deepamehta.client.GraphPanel#paint	(the edge in progress)
	 * @see		de.deepamehta.client.GraphPanel#paintEdge
	 */
	public static void paintLine(Graphics g, int x1, int y1, int x2, int y2,
															   boolean hasDirection) {
		if (hasDirection) {
			g.drawLine(x1, y1 - 1, x2, y2);
			g.drawLine(x1 + 1, y1 - 1, x2, y2);
			g.drawLine(x1 + 2, y1, x2, y2);
			g.drawLine(x1 + 2, y1 + 1, x2, y2);
			g.drawLine(x1 + 1, y1 + 2, x2, y2);
			g.drawLine(x1, y1 + 2, x2, y2);
			g.drawLine(x1 - 1, y1 + 1, x2, y2);
			g.drawLine(x1 - 1, y1, x2, y2);
		} else {
			g.drawLine(x1, y1, x2, y2);
			g.drawLine(x1 + 1, y1, x2 + 1, y2);
			g.drawLine(x1, y1 + 1, x2, y2 + 1);
			g.drawLine(x1 + 1, y1 + 1, x2 + 1, y2 + 1);
		}
	}



	// ----------------------
	// --- HTML Utilities ---
	// ----------------------



	/**
	 * Encodes certain characters into HTML entities.
	 * For the time being only one encoding is performed: " -> &quot;
	 * <p>
	 * Useful when text appears in HTML <input> element.
	 */
	public static String encodeHTMLEntities(String text) {
		return replace(text, '"', "&quot;");
		/* ###
			if (c == '<') {
				out.append("&lt;");
			} else if (c == '>') {
				out.append("&gt;");
			} else if (c == '&') {
				out.append("&amp;");
			} else if (c == '\"') {
				out.append("&quot;");
		*/
	}

	/**
	 * decodes one special character from HTML text
	 *
	 * @param specChar special character
	 *
	 * @return decoded character
	 *
	 * @see		#restoreSpecialCharacters
	 */
	/* ### public static String decodeSpecialCharacter(String specChar) {
		if (specChar.equals("&gt;")) {
			return ">";
		}
		if (specChar.equals("&lt;")) {
			return "<";
		}
		if (specChar.equals("&quot;")) {
			return "\"";
		}
		if (specChar.equals("&amp;")) {
			return "&";
		}
		return specChar;
	} */

	// --- encodeHTMLTags (2 forms) ---

	static public String encodeHTMLTags(String text) {
		return encodeHTMLTags(text, false);
	}

	static public String encodeHTMLTags(String text, boolean allowSomeTags) {
		StringBuffer quotedText = new StringBuffer(text);
		int i = 0;
		while ((i = quotedText.indexOf("<", i)) != -1) {
			boolean quote;
			// check weather to quote
			if (allowSomeTags) {
				int wordStart = i + 1 < quotedText.length() && quotedText.charAt(i + 1) == '/' ? i + 2 : i + 1;
				String tag = getWord(quotedText, wordStart);
				quote = !ALLOWED_TAGS.contains(tag);
			} else {
				quote = true;
			}
			// perform quoting
			if (quote) {
				quotedText.replace(i, i + 1, "&lt;");
			}
			//
			i ++;
		}
		return quotedText.toString();
	}

	static private String getWord(StringBuffer str, int fromIndex) {
		int i = fromIndex;
		while (i < str.length() && Character.isLetter(str.charAt(i))) {
			i++;
		}
		return str.substring(fromIndex, i);
	}

	// ---

	static public String emailToHTML(String text) {
		String emailRegex = "[\\w\\d-_/\\.]*@+[\\w\\d-_/\\.]*";
	    Pattern pattern = Pattern.compile(emailRegex);
	    Matcher matcher = pattern.matcher(text);
	    int lastIndex = 0;
	    StringBuffer html = new StringBuffer();
	    while (matcher.find()) {
	    	//
	    	html.append(text.substring(lastIndex, matcher.start()));
	    	int before = matcher.start()-13;
	    	if (before >= 0 && "href=\"mailto:".equals(text.substring(before, matcher.start()))) {
	    		html.append(matcher.group());
	    	} else {
	    		html.append("<a href=\"mailto:" + matcher.group() + "\">" + matcher.group() + "</a>");
	    	}	    	
	    	lastIndex = matcher.end();
	    }
	    html.append(text.substring(lastIndex));
	    return html.toString();
		
	}
	
	static public String weblinksToHTML(String text) {
		// not conform to rfc uri; regex not for java found under http://www.faqs.org/rfcs/rfc2396.html, sufficient until now
		String test = "http://[\\w\\d-_/\\.\\?\\%\\@\\$\\;\\&\\=\\\"\\#]*";
		Pattern pattern = Pattern.compile(test);
	    Matcher matcher = pattern.matcher(text);
	    int lastIndex = 0;
	    StringBuffer html = new StringBuffer();
	    while (matcher.find()) {
	    	//
	    	html.append(text.substring(lastIndex, matcher.start()));
	    	int before = matcher.start()-6;
	    	if (before >= 0 && "href=\"".equals(text.substring(before, matcher.start()))) {
	    		html.append(matcher.group());
	    	} else {
	    		html.append("<a href=\"" + matcher.group() + "\" target=\"blank\">" + matcher.group() + "</a>");
	    	}	    	
	    	lastIndex = matcher.end();
	    }
	    html.append(text.substring(lastIndex));
	    return html.toString();
	}

	// ---

	static public String replaceLF(String text) {
		text = text.replaceAll("\r\n", "<br>");
		text = text.replaceAll("\r", "<br>");		// originates from web
		text = text.replaceAll("\n", "<br>");		// originates from graphical client
		// ### text = DeepaMehtaUtils.replace(text, '\r', "<br>");		// originates from web
		// ### text = DeepaMehtaUtils.replace(text, '\n', "<br>");		// originates from graphical client
		return text;
	}

	// ---

	/**
	 * Transforms plain text for being displayed in an HTML page.
	 * Four transformations are performed:
	 * <ul>
	 * <li>### Angle brackets into &amp;lt; entity</li>
	 * <li>Line breaks into &lt;br> tag</li>
	 * <li>Email addresses (containing an <code>@</code>) into &lt;a> tag (<code>mailto:</code>)</li>
	 * <li>Web addresses (beginning with <code>http:</code>) into &lt;a> tag</li>
	 * </ul>
	 */
	static public String transformToHTML(String text) {
		// ### text = encodeHTMLTags(text);		// ### bug #14137 is open again, but we must comment this line
												// ### for Kiezatlas "Administrator Infos" property
		text = emailToHTML(text);
		text = weblinksToHTML(text);
		text = replaceLF(text);
		return text;
	}	

	// ---

	/**
	 * @see		de.deepamehta.service.web.DeepaMehtaServlet#addObject
	 */
	static public String html2xml(String html) {
		// ### too simple
		StringBuffer xml = new StringBuffer();
		// close image tags
		int pos = 0;
		int pos1 = html.indexOf("<img");
		while (pos1 != -1) {
			int pos2 = html.indexOf(">", pos1);
			xml.append(html.substring(pos, pos2 + 1));
			xml.append("</img>");
			pos = pos2 + 1;
			pos1 = html.indexOf("<img", pos);
		}
		xml.append(html.substring(pos));
		// close br tags
		html = xml.toString();
		xml.setLength(0);
		//
		pos = 0;
		pos1 = html.indexOf("<br>");
		while (pos1 != -1) {
			xml.append(html.substring(pos, pos1 + 4));
			xml.append("</br>");
			pos = pos1 + 4;
			pos1 = html.indexOf("<br>", pos);
		}
		xml.append(html.substring(pos));
		//
		return xml.toString();
	}



	// ---------------------------
	// --- String Manipulation ---
	// ---------------------------



	/**
	 * ### to be dropped, use a StringTokenizer or split() instead
	 * <p>
	 * Convenience method to split a string that contains a <code>:</code> into 2
	 * substrings.
	 * <p>
	 * Used here to split a refresh request into topicmap ID and viewmode.
	 */
	static public String[] explode(String str) {
		String[] result = new String[2];
		int pos = str.indexOf(":");
		if (pos != -1) {
			result[0] = str.substring(0, pos);
			result[1] = str.substring(pos + 1);
		} else {
			result[0] = str;
			// result[1] = null;
		}
		return result;
	}

	static public String replace(String str, char oldChar, String newStr) {
		// ### return str.replaceAll(""+oldChar, newStr);
		// ###
		// ### replaceAll() works with regexps and ist NOT compatible with the following regexp-less solution
		// ### The difference regards Quoting: a backslash in a Java string must be quoted: \\
		// ### BUT within regexps each backslash must be quoted again: \\\\
		int pos = str.indexOf(oldChar);
		if (pos == -1) {
			return str;
		}
		//
		StringBuffer result = new StringBuffer();
		int pos0 = 0;
		while (pos != -1) {
			result.append(str.substring(pos0, pos));
			result.append(newStr);
			pos0 = pos + 1;
			pos = str.indexOf(oldChar, pos0);
		}
		result.append(str.substring(pos0));
		return result.toString();
	}

	// ---

	static public String align(String str) {
		return (str.length() == 1 ? "0" : "") + str;
	}

	static public String unalign(String str) {
		return (str.startsWith("0") ? str.substring(1) : str);
	}

	// ---

	static public String nTimes(String str, int n) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < n; i++) {
			result.append(str);
		}
		return result.toString();
	}



	// ---
	// ---
	// ---



	/**
	 * References checked: 27.12.2001 (2.0a14-pre5)
	 *
	 * @see		BaseTopicMap#BaseTopicMap(Vector topics, Vector associations)
	 * @see		PresentableTopicMap#PresentableTopicMap(DataInputStream)
	 * @see		de.deepamehta.client.PresentationTopicMap#init
	 */
	public static Hashtable fromTopicVector(Vector vector) {
		Hashtable topics = new Hashtable();
		Enumeration e = vector.elements();
		Topic topic;
		while (e.hasMoreElements()) {
			topic = (Topic) e.nextElement();
			topics.put(topic.getID(), topic);
		}
		return topics;
	}

	/**
	 * References checked: 27.12.2001 (2.0a14-pre5)
	 *
	 * @see		BaseTopicMap#BaseTopicMap(Vector topics, Vector associations)
	 * @see		PresentableTopicMap#PresentableTopicMap(DataInputStream)
	 * @see		de.deepamehta.client.PresentationTopicMap#init
	 */
	public static Hashtable fromAssociationVector(Vector vector) {
		Hashtable associations = new Hashtable();
		Enumeration e = vector.elements();
		Association assoc;
		while (e.hasMoreElements()) {
			assoc = (Association) e.nextElement();
			associations.put(assoc.getID(), assoc);
		}
		return associations;
	}

	// ---

	public static Vector topicIDs(Vector topics) {
		Vector topicIDs = new Vector();
		//
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			topicIDs.addElement(topic.getID());
		}
		//
		return topicIDs;
	}



	// -------------------
	// --- Date & Time ---
	// -------------------



	public static Calendar getCalendar(String date) {
		int year = getYear(date);
		int month = getMonth(date);
		int day = getDay(date);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		// ### cal.setFirstDayOfWeek(Calendar.MONDAY);	// ### european
		cal.set(year, month - 1, day);	// Calendar starts month with 0, DeepaMehta with 1
		return cal;
	}

	// --- getDate(4 forms) ---

	public static String getDate() {
		return getDate(DATE_SEPARATOR);
	}

	public static String getDate(Calendar cal) {
		return getDate(cal, DATE_SEPARATOR);
	}

	/**
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#createLogfile
	 */
	public static String getDate(String sep) {
		Calendar cal = Calendar.getInstance();
		return getDate(cal, sep);
	}

	public static String getDate(Calendar cal, String sep) {
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;	// Note: DeepaMehta begins with month=1
		int d = cal.get(Calendar.DAY_OF_MONTH);
		String date = y + sep + (m < 10 ? "0" : "") + m + sep + (d < 10 ? "0" : "") + d;	// ### consider align()
		return date;
	}

	// --- getTime (3 forms) ---

	public static String getTime() {
		return getTime(true);
	}

	/**
	 * @see		de.deepamehta.client.MessagePanel#addMessage
	 */
	public static String getTime(boolean withSecs) {
		return getTime(withSecs, TIME_SEPARATOR);
	}

	/**
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#createLogfile
	 */
	public static String getTime(boolean withSecs, String sep) {
		Calendar cal = Calendar.getInstance();
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		String time = (h < 10 ? "0" : "") + h + sep + (m < 10 ? "0" : "") + m;
		if (withSecs) {
			time += sep + (s < 10 ? "0" : "") + s;
		}
		// ### consider align()
		return time;
	}

	// ---

	/**
	 * @param	time	a time string in DeepaMehta format (hh:mm)
	 */
	public static int getMinutes(String time) {
		int hours = Integer.parseInt(time.substring(0, 2));
		int minutes = Integer.parseInt(time.substring(3, 5));
		return 60 * hours + minutes;
	}

	// ---

	/**
	 * @param	date	a date string in DeepaMehta format (yyyy/mm/dd)
	 */
	public static int getDay(String date) {
		int day = Integer.parseInt(date.substring(8));
		return day;
	}

	public static int getMonth(String date) {
		int month = Integer.parseInt(date.substring(5, 7));
		return month;
	}

	public static int getYear(String date) {
		int year = Integer.parseInt(date.substring(0, 4));
		return year;
	}



	// -----------------
	// --- Reporting ---
	// -----------------



	/**
	 * @see		#initialize
	 */
	public static void reportVMProperties() {
		try {
			String prop = System.getProperty("java.specification.vendor");
			// Note: first get the property to prevent output in case of exception
			System.out.println("> Java Runtime Environment");
			System.out.println(">    specification: " + prop + "/" +
				System.getProperty("java.specification.name") + "/" +
				System.getProperty("java.specification.version"));
			System.out.println(">    VM specification: " +
				System.getProperty("java.vm.specification.vendor") + "/" +
				System.getProperty("java.vm.specification.name") + "/" +
				System.getProperty("java.vm.specification.version"));
			System.out.println(">    VM implementation: " +
				System.getProperty("java.vm.vendor") + "/" +
				System.getProperty("java.vm.name") + "/" +
				System.getProperty("java.vm.version"));
			System.out.println(">    File encoding: " + System.getProperty("file.encoding"));
		} catch (Exception e) {
			System.out.println("*** The VM properties can't be reported because this applet is not signed");
		}
	}

	public static void memoryStatus() {
		long memFree = Runtime.getRuntime().freeMemory();
		long memTotal = Runtime.getRuntime().totalMemory();
		System.out.println("> " + mem(memTotal - memFree) + " used (" +
			mem(memTotal) + " allocated)");
	}

	public static String mem(long bytes) {
		if (bytes < 1024) {
			return Long.toString(bytes);
		}
		long kBytes = bytes / 1024;
		if (kBytes < 1000) {
			return kBytes + "K";
		}
		long mBytes = kBytes / 1000;
		return mBytes + "M";
	}

/* ###
	public static boolean isCompatible(String version, String requiredVersion) {
		if (version.equals(requiredVersion)) {
			return true;
		}
		//
		String release;
		String interim;
		String requiredRelease;
		String requiredInterim;
		//
		int pos = version.indexOf(version, '-');
		if (pos != -1) {
			release = version.substring(0, pos);
			interim = version.substring(pos + 1);
		} else {
			release = version;
		}
		//
		pos = version.indexOf(requiredVersion, '-');
		if (pos != -1) {
			requiredRelease = requiredVersion.substring(0, pos);
			requiredInterim = requiredVersion.substring(pos + 1);
		} else {
			requiredRelease = requiredVersion;
		}
		//
		if (interim != null) {
			if (requiredInterim != null) {
			} else {
			}
		} else {
			if (requiredInterim != null) {
			} else {
			}
		}
	}
*/



	// ------------
	// --- Misc ---
	// ------------



	// ###
	static public void printStackTrace() {
		printStackTrace(10, 1);
	}

	// ###
	static public void printStackTrace(int depth) {
		printStackTrace(depth, 1);
	}

	// ###
	static public void printStackTrace(int depth, int skip) {
		if (false) {
			skip++;
			System.err.println("----");
			Exception e = new Exception();
			e.fillInStackTrace();
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (int i = skip; i < stackTrace.length && i < depth + skip; i++) {
				System.err.println("    " + stackTrace[i].toString());
			}
			System.err.println("----");
		}
	}
}
