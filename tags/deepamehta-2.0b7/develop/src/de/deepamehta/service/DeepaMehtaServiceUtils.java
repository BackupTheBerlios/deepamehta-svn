package de.deepamehta.service;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.topics.LiveTopic;
//
import com.sun.jimi.core.Jimi;
import java.awt.Image;
import java.util.*;
import java.sql.*;
import java.io.*;



/**
 * <P>
 * <HR>
 * Last functional change: 18.2.2004 (2.0b3-pre1)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class DeepaMehtaServiceUtils implements DeepaMehtaConstants {



	// **********************
	// *** Static Methods ***
	// **********************



	/**
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @throws	DeepaMehtaException	if an error occurrs
	 *
	 * @see		de.deepamehta.topics.TypeTopic#createIconfile
	 */
	/* ### public static void createGIFFile(Image image, File file) throws DeepaMehtaException {
		try {
			// ### Jimi.putImage(image, file.getPath());	// ### cannot find encoder
			OutputStream out = new BufferedOutputStream(new FileOutputStream(file), FILE_BUFFER_SIZE);
			new GifEncoder(image, out).encode();
			out.close();	// Note: close() is needed to flush the buffer
		} catch (Throwable e) {
			file.delete();	// ### return value?
			throw new DeepaMehtaException(e.getMessage());
		}
	} */

	/**
	 * Saves an image to disk. Supported are JPG and PNG formats.
	 * The used format depends on the file extension.
	 *
	 * @param	image	the image
	 * @param	file	the file reference. Should end with ".jpg" or ".png".
	 */
	public static void createImageFile(Image image, File file) throws DeepaMehtaException {
		try {
			Jimi.putImage(image, file.getPath());
		} catch (Throwable e) {
			System.out.println("*** DeepaMehtaServiceUtils.createImageFile(): " + e);			
			throw new DeepaMehtaException(e.getMessage());
		}
	}

	// ---

	/**
	 * Returns the internet domain of the specified host.<BR>
	 * a) www.intel.com -> intel.com<BR>
	 * b) www.bbc.co.uk -> bbc.co.uk<BR>
	 *
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#revealLink
	 */
	public static String domain(String host, ApplicationService as) {
		int pos = host.lastIndexOf('.');
		if (pos == -1 || (pos + 1) == host.length()) {
			// no dot or it is at the end
			System.out.println("getSiteURL(): nonsence in URL!");			
			return "";
		}
		String topLevelDomainName = host.substring(pos + 1);
		boolean hasSubdomainsBoolean = hasSubdomains(topLevelDomainName);
		int dotCounter = 0;
		for (int i = host.length() - 1; i > 0; i--) {
			char ch = host.charAt(i);
			if (ch == '.') {
				dotCounter++;
				if (dotCounter == 2 && !hasSubdomainsBoolean) {
					//e.g. www.intel.com -> intel.com
					return host.substring(i + 1);
				}
				if (dotCounter == 3 && hasSubdomainsBoolean) {
					//e.g. www.bbc.co.uk -> bbc.co.uk
					return host.substring(i + 1);
				}
			}				
		}
		return host; // cases like intel.com or bbc.co.uk
	}

	/**
	 * Tests whether the domain has subdomains (e.g. uk -> co.uk, ac.uk etc.)
	 *
	 * @param	domainName domain
	 *
	 * @return	true if the domain has subdomains, otherwise false
	 *
	 * @see		#domain
	 */		
	private static boolean hasSubdomains(String domainName) {
		if (domainName.equals("jp")) {
			return true;
		} else if (domainName.equals("th")) {
			return true;
		} else if (domainName.equals("nz")) {
			return true;
		} else if (domainName.equals("uk")) {
			return true;
		} else if (domainName.equals("ar")) {
			return true;
		} else if (domainName.equals("au")) {
			return true;
		} else if (domainName.equals("sg")) {
			return true;
		}
		return false;
		/* ### Vector whoisServers = as.cm.getTopics("tt-twoleveldomains", new Hashtable());
		if (whoisServers.isEmpty()) {
			// nothing was found
			return false;
		}
		Enumeration e = whoisServers.elements();
		while (e.hasMoreElements()) {
			BaseTopic bt = (BaseTopic) e.nextElement();
			Hashtable properties = as.getTopicProperties(bt.getID(), bt.getVersion());
			String domainList = (String) properties.get("Domains");
			if (domainList == null || domainList.trim().equals("")) {
				continue; // empty, continue
			}		
			if (containsDomain(domainList, domainName)) {
				return true;
			}
		}
		return false; */
	}

	/**
	 * Checks whether the domain is in the lists of domains. ### to be dropped
	 *
	 * @param	domainList	list of domain
	 * @param	domainName	searched domain
	 *
	 * @return	true if domain was found, otherwise false
	 *
	 * @see		#getWhoisServer	 	 	 
	 */	
	public static boolean containsDomain(String domainList, String domainName) {
		// check whether domainName is in in the list domainList
		// ### domainList = domainList.toLowerCase();
		// ### domainName = domainName.toLowerCase();
		boolean domainFound = false;
		int domainsLength = domainList.length();
		int domainLength = domainName.length();
		int position = 0;
		while (!domainFound) {			
			position = domainList.indexOf(domainName, position);
			if (position == -1) {
				break;
			}
			int startPosition = position;
			int behindEnd = position + domainLength;
			if (startPosition > 0) {
				// test whether before it is a ","
				char ch = domainList.charAt(startPosition - 1);
				if (ch != ',') {
					// it is inside of something -> continue
					position++;
					continue;
				}
			}
			if (behindEnd < domainsLength) {
				// test whether after it is a ","
				char ch = domainList.charAt(behindEnd);
				if (ch != ',') {
					// it is inside of something -> continue
					position++;
					continue;
				}					
			}
			domainFound = true;				
		}
		//
		return domainFound;						
	}

	// ---

	/**
	 * References checked: 26.12.2001 (2.0a14-pre5)
	 *
	 * @see		de.deepamehta.topics.TypeTopic#makeTypeDefinition
	 */
	/* ### public static Hashtable fromTypeDefinition(Vector vector) {
		Hashtable table = new Hashtable();
		Enumeration e = vector.elements();
		PropertyDefinition propDef;
		while (e.hasMoreElements()) {
			propDef = (PropertyDefinition) e.nextElement();
			table.put(propDef.getPropertyName(), propDef);
		}
		return table;
	} */

	// ---

	/**
	 * References checked: 20.10.2001 (2.0a13-pre1)
	 *
	 * @see		CorporateCommands#addTopicTypeCommands
	 * @see		CorporateCommands#addAssocTypeCommands
	 */
	public static int findIndexByID(Vector topics, String topicID) {
		for (int i = 0; i < topics.size(); i++) {
			if (((BaseTopic) topics.elementAt(i)).getID().equals(topicID)) {
				return i;
			}
		}
		return -1;
	}

	public static int findIndexByName(Vector topics, String name) {
		for (int i = 0; i < topics.size(); i++) {
			if (((BaseTopic) topics.elementAt(i)).getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}
}
