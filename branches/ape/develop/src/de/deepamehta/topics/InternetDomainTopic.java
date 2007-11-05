package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.DeepaMehtaServiceUtils;
import de.deepamehta.service.Session;
import de.deepamehta.topics.helper.WhoisRequest;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * An internet 2nd-level domain.
 * <P>
 * <HR>
 * Last functional change: 11.3.2004 (2.0b3-pre1)<BR>
 * Last documentation update: 13.8.2001 (2.0a11)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class InternetDomainTopic extends LiveTopic {



	private static final String ITEM_GET_DOMAIN_INFORMATION = "Get Domain Information";
	private static final String CMD_GET_DOMAIN_INFORMATION = "getDomainInformation";



	// *******************
	// *** Constructor ***
	// *******************



	public InternetDomainTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// ***************
	// *** Methods ***
	// ***************



	// ----------------------
	// --- Defining Hooks ---
	// ----------------------



	/* ### protected CorporateDirectives evoke(Session session, String topicmapID,
																String viewmode) {
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		//
		return directives;
	} */

	/**
	 * @see		de.deepamehta.service.ApplicationService#getTopicCommands
	 */	
	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		int editorContext = as.editorContext(topicmapID);
		//
		commands.addNavigationCommands(this, editorContext, session);
		commands.addSeparator();
		// --- "Get Domain Information" ---
		commands.addCommand(ITEM_GET_DOMAIN_INFORMATION, CMD_GET_DOMAIN_INFORMATION);
		// --- standard topic commands ---
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#performTopicAction
	 */
	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		if (command.equals(CMD_GET_DOMAIN_INFORMATION)) {
			CorporateDirectives directives = new CorporateDirectives();
			retrieveDomainInformation(topicmapID, viewmode, directives);
			return directives;
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
	}

	public static Vector hiddenProperties(TypeTopic type) {
		Vector props = new Vector();
		props.add(PROPERTY_DESCRIPTION);
		props.add(PROPERTY_ICON);
		return props;
	}



	// -----------------------
	// --- Private Methods ---
	// -----------------------



	/**
	 * Retrieves domain information (using whois servers)
	 *
	 * @param	directives	directives which should be later executed
	 *
	 * @see		#executeCommand
	 */		
	private void retrieveDomainInformation(String topicmapID, String viewmode, CorporateDirectives directives) {
		String domain = getName();
		// error check 1
		if (domain.equals("")) {
			throw new DeepaMehtaException("no topic name set");
		}
		// error check 2
		int pos = domain.lastIndexOf('.');
		if (pos == -1 || (pos + 1) == domain.length()) {
			// no dot or it is at the end
			throw new DeepaMehtaException("\"" + domain + "\" is not a valid domain name");
		}
		// get toplevel-domain
		String topLevelDomain = domain.substring(pos + 1);
		// get corresponding whois-server
		String whoisServer = getWhoisServer(topLevelDomain);
		System.out.println(">>> InternetDomainTopic.retrieveDomainInformation(): domain=\"" + domain +
			"\" tld=\"" + topLevelDomain + "\" whois-server=\"" + whoisServer + "\"");
		WhoisRequest whois = new WhoisRequest(whoisServer);
		// query whois-server
		String whoisRecord;
		try {
			whoisRecord = whois.whois(domain);
		} catch (Exception exc1) {
			throw new DeepaMehtaException("whois-lookup for domain \"" + domain + "\" failed");
		}
		// set property
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DOMAIN_INFORMATION, whoisRecord);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
	}

	/**
	 * Returns appropriate whois-server for the specified toplevel-domain.
	 *
	 * @param	topLevelDomain	  e.g. <CODE>com</CODE>, <CODE>de</CODE>, <CODE>uk</CODE>
	 *
	 * @return	the whois server, e.g. <CODE>whois.ripe.net</CODE>
	 *
	 * @see		#retrieveDomainInformation	 	 	 
	 */		
	private String getWhoisServer(String topLevelDomain) {
		Vector whoisServers = cm.getTopics("tt-whoistopic", new Hashtable());
		// error check
		if (whoisServers.isEmpty()) {
			throw new DeepaMehtaException("no \"Whois\" topics in corporate memory");
		}
		//
		Enumeration e = whoisServers.elements();
		while (e.hasMoreElements()) {
			BaseTopic whoisServer = (BaseTopic) e.nextElement();
			String domainList = getProperty(whoisServer, "Domains");
			String server = getProperty(whoisServer, "Server");			
			if (DeepaMehtaServiceUtils.containsDomain(domainList, topLevelDomain)) {
				return server;
			}
		}
		//
		throw new DeepaMehtaException("no whois-server for toplevel-domain \"" + topLevelDomain + "\" available");
	}

	/**
	 * ### DM does not handle apostrophes correctly, therefore it must be done here.<BR>
	 * ### This method will be obsolete as soon as this is solved in the framework.
	 * ### -- Meanwhile there is RCM.quote()
	 * <P>
	 * Handling apostrophes in DeepaMehta (this is not correctly implemented in
	 * DeepaMehta)
	 *
	 * @param	str		text with simple apotrophes
	 *
	 * @return	text	with doubled apostrophes
	 *
	 * @see		#retrieveDomainInformation	 	 	 
	 */		
	/* ### private String handleApostrophes(String str) {
		// return str; // use when its ok in framework
		String result = "";
		String rest = str;
		int apPos;
		apPos = rest.indexOf("\'");
		while (apPos > -1) {
			result = result + rest.substring(0,apPos) + "\'\'"; // double apostrophe
			int restLength = rest.length();
			if (apPos + 1 == restLength) {
				// it was the end of the string
				rest = "";
			} else {
				// take the rest of the string
				rest = rest.substring(apPos + 1);
			}
			apPos = rest.indexOf("\'");			
		}
		result = result + rest;
		return result;
	} */
}
