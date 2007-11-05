package de.deepamehta.service;

import de.deepamehta.AmbiguousSemanticException;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.topics.DataSourceTopic;
import de.deepamehta.topics.LiveTopic;

import java.util.Hashtable;
import java.util.Vector;



/**
 * Implementation of LoginCheck for WebBuilder systems.
 * The LiveTopic representing this LoginCheck must have the following properties:
 * <ul>
 * <li>Exactly one "association" association from the topic to a DataSourceTopic
 * <li><b>Profile Elementtype:</b> Name of the user data table, e.g. "user_profile"
 * <li><b>Username Attribute:</b> Name of column containing login names, e.g. "login"
 * <li><b>Username Attribute:</b> Name of column containing passwords, e.g. "password"
 * </ul>
 */
public class WebBuilderLogin implements LoginCheck, DeepaMehtaConstants {

	/**
	 * The LiveTopic representing this LoginCheck.
	 * Further property queries will be directed to this topic.
	 */
	LiveTopic topic;
		
	/**
	 * Just stores the reference to the LiveTopic
	 * representing this LoginCheck.
	 */
	public WebBuilderLogin(LiveTopic topic) throws Exception {
		this.topic = topic;
	}
	
	/**
	 * @return the CorporateDatasource for authentification.
	 * Note that a new instance of the CorporateDatasource
	 * is created if the parameters "Driver" or "URL" were changed.
	 */
	private CorporateDatasource getDataSource() throws Exception {
		ApplicationService as = topic.as;
		Vector baseTopics = as.getRelatedTopics(topic.getID(), SEMANTIC_DATA_SOURCE, 2);
		if (baseTopics.size() == 0) {
			throw new DeepaMehtaException("*** WebBuilderLogin.getDataSource(): " +
								"there are no datasources assigned with the topic.");
		}
		BaseTopic baseTopic = (BaseTopic) baseTopics.firstElement();	
		if (baseTopics.size() > 1) {
			throw new AmbiguousSemanticException("*** WebBuilderLogin.getDataSource(): " +
			"there are more than one datasources assigned with the topic.", baseTopic);
		}		
		// ### as.createLiveTopic(baseTopic, false, null);		
		DataSourceTopic topic = (DataSourceTopic) as.getLiveTopic((BaseTopic) baseTopic);
		return topic.getDataSource();
	}
	
	/**
	 * Implementation of LoginCheck
	 * @return true if authentification against the WebBuilder system succeeded,
	 *         false otherwise.
	 */
	public boolean loginCheck(String username, String password) {
		String profileElement = topic.getProperty("Profile Elementtype");
		String usernameAttr   = topic.getProperty("Username Attribute");
		String passwordAttr   = topic.getProperty("Password Attribute");
	
		Vector userElements = null;
		// --- username check ---
		try {
			userElements = getDataSource().queryElements(profileElement, usernameAttr, 
																		username, true);
		}
		catch(Exception e) {
			System.out.println("*** WebBuilderLogin.loginCheck(): " +
			"query of userElements failed -- using ApplicationService as fallback.");
			e.printStackTrace();			
			return topic.as.loginCheck(username, password);
		}
		int count = userElements.size();
		if (count == 0) {
			System.out.println(">>> WebBuilderLogin.loginCheck(): user \"" +
				username + "\" is not existent in database.");
			return false;
		}
		if (count > 1) {
			throw new DeepaMehtaException("*** WebBuilderLogin.loginCheck(): user \"" +
			username + "\" exists " + count + " times in \"" + profileElement + "\".");
		}
		// --- password check ---
		Hashtable userElement = (Hashtable) userElements.firstElement();
		String passwdFromDB = (String) userElement.get(passwordAttr.toUpperCase());				
		String cryptedPasswd = chiffre(password) + "";
		
		return cryptedPasswd.equals(passwdFromDB);
	}

	/**
	 * One-way password encryption used in XBuilder system.
	 * Adapted from java.lang.String.hashCode() of JDK1.1.8
	 */
	static int chiffre( String s ) {
		int h = 0;
		int off = 0;
		char[] val = s.toCharArray();
		int len = s.length();
	
		if (len < 16) {
	 	    for (int i = len ; i > 0; i--) {
	 			h = (h * 37) + val[off++];
	 	    }
	 	}
	 	else
	 	{
	 	    // only sample some characters
	 	    int skip = len / 8;
	 	    for (int i = len ; i > 0; i -= skip, off += skip) {
	 			h = (h * 39) + val[off];
	 	    }
	 	}
		return h;
	}
}