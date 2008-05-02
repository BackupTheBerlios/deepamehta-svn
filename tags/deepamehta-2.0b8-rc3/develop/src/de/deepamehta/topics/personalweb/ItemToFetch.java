package de.deepamehta.topics.personalweb;

import java.net.MalformedURLException;
import java.net.URL;



/**
 * <CODE>ItemToFetch</CODE> objects are stored in the fetch-queue.
 * An <CODE>ItemToFetch</CODE> consists of 3 values:
 * <UL>
 * <LI>URL this item originally was retrieved from.
 * <LI>...
 * </UL>
 */
public class ItemToFetch {

	public URL		url;
	public String	webpageID;	// DeepaMehta specific
	public String	websiteID;	// DeepaMehta specific

	public ItemToFetch(String url, String webpageID) throws MalformedURLException {
		this(new URL(url), webpageID, null);
	}

	/**
	 * @see		PersonalWeb#PersonalWeb
	 * @see		ParseThread#parseHTML
	 */
	public ItemToFetch(URL url, String webpageID, String websiteID) {
		this.url = url;
		this.webpageID = webpageID;
		this.websiteID = websiteID;
	}

	/**
	 * Needed because Vector#contains is used in PersonalWeb#putInFetchQueue
	 */
	public boolean equals(Object obj) {
		return url.equals(((ItemToFetch) obj).url);	// ### probably other attribute
	}
}
