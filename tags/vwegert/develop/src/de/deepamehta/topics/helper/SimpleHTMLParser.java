package de.deepamehta.topics.helper;



/**
 * A simple HTML parser.
 * <P>
 * ### to be dropped
 */
public class SimpleHTMLParser {

	private int pos;		// position of attribute, or -1 if attribute was not found
	private String attr;
	private String link;
	private String anchor;
	private int len;
	
	public  SimpleHTMLParser(String html, int start, String attribute) {
		String upperHTML = html.toUpperCase();
		boolean retry;
		do {
			retry = false;
			pos = upperHTML.indexOf(attribute.toUpperCase(), start);
			if (pos != -1) {
				int start1 = pos + attribute.length();
				// skip whitespace
				char c = html.charAt(start1);
				while (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
					start1++;
					c = html.charAt(start1);
				}
				//
				if (c == '=') {
					start1++;
					// search begin of attribute value (start1)
					c = html.charAt(start1);
					while (c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '"' || c == '\'') {
						start1++;
						c = html.charAt(start1);
					}
					// search end of attribute value (start2)
					int start2 = start1;
					int anchorPos = -1;
					c = html.charAt(start2);
					while (c != ' ' && c != '>' && c != '"' && c != '\'') {
						if (c == '#') {
							anchorPos = start2;
						}
						start2++;
						c = html.charAt(start2);
					}
					// initialize members
					attr = html.substring(pos, start1);
					if (anchorPos != -1) {
						link = html.substring(start1, anchorPos);
						anchor = html.substring(anchorPos, start2);
					} else {
						link = html.substring(start1, start2);
						anchor = "";
					}
					len = start2 - pos;
				} else {
					// attribute name found, but "=" is missing
					retry = true;	// keep searching
					start = start1;
				}
			}
		} while (retry);
	}
	
	public int getPosition() {
		return pos;
	}

	public String getAttribute() {
		return attr;
	}

	public String getLink() {
		return link;
	}

	public String getAnchor() {
		return anchor;
	}

	public int getLength() {
		return len;
	}
}
