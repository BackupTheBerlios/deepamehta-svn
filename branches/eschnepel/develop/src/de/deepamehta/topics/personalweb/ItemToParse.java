package de.deepamehta.topics.personalweb;

import java.net.URL;



/**
 * <CODE>ItemToParse</CODE> objects are stored in the parse-queue.
 * An <CODE>ItemToParse</CODE> consists of 5 values:
 * <UL>
 * <LI>URL this item originally was retrieved from.
 * <LI>The string is the HTML to parse.
 * <LI>The boolean is true, if the HTML was loaded from a local file. In this case
 *     the HTML still needs to be parsed but neither converted nor saved.
 * <LI>...
 * </UL>
 */
class ItemToParse {

	URL		url;
	String	html;
	boolean	local;
	String	webpageID;	// DeepaMehta specific
	String	websiteID;	// DeepaMehta specific

	/**
	 * @see		FetchThread#run
	 */
	ItemToParse(URL url, String html, boolean local, String	webpageID, String websiteID) {
		this.url = url;
		this.html = html;
		this.local = local;
		this.webpageID = webpageID;
		this.websiteID = websiteID;
	}
}
