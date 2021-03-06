package de.deepamehta;

import java.awt.*;
import java.io.*;
import java.util.*;



/**
 * <P>
 * <HR>
 * Last functional change: 7.3.2005 (2.0b6)<BR>
 * Last documentation update: 28.7.2001 (2.0a11)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class DeepaMehtaUtils implements DeepaMehtaConstants {



	// -----------
	// --- I/O ---
	// -----------



	/**
	 * Returns the contents of the specified file as a <CODE>String</CODE>.
	 * <p>
	 * References checked: 4.2.2005 (2.0b5)
	 *
	 * @see		de.deepamehta.topics.personalweb.FetchThread#getContent
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#transformTopicmap
	 * @see		de.deepamehta.client.TextEditorPanel#importData
	 */
	public static String readFile(File file) throws IOException {
		int len = (int) file.length();
		byte buffer[] = new byte[len];
		FileInputStream in = new FileInputStream(file);
		int num = in.read(buffer);
		in.close();
		return new String(buffer, 0);	// ### deprecated -- use a reader instead
	}

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
		Hashtable topicData = new Hashtable();
		String fieldName;
		String value;
		int fieldCount = in.readInt();
		for (int i = 0; i < fieldCount; i++) {
			fieldName = in.readUTF();
			value = in.readUTF();
			topicData.put(fieldName, value);
		}
		return topicData;
	}

	/**
	 * @see		de.deepamehta.client.InteractionConnection#changeTopicData
	 * @see		de.deepamehta.service.CorporateDirectives#write
	 */
	public static void writeHashtable(Hashtable topicData, DataOutputStream out)
																	throws IOException {
		out.writeInt(topicData.size());
		Enumeration e = topicData.keys();
		String fieldName;
		String value;
		while (e.hasMoreElements()) {
			fieldName = (String) e.nextElement();
			value = (String) topicData.get(fieldName);
			out.writeUTF(fieldName);
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



	// --- parseHexColor (3 forms) ---

	public static Color parseHexColor(String color) {
		try {
			int r = Integer.parseInt(color.substring(1, 3), 16);
			int g = Integer.parseInt(color.substring(3, 5), 16);
			int b = Integer.parseInt(color.substring(5, 7), 16);
			return new Color(r, g, b);
		} catch (RuntimeException e) {
			throw new DeepaMehtaException("invalid color specification: \"" + color +
				"\", expected format is \"#rrggbb\"");
		}
	}

	// ### to be dropped
	public static Color parseHexColor(String color, String defaultColor) {
		return parseHexColor(color, parseHexColor(defaultColor, Color.white));
	}

	// ### to be dropped
	public static Color parseHexColor(String color, Color defaultColor) {
		try {
			if (color.length() == 0) {
				System.out.println(">>> DeepaMehtaUtils.parseHexColor(): no " +
					"color specified -- default color used");
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
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int day = Integer.parseInt(date.substring(8));
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month - 1, day);	// Calendar starts month with 0, DeepaMehta with 1
		return cal.getTime();
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



	// ---------------------------
	// --- String Manipulation ---
	// ---------------------------



	/**
	 * Useful when PropertyData (particularly HTML data)
	 * are written directly to property fields.
	 */
	/* ### public static String encodeSpecialCharacters(String in) {
		int len = in.length();
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < len; i++) {
			char c = in.charAt(i);
			if (c == '<') {
				out.append("&lt;");
			} else if (c == '>') {
				out.append("&gt;");
			} else if (c == '&') {
				out.append("&amp;");
			} else if (c == '\"') {
				out.append("&quot;");
			} else {
				out.append(c);
			}
		}
		return out.toString();
	} */

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

	// ---

	/**
	 * ### to be dropped, use a StringTokenizer or split() instead
	 * <P>
	 * Convenience method to split a string that contains a <CODE>:</CODE> into 2
	 * substrings.
	 * <P>
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

	static public String replaceLF(String text) {
		text = text.replaceAll("\r\n", "<br>");
		text = text.replaceAll("\r", "<br>");		// originates from web
		text = text.replaceAll("\n", "<br>");		// originates from graphical client
		// ### text = DeepaMehtaUtils.replace(text, '\r', "<br>");		// originates from web
		// ### text = DeepaMehtaUtils.replace(text, '\n', "<br>");		// originates from graphical client
		return text;
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

	// --- put (2 forms) ---

	/**
	 * Puts a key-value pair into the Hashtable. If value is null,
	 * the defaultObject will be put instead of value.
	 * If key is null, this method does nothing.
	 *
	 * @see     de.deepamehta.topics.TopicTypeTopic#exportTypeDefinition
	 * @see     de.deepamehta.topics.AssociationTypeTopic#exportTypeDefinition
	 */
	/* ### public static void put(Hashtable hash, String key, Object value, Object defaultObj) {
		if (key != null) {
			Object toPut = value != null ? value : defaultObj;
			if (toPut != null) {
				hash.put(key, toPut);
			}
		}
	} */

	/**
	 * Puts a key-value pair into the Hashtable. If value or key is null,
	 * this method does nothing.
	 *
	 * @see     de.deepamehta.topics.TopicTypeTopic#exportTypeDefinition
	 * @see     de.deepamehta.topics.AssociationTypeTopic#exportTypeDefinition
	 */
	/* ### public static void put(Hashtable hash, String key, Object value) {
		if (key != null && value != null) {
		    hash.put(key, value);
		}
	} */



	// -------------------
	// --- Date & Time ---
	// -------------------



	public static String getDate() {
		return getDate(DATE_SEPARATOR);
	}

	/**
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#createLogfile
	 */
	public static String getDate(String sep) {
		Calendar cal = Calendar.getInstance();
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

/*
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
}
