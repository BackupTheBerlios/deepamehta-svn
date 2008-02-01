package de.deepamehta.service.web;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.Session;
import de.deepamehta.topics.helper.EmailChecker;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;



/**
 * ### A <CODE>WebSession</CODE> represents a client who is connected to the server.
 * <P>
 * At instantiation time of a <CODE>WebSession</CODE> the clients user is not
 * logged in.
 * <P>
 * Note: the same user can login from different client machines at the same time.
 * <P>
 * <HR>
 * Last functional change: 31.1.2008 (2.0b8)<BR>
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

	// ### private String demoTopicmapID;

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
	 * @see		de.deepamehta.service.ApplicationService#createSession
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
	 * @see		de.deepamehta.service.ServerConsole#updateSessions
	 */
	public String getHostname() {
		return null;	// ###
	}

	/**
	 * References checked: 17.12.2001 (2.0a14-pre5)
	 *
	 * @see		de.deepamehta.service.ApplicationService#runsAtServerHost
	 */
	public String getAddress() {
		return null;	// ###
	}

	/**
	 * @see		de.deepamehta.service.CorporateDirectives#updateCorporateMemory
	 * @see		de.deepamehta.topics.TopicMapTopic#openSharedTopicmap
	 */
	public boolean isDemo() {
		return false;	// ###
	}

	public String getDemoTopicmapID() {
		return null;	// ###
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

	/**
	 * @see		de.deepamehta.service.ApplicationService#importTopicmap
	 * @see		de.deepamehta.service.ApplicationService#importCM
	 * @see		de.deepamehta.topics.TopicMapTopic#executeChainedCommand
	 * @see		de.deepamehta.topics.TopicMapTopic#openSharedTopicmap
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
	 * @see		de.deepamehta.service.ApplicationService#startSession	false
	 * @see		de.deepamehta.service.ApplicationService#startDemo		true
	 */
	public void setDemo(boolean isDemo) {
		// ### this.isDemo = isDemo;
	}

	public void setDemoTopicmapID(String demoTopicmapID) {
		// ### this.demoTopicmapID = demoTopicmapID;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#startSession	true
	 * @see		de.deepamehta.service.ApplicationService#startDemo		true
	 */
	public void setLoggedIn(boolean loggedIn) {
		// ### this.loggedIn = loggedIn;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#startSession
	 */
	public void setUserID(String userID) {
		// ### this.userID = userID;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#startSession
	 * @see		de.deepamehta.service.ApplicationService#startDemo
	 */
	public void setUserName(String userName) {
		// ### this.userName = userName;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#addPersonalWorkspace
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
