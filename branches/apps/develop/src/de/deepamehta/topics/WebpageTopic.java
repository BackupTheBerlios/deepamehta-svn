package de.deepamehta.topics;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.DeepaMehtaServiceUtils;
import de.deepamehta.service.Session;
import de.deepamehta.topics.personalweb.PersonalWeb;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;



/**
 * This active topic represents a webpage by means of a URL.
 * <p>
 * <h4>Active behavoir</h4>
 *
 * ### The active <i>displaing in browser</i> behavoir of a <code>WorkspaceTopic</code> is
 * loading the webpage in the clients browser once the user triggers the default action.
 * <p>
 * The active <i>downloading</i> behavoir of a <code>WorkspaceTopic</code> is
 * downloading the webpage in the servers file system (### to client in future).
 * <p>
 * The active <i>notification enabling</i> behavoir of a <code>WorkspaceTopic</code> is
 * creating association of type <code>notification</code> between <code>WorkspaceTopic</code>
 * and <code>UserTopic</code> that means that user wants to be notified about existence
 * and last changes of this webpage.
 * <p>
 * The active <i>notification disabling</i> behavoir of a <code>WorkspaceTopic</code> is
 * deleting association of type <code>notification</code> between <code>WorkspaceTopic</code>
 * and <code>UserTopic</code> that means that user does not want to be notified about
 * existence and last changes of this webpage.
 * <p>
 * The active <i>properties change allowed</i> behavoir of a <code>WorkspaceTopic</code> is
 * checking of <code>URL</code> and <code>Checking Interval</code> properties. If the
 * <code>URL</code> property is entered without prefix <code>http://</code>, this prefix is
 * automaticaly added. If some error is found in propeties, <code>DeepaMehtaException</code>
 * with description of error is thrown and changes are rejected.
 * <p>
 * The active <i>properties changed</i> behavoir of a <code>WorkspaceTopic</code> is
 * reacting on changes in <code>URL</code> and <code>Checking Interval</code> properties.
 * If the notification thread is already running for this topic, <code>URL</code>
 * and <code>Checking Interval</code> properties are updated in this thread.
 * <p>
 * The active <i>property disabling</i> behavoir of a <code>WorkspaceTopic</code> is
 * disabling the <code>Last Modified</code> property.
 * <p>
 * The active <i>retrieve domain information</i> behavoir gets the domain information from
 *  the appropriate whois server.
 * <p>
 * The class contains two internal classes: {@link WebpageTopic.WebpageParserCallback
 * WebpageParserCallback} (parsing of HTML text)
 * and {@link WebpageTopic.WebpageChecker WebpageChecker} (thread used to check
 * changes on webpages).
 * <p>
 * <hr>
 * Last change: 21.04.2009 (2.0b8)<br>
 * J&ouml;rg Richter<br> / Malte Rei&szlig;ig
 * jri@deepamehta.de / mre@deepamehta.de
 */
public class WebpageTopic extends LiveTopic {



