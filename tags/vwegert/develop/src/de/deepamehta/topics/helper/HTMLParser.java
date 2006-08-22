package de.deepamehta.topics.helper;



/**
 * ### should be named SimpleTextParser
 * <P>
 * <HR>
 * Last sourcecode change: 4.8.2002 (2.0a15-pre11)<BR>
 * Last documentation update: 4.8.2002 (2.0a15-pre11)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class HTMLParser {

	/**
	 * parsed text
	 */
	String html;

	/**
	 * parameter text in lower cases
	 */	
	String htmlLowerCase;

	/**
	 * current position of the parser
	 */	
	int htmlPos;

	/**
	 * length of the parsed text
	 */	
	int htmlLength;

	/**
	 * position of last successful search
	 */	
	int foundPos;



	// ******************
	// *** Contructor ***
	// ******************



	/**
	 * @param	htmlInput	text to parse
	 */
	public HTMLParser(String htmlInput) {
		// replace EOLNs and tabulators with spaces
		html = htmlInput.replace('\n',' ').replace('\r',' ').replace('\t',' ');
		htmlLowerCase = html.toLowerCase();
		htmlPos = 0;
		htmlLength = html.length();
	}



	// *********************
	// *** Parse Methods ***
	// *********************



	/**
	 * Reads until the text is found.
	 * <P>
	 * If the text is found, the internal parser position is set just behind it
	 *
	 * @param	what searched text
	 * @param	caseSensitive case sensitive search
	 *
	 * @return	text before the found text or empty string if nothing was found
	 */
	public String readUntil(String what, boolean caseSensitive) {
		if (what.equals("")) {
			return "";
		}	
		if (htmlPos >= htmlLength) {
			return "";
		}
		String str;
		String buffer;
		if (caseSensitive) {
			str = what;
			buffer = html;
		} else {
			str = what.toLowerCase();
			buffer = htmlLowerCase;
		}
		int strPos = buffer.indexOf(str, htmlPos);
		if (strPos == -1) {
			return "";
		}
		String result = html.substring(htmlPos, strPos);
		htmlPos = strPos + str.length();
		return result;
	}
	
	/**
	 * restarts parsing by setting internal parser position to 0
	 */	
	public void restart() {
		htmlPos = 0;
	}

	// --- textRange (2 forms) ---
	
	public String textRange(String start, String end) {
		return textRange(start, end, false, false);
	}

	/**
	 * Looks for text delimited by two other pieces of text.
	 * <P>
	 * If the text is found, the internal parser position is set just behind the second delimiter.
	 * <P>
	 * References checked: 4.8.2002 (2.0a15-pre11)
	 *
	 * @param	start			first delimiter
	 * @param	end				second delimiter
	 * @param	caseSensitive	case sensitive search
	 *
	 * @return	delimited text or empty string if nothing was found
	 *
	 * @see		#textRangeExists
	 * @see		de.deepamehta.topics.WebpageTopic#testRedirections
	 * @see		de.deepamehta.topics.WebpageTopic#getWebpageTitle
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#setWebpageTopicName
	 */	
	public String textRange(String start, String end, boolean returnDelimiter, boolean caseSensitive) {
		if (htmlPos >= htmlLength) {
			return "";
		}	
		String startString;
		String endString;
		String buffer;
		// realize case sensitivity
		if (caseSensitive) {
			startString = start;
			endString = end;
			buffer = html;
		} else {
			startString = start.toLowerCase();
			endString = end.toLowerCase();
			buffer = htmlLowerCase;
		}
		// search start
		int posStart1 = buffer.indexOf(startString, htmlPos);
		if (posStart1 == -1) {
			return "";
		}
		int startLength = startString.length();
		int posStart2 = posStart1 + startLength;
		if (posStart2 == htmlLength) {
			return "";		
		}
		// search end
		int posEnd1 = buffer.indexOf(endString, posStart2);
		if (posEnd1 == -1) {
			return "";
		}
		int endLength = endString.length();
		int posEnd2 = posEnd1 + endLength;
		//
		htmlPos = posEnd2;
		foundPos = posStart1;
		//
		if (returnDelimiter) {
			return html.substring(posStart1, posEnd2);
		} else {
			return html.substring(posStart2, posEnd1);
		}
	}

	// ---

	/**
	 * Looks whether there is a text delimited by two other pieces of text.
	 * <P>
	 * If the text is found, the internal parser position is set just behind
	 * the second delimiter
	 *
	 * @param	start			first delimiter
	 * @param	end				second delimiter
	 *
	 * @return	true if the text is found (it can be empty), otherwise false
	 */	
	public boolean textRangeExists(String start, String end) {
		return !textRange(start, end).equals("");
	}	

	/**
	 * this text moves internal parser pointer skiping  everything till specified text
	 * (including it)
	 *
	 * @param what text to which it should skip
	 * @param caseSensitive case sensitive search
	 * @param skipRestIfNotFound if true and the text is not found, then internal 
	 * 			parser pointer moves to the end, otherwise it will not change
	 */
	public void skip(String what, boolean caseSensitive, boolean skipRestIfNotFound) {
		if (what.equals("")) {
			return;
		}
		if (htmlPos >= htmlLength) {
			return;
		}
		String str;
		String buffer;
		if (caseSensitive) {
			str = what;
			buffer = html;
		} else {
			str = what.toLowerCase();
			buffer = htmlLowerCase;
		}
		int strPos = buffer.indexOf(str, htmlPos);
		if (strPos == -1) {
			if (skipRestIfNotFound) {
				htmlPos = htmlLength;
			}
			return;
		}
		int strLen = str.length();
		htmlPos = strPos + strLen;
	}
	
	/**
	 * Cuts the last part of the parsed text.
	 *
	 * @param	endString from which text to cut
	 * @param	caseSensitive case sensitive search
	 */	
	public void cutTheText(String endString, boolean caseSensitive) {
		if (endString.equals("")) {
			return;
		}
		if (htmlPos >= htmlLength) {
			return;
		}
		String str;
		String buffer;
		if (caseSensitive) {
			str = endString;
			buffer = html;
		} else {
			str = endString.toLowerCase();
			buffer = htmlLowerCase;
		}
		int strPos = buffer.indexOf(str, htmlPos);
		if (strPos == -1) {
			return;
		}
		html = html.substring(0, strPos);
		htmlLowerCase = htmlLowerCase.substring(0, strPos);
		htmlLength = strPos + 1;
	}

	// ---

	public int getFoundPos() {
		return foundPos;
	}
}
