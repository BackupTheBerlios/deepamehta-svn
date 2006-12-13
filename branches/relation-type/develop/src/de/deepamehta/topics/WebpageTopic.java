package de.deepamehta.topics;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.DeepaMehtaServiceUtils;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.topics.personalweb.PersonalWeb;
//
import java.net.*;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;



/**
 * This active topic represents a webpage by means of a URL.
 * <P>
 * <H4>Active behavoir</H4>
 *
 * ### The active <I>displaing in browser</I> behavoir of a <CODE>WorkspaceTopic</CODE> is 
 * loading the webpage in the clients browser once the user triggers the default action.
 * <P>
 * The active <I>downloading</I> behavoir of a <CODE>WorkspaceTopic</CODE> is
 * downloading the webpage in the servers file system (### to client in future).
 * <P>
 * The active <I>notification enabling</I> behavoir of a <CODE>WorkspaceTopic</CODE> is
 * creating association of type <CODE>notification</CODE> between <CODE>WorkspaceTopic</CODE>
 * and <CODE>UserTopic</CODE> that means that user wants to be notified about existence
 * and last changes of this webpage.
 * <P>
 * The active <I>notification disabling</I> behavoir of a <CODE>WorkspaceTopic</CODE> is
 * deleting association of type <CODE>notification</CODE> between <CODE>WorkspaceTopic</CODE>
 * and <CODE>UserTopic</CODE> that means that user does not want to be notified about 
 * existence and last changes of this webpage.
 * <P>
 * The active <I>properties change allowed</I> behavoir of a <CODE>WorkspaceTopic</CODE> is
 * checking of <CODE>URL</CODE> and <CODE>Checking Interval</CODE> properties. If the 
 * <CODE>URL</CODE> property is entered without prefix <CODE>http://</CODE>, this prefix is
 * automaticaly added. If some error is found in propeties, <CODE>DeepaMehtaException</CODE>
 * with description of error is thrown and changes are rejected.
 * <P>
 * The active <I>properties changed</I> behavoir of a <CODE>WorkspaceTopic</CODE> is
 * reacting on changes in <CODE>URL</CODE> and <CODE>Checking Interval</CODE> properties. 
 * If the notification thread is already running for this topic, <CODE>URL</CODE> 
 * and <CODE>Checking Interval</CODE> properties are updated in this thread.
 * <P>
 * The active <I>property disabling</I> behavoir of a <CODE>WorkspaceTopic</CODE> is
 * disabling the <CODE>Last Modified</CODE> property.
 * <P>
 * The active <I>retrieve domain information</I> behavoir gets the domain information from
 *  the appropriate whois server.
 * <P> 
 * The class contains two internal classes: {@link WebpageTopic.WebpageParserCallback 
 * WebpageParserCallback} (parsing of HTML text) 
 * and {@link WebpageTopic.WebpageChecker WebpageChecker} (thread used to check 
 * changes on webpages).
 * <P>
 * <HR>
 * Last functional change: 5.4.2005 (2.0b6)<BR>
 * Last documentation update: 21.3.2001<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class WebpageTopic extends LiveTopic {



	private static final String ITEM_DOWNLOAD_WEBPAGE = "Reload";
	private static final String  CMD_DOWNLOAD_WEBPAGE = "updateWebpage";
	private static final String ICON_DOWNLOAD_WEBPAGE = "webpage.gif";



	// *******************
	// *** Constructor ***
	// *******************



	public WebpageTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	/**
	 * @see		de.deepamehta.service.ApplicationService#getTopicCommands
	 */	
	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		int editorContext = as.editorContext(topicmapID);
		//
		commands.addNavigationCommands(this, editorContext, session);
		//
		// --- "Reload" ---
		commands.addSeparator();
		commands.addCommand(ITEM_DOWNLOAD_WEBPAGE, CMD_DOWNLOAD_WEBPAGE, FILESERVER_ICONS_PATH, ICON_DOWNLOAD_WEBPAGE);
		//
		// --- standard topic commands ---
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#performTopicAction
	 */
	public CorporateDirectives executeCommand(String command, Session session,
						String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		if (command.equals(CMD_DEFAULT)) { // when double-clicked
			// it is necessary to handle correctly downloaded pages
			String urlProperty = getProperty(PROPERTY_URL);
			try {
				String urlString;
				if (as.localFileExists(new URL(urlProperty))) {
					urlString = as.getCorporateWebBaseURL() + urlProperty.substring(7);
					// ### 7
					System.out.println(">>> Webpage \"" + urlProperty + "\" exists " +
						"locally -- URL transformed to \"" + urlString + "\"");
				} else {
					urlString = urlProperty;
					//
					System.out.println(">>> Webpage \"" + urlProperty + "\" does NOT " +
						"exists locally -- online connection required");
				}
				directives.add(DIRECTIVE_OPEN_URL, urlString);
			} catch (MalformedURLException e) {
				throw new DeepaMehtaException("URL is invalid: \"" + urlProperty + "\"");
			}
		} else if (command.equals(CMD_DOWNLOAD_WEBPAGE)) {
			download(topicmapID, viewmode, session, directives);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public boolean propertiesChangeAllowed(Hashtable oldData, Hashtable newData, CorporateDirectives directives) {
		String urlString = (String) newData.get(PROPERTY_URL);
		if (urlString != null) {
			try {
				URL url = new URL(urlString);
			} catch (MalformedURLException e) {
				String errText = "\"" + urlString + "\" is not a valid URL";
				directives.add(DIRECTIVE_SHOW_MESSAGE, errText, new Integer(NOTIFICATION_WARNING));
				System.out.println("*** WebpageTopic.propertiesChangeAllowed(): " + errText);
				return false;
			}
		}
		return super.propertiesChangeAllowed(oldData, newData, directives);
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#setTopicData
	 */
	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps, String topicmapID,
																					String viewmode, Session session) {
		CorporateDirectives directives = super.propertiesChanged(newProps, oldProps, topicmapID, viewmode, session);
		// --- "URL" ---
		String prop = (String) newProps.get(PROPERTY_URL);
		if (prop != null) {
			try {
				URL url = new URL(prop);
				String host = url.getHost();
				// remove old website assignment
				BaseTopic website = getWebsite();
				if (website != null && !website.getName().equals(host)) {
					BaseAssociation assoc = cm.getAssociation(ASSOCTYPE_ASSOCIATION, getID(), website.getID());
					directives.add(as.deleteAssociation(assoc));	// ### add HIDE_ASSOC directive instead
				}
				// create website topic
				String websiteID = as.createTopic(TOPICTYPE_WEBSITE, host);
				as.createAssociation(ASSOCTYPE_ASSOCIATION, getID(), websiteID);	// ### use semantic
				//
				// create domain topic
				String domain = DeepaMehtaServiceUtils.domain(host, as);
				String domainID = as.createTopic(TOPICTYPE_INTERNET_DOMAIN, domain);
				as.createAssociation(ASSOCTYPE_ASSOCIATION, websiteID, domainID);	// ### use semantic
				//
				// download this webpage
				download(topicmapID, viewmode, session, directives);
			} catch (MalformedURLException e) {
				// ### should never happen because prohibited by propertiesChangeAllowed()
				System.out.println("*** WebpageTopic.propertiesChanged(): " + e);
			}
		}
		//
		return directives;
	}

	public String getNameProperty() {
		return PROPERTY_URL;
	}

	public Hashtable getPropertyBaseURLs() {
		Hashtable baseURLs = new Hashtable();
		String url = null;
		try {
			url = getProperty(PROPERTY_URL);
			baseURLs.put(PROPERTY_DESCRIPTION, as.getCorporateWebBaseURL() + as.localFile(new URL(url), true));
		} catch (MalformedURLException e) {
			System.out.println(">>> invalid URL: \"" + url + "\"");
		}
		return baseURLs;
	}

	public static Vector hiddenProperties(TypeTopic type) {
		Vector props = new Vector();
		props.addElement(PROPERTY_ICON);
		return props;
	}

	public static void propertyLabel(PropertyDefinition propertyDef, ApplicationService as, Session session) {
		String propName = propertyDef.getPropertyName();
		if (propName.equals(PROPERTY_NAME)) {
			propertyDef.setPropertyLabel("Title");
		} else if (propName.equals(PROPERTY_DESCRIPTION)) {
			propertyDef.setPropertyLabel("Content");
		}
	}



	// **********************
	// *** Utlity Methods ***
	// **********************



	/**
	 * Returns the corresponding website (type <CODE>tt-website</CODE>) of this webpage
	 * resp. <CODE>null</CODE> if no website is associated.
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#isGroupTopicmap
	 */
	public BaseTopic getWebsite() {
		// ### use "as" utlity
		Vector websites = cm.getRelatedTopics(getID(), ASSOCTYPE_ASSOCIATION, TOPICTYPE_WEBSITE, 2);
		if (websites.size() == 0) {
			return null;
		}
		if (websites.size() > 1) {
			throw new DeepaMehtaException("*** WebpageTopic.getWebsite(): \"" + getID() +
				"\" has " + websites.size() + " associated websites (expected is 1)");
		}
		return (BaseTopic) websites.firstElement();
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Starts the crawler for downloading this webpage.
	 *
	 * @param	directives	directives which should be later executed
	 *
	 * @see		#executeCommand	 	 
	 * @see		#propertiesChanged 
	 */
	private void download(String topicmapID, String viewmode, Session session, CorporateDirectives directives) {
		// error check 1
		/* ### if (as.getCorporateWebPath().equals("")) {
			throw new DeepaMehtaException("\"" + PROPERTY_CW_ROOT_DIR + "\" must be set by an administrator");
		} */
		// error check 2
		String url = getProperty(PROPERTY_URL);
		if (url.equals("")) {
			throw new DeepaMehtaException("\"URL\" not set");
		}
		// error check 3
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			throw new DeepaMehtaException("URL \"" + url + "\" is invalid");
		}
		// start crawler
		new PersonalWeb(url, getID(), getWebsite().getID(), as, session, topicmapID, viewmode);
	}
}