	private static final String ITEM_DOWNLOAD_WEBPAGE = "Download";
	private static final String  CMD_DOWNLOAD_WEBPAGE = "updateWebpage";
	private static final String ICON_DOWNLOAD_WEBPAGE = "webpage.gif"; // applications-internet.gif




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
		// --- "Download" ---
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
			// ### try {
				String urlString;
				// ### accessing local pages is disabled
				// ###
				// ### if (as.localFileExists(new URL(urlProperty))) {
				// ###	urlString = as.getCorporateWebBaseURL() + urlProperty.substring(7);		// ### 7
				// ###	System.out.println(">>> Webpage \"" + urlProperty + "\" exists " +
				// ###		"locally -- URL transformed to \"" + urlString + "\"");
				// ### } else {
				urlString = urlProperty;
					//
				// ###	System.out.println(">>> Webpage \"" + urlProperty + "\" does NOT " +
				// ###		"exists locally -- online connection required");
				// ### }
				directives.add(DIRECTIVE_OPEN_URL, urlString);
                // as.setTopicProperty(this, PROPERTY_BROWSER, urlString);
			// ### } catch (MalformedURLException e) {
			// ###	throw new DeepaMehtaException("URL is invalid: \"" + urlProperty + "\"");
			// ### }
		} else if (command.equals(CMD_DOWNLOAD_WEBPAGE)) {
			// download(topicmapID, viewmode, session, directives);
		} else if (command.startsWith(CMD_FOLLOW_HYPERLINK)) {
			String url = command.substring(CMD_FOLLOW_HYPERLINK.length()+COMMAND_SEPARATOR.length());
            System.out.println("    linkClicked let's browse to: " + url);
            BaseTopic webpage = getWebpageTopic(url, session);
            PresentableTopic pt = as.createPresentableTopic(webpage, this.getID(), topicmapID);
            // is this the way to create an association in CM?
            BaseAssociation link = as.createLiveAssociation(cm.getNewAssociationID() ,ASSOCTYPE_GENERIC, this.getID(), webpage.getID(), session, directives);
            PresentableAssociation pa = as.createPresentableAssociation(ASSOCTYPE_GENERIC, this.getID(), 1, webpage.getID(), 1, true);
            System.out.println("    created a link/assoc ("+link.getID() +") and a new webpage topic("+webpage.getID()+") nearby " +this.getID() + " in map: " + topicmapID);
            directives.add(DIRECTIVE_SHOW_TOPIC, pt);
            directives.add(DIRECTIVE_SHOW_ASSOCIATION, pa);
            directives.add(DIRECTIVE_SELECT_TOPIC, pt.getID());
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
    }



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------


    /**
     * If the URL is already stored in the Corporate Memory, reject the propertyChanged
     *
     * ### To extend the propertyCangedAllowed Hook for the topicMapId for revealing
     * the one webpage topic immediately, instead of just rejecting the user's input
     *
     * @param propName
     * @param propValue
     * @param session
     * @param directives
     * @return
     */
	public boolean propertyChangeAllowed(String propName, String propValue, Session session, CorporateDirectives directives) {
        if (propName.equals(PROPERTY_URL)) {
			try {
                Hashtable props = new Hashtable();
                props.put(PROPERTY_URL, propValue);	// ### ignore ref -- still an issue?
                URL url = new URL(propValue);
                Vector webpages = cm.getTopics(TOPICTYPE_WEBPAGE, props);
                String title = propValue.substring(7);
                int count = webpages.size();
                if (count > 0) {
                    // System.out.println("    URL Cannot be changed, already known topic shares the URL");
                    directives.add(DIRECTIVE_SHOW_MESSAGE, "This URL is already known to DeepaMehta, let's see if it is this ...", new Integer(NOTIFICATION_WARNING));
                    directives.add(DIRECTIVE_SHOW_TOPIC, new PresentableTopic((BaseTopic)webpages.get(0)));
                    directives.add(DIRECTIVE_SELECT_TOPIC, ((BaseTopic)webpages.get(0)).getID());
                    // workaround to allow retrigger URL by hitting Enter
                    as.setTopicProperty(this, PROPERTY_BROWSER, url.toString()); // reset anyway, to be removed when getText Problem is solved
                    directives.add(DIRECTIVE_FOCUS_PROPERTY);
                    // return false;
                } else {
                    as.setTopicProperty(this, PROPERTY_BROWSER, url.toString());
                    // props.put(PROPERTY_BROWSER, url.toString());
                    directives.add(DIRECTIVE_FOCUS_PROPERTY);
                }
			} catch (MalformedURLException e) {
				String errText = "\"" + propValue + "\" is not a valid URL";
				directives.add(DIRECTIVE_SHOW_MESSAGE, errText, new Integer(NOTIFICATION_WARNING));
				System.out.println("*** WebpageTopic.propertyChangeAllowed(): " + errText);
				return false;
			}
		}
		return super.propertyChangeAllowed(propName, propValue, session, directives);
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#setTopicData
	 */
	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps, String topicmapID,
																					String viewmode, Session session) {
		CorporateDirectives directives = super.propertiesChanged(newProps, oldProps, topicmapID, viewmode, session);
		// --- "URL" ---
		String newUrl = (String) newProps.get(PROPERTY_URL);
		if (newUrl != null) {
            try {
				URL url = new URL(newUrl);
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
				// download this webpage
				// download(topicmapID, viewmode, session, directives);
			} catch (MalformedURLException e) {
				// ### should never happen because prohibited by propertyChangeAllowed()
				System.out.println("*** WebpageTopic.propertiesChanged(): " + e);
			}
		}
        return directives;
	}

	public String getNameProperty() {
		return PROPERTY_NAME;
	}

    // What is this used for ? Link in PropertyPanel ?
    // Associate Webpage to Website via "BaseUrl" ?
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
        props.addElement(PROPERTY_DESCRIPTION);
        // props.addElement(PROPERTY_URL);
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
	 * Returns the corresponding website (type <code>tt-website</code>) of this webpage
	 * resp. <code>null</code> if no website is associated.
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

    private BaseTopic getWebpageTopic(String prop, Session session) {
        System.out.println("    getWebpageTopic for:" + prop);
        BaseTopic webpage;
        Hashtable props = new Hashtable();
        props.put(PROPERTY_URL, prop);	// ### ignore ref -- still an issue?
        Vector webpages = cm.getTopics(TOPICTYPE_WEBPAGE, props);
        String title = prop.substring(11); // strips http://www. in the normal case
        int count = webpages.size();
        if (count == 0) {
            // System.out.println("Properties Changed Hook performs existing check");
            String[] elements = prop.split(".");
            if (elements.length > 2) {
                title = elements[1] + elements[2];
            }
            System.out.println("    it's a new webpage, let's make it a topic, title: " + title);
            webpage = as.createLiveTopic(cm.getNewTopicID(), TOPICTYPE_WEBPAGE,
                    title, session);
            if (prop != null) {
                as.setTopicProperty(webpage.getID(), 1, PROPERTY_BROWSER, prop.toString());
                as.setTopicProperty(webpage.getID(), 1, PROPERTY_URL, prop.toString());
                as.setTopicProperty(webpage.getID(), 1, PROPERTY_NAME, title);
                System.out.println("    webpage topic knows where to go next: " + prop);
            }
            //webpage.setProperties(props);
            //webpage.setEvoke(true);
        } else {
            System.out.println("    it's known webpage, let's go with the topic to: " + prop);
            webpage = (BaseTopic) webpages.firstElement();
            // set Browser Window too
            as.setTopicProperty(webpage.getID(), 1, PROPERTY_BROWSER, prop.toString());
            if (count > 1) {
                System.out.println("*** WebpageTopic.getWebpageTopic(): there're " + count +
                    " Webpage topics for \"" + prop + "\"");
            }
            // webpage = new PresentableTopic(webpageTopic, this.getID());
        }
        return webpage;
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
