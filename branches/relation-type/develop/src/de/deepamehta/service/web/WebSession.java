package de.deepamehta.service.web;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.Session;
import de.deepamehta.topics.helper.EmailChecker;
//
import javax.servlet.http.HttpSession;
//
import java.util.*;



/**
 * ### A <CODE>WebSession</CODE> represents a client who is connected to the server.
 * <P>
 * At instantiation time of a <CODE>WebSession</CODE> the clients user is not
 * logged in.
 * <P>
 * Note: the same user can login from different client machines at the same time.
 * <P>
 * <HR>
 * Last functional change: 9.7.2004 (2.0b3)<BR>
 * Last documentation update: 17.5.2001 (2.0a10-post3)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public final class WebSession implements Session, DeepaMehtaConstants {

	public HttpSession session;

	/**
	 * The session ID of this client session.
	 * <P>
	 * Initialized by {@link #WebSession constructor}.
	 */
	// ### private int sessionID;

	/**
	 * The hostname of the client machine.
	 * <P>
	 * Initialized by {@link #WebSession constructor}.
	 */
	// ### private String clientName;

	/**
	 * The IP address of the client machine.
	 * <P>
	 * Initialized by {@link #WebSession constructor}.
	 */
	// ### private String clientAddress;

	// ---

	/**
	 * Once logged in, reflects weather this session is a demo session
	 * (<CODE>true</CODE>) resp. a login session (<CODE>false</CODE>).
	 * <P>
	 * Initialized by {@link #setDemo}.
	 */
	// ### private boolean isDemo;

	/**
	 * Once logged in, set to (<CODE>true</CODE>).
	 * <P>
	 * Note: remains uninizialized (<CODE>false</CODE>) if this is a demo session.
	 * <P>
	 * Initialized by {@link #setLoggedIn}.
	 */
	// ### private boolean loggedIn;

	/**
	 * Once logged in, the user ID of the logged in user.
	 * <P>
	 * Note: remains uninizialized if this is a demo session.
	 * <P>
	 * Initialized by {@link #setUserID}.
	 */
	// ### private String userID;

	/**
	 * Once this session is started, the username of the session user.
	 * <P>
	 * Note: also initialized if this is a demo session (demo users are named "Guest x"
	 * where x is the session ID).
	 * <P>
	 * Initialized by {@link #setUserName}.
	 */
	// ### private String userName;

	/**
	 * Accessed by {@link #getUserPreferences}
	 */
	// ### private UserPreferences userPrefs;

	// ---

	/**
	 * Once logged in, the user's personal workspace (type <CODE>tt-topicmap</CODE>).
	 * <P>
	 * <TABLE>
	 * <TR><TD><B>Initialized by</B></TD></TR>
	 * <TR><TD>{@link #setPersonalWorkspace}</TD></TR>
	 * <TR><TD><B>Accessed by</B></TD></TR>
	 * <TR><TD>{@link #getPersonalWorkspace}</TD></TR>
	 * </TABLE>
	 */
	// ### private BaseTopic personalWorkspace;

	// ### private Object comm;



	// *******************
	// *** Constructor ***
	// *******************


	/**
	 * @see		ApplicationService#createSession
	 */
	WebSession(HttpSession session) {
		this.session = session;
	}



	// *****************************************************************
	// *** Implementation of interface de.deepamehta.service.Session ***
	// *****************************************************************



	public Object getAttribute(String name) {
		return session.getAttribute(name);
	}

	public Enumeration getAttributeNames() {
		return session.getAttributeNames();
	}

	public int getType() {
		return SESSION_WEB_INTERFACE;
	}

	/**
	 * @see		DeepaMehtaServer#unregisterClient
	 */
	public int getSessionID() {
		return -1;		// ###
	}

	/**
	 * References checked: 17.12.2001 (2.0a14-pre5)
	 *
	 * @see		ServerConsole#updateSessions
	 */
	public String getHostname() {
		return null;	// ###
	}

	/**
	 * References checked: 17.12.2001 (2.0a14-pre5)
	 *
	 * @see		ApplicationService#runsAtServerHost
	 */
	public String getAddress() {
		return null;	// ###
	}

	/**
	 * @see		CorporateDirectives#updateCorporateMemory
	 * @see		de.deepamehta.topics.TopicMapTopic#openGroupView
	 */
	public boolean isDemo() {
		return false;	// ###
	}

	public boolean loggedIn() {
		return false;	// ###
	}

	/**
	 * @see		de.deepamehta.topics.TopicMapTopic#executeChainedCommand
	 */
	public String getUserID() {
		return null;	// ###
	}

	public String getUserName() {
		return null;	// ###
	}

	/* ### public UserPreferences getUserPreferences() {
		return null;	// ###
	} */

	/**
	 * @see		ApplicationService#importTopicmap
	 * @see		ApplicationService#importCM
	 * @see		de.deepamehta.topics.TopicMapTopic#executeChainedCommand
	 * @see		de.deepamehta.topics.TopicMapTopic#openGroupView
	 * @see		de.deepamehta.topics.TopicMapTopic#publish
	 */
	public BaseTopic getPersonalWorkspace() throws DeepaMehtaException {
		return null;	// ###
	}

	public Object getCommunication() {
		return null;	// ###
	}

	// ---

	public void setAttribute(String name, Object value) {
		session.setAttribute(name, value);
	}

	public void removeAttribute(String name) {
		session.removeAttribute(name);
	}

	/**
	 * @see		ApplicationService#startSession	false
	 * @see		ApplicationService#startDemo		true
	 */
	public void setDemo(boolean isDemo) {
		// ### this.isDemo = isDemo;
	}

	/**
	 * @see		ApplicationService#startSession	true
	 * @see		ApplicationService#startDemo		true
	 */
	public void setLoggedIn(boolean loggedIn) {
		// ### this.loggedIn = loggedIn;
	}

	/**
	 * @see		ApplicationService#startSession
	 */
	public void setUserID(String userID) {
		// ### this.userID = userID;
	}

	/**
	 * @see		ApplicationService#startSession
	 * @see		ApplicationService#startDemo
	 */
	public void setUserName(String userName) {
		// ### this.userName = userName;
	}

	/**
	 * @see		ApplicationService#startSession
	 */
	/* ### public void setUserPreferences(UserPreferences userPrefs) {
		// ### this.userPrefs = userPrefs;
	} */

	/**
	 * @see		ApplicationService#addPersonalWorkspace
	 */
	public void setPersonalWorkspace(BaseTopic personalWorkspace) {
		// ### this.personalWorkspace = personalWorkspace;
	}

	public void setCommunication(Object comm) {
		// ### this.comm = comm;
	}

	public void setEmailChecker(EmailChecker ec) {
		// ### this.emailChecker = ec;
	}
}
