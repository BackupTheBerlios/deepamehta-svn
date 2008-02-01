package de.deepamehta.service;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.topics.helper.EmailChecker;

import java.util.Enumeration;
import java.util.Hashtable;



/**
 * A <CODE>DeepaMehtaSession</CODE> represents a client who is connected to the server.
 * <P>
 * At instantiation time of a <CODE>DeepaMehtaSession</CODE> the clients user is not
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
public final class DeepaMehtaSession implements Session, DeepaMehtaConstants {

	private Hashtable attributes;

	/**
	 * The session ID of this client session.
	 * <P>
	 * Initialized by {@link #DeepaMehtaSession constructor}.
	 */
	private int sessionID;

	/**
	 * The hostname of the client machine.
	 * <P>
	 * Initialized by {@link #DeepaMehtaSession constructor}.
	 */
	private String clientName;

	/**
	 * The IP address of the client machine.
	 * <P>
	 * Initialized by {@link #DeepaMehtaSession constructor}.
	 */
	private String clientAddress;

	// ---

	/**
	 * Once logged in, reflects weather this session is a demo session
	 * (<CODE>true</CODE>) resp. a login session (<CODE>false</CODE>).
	 * <P>
	 * Initialized by {@link #setDemo}.
	 */
	private boolean isDemo;
	
	private String demoTopicmapID;

	/**
	 * Once logged in, set to (<CODE>true</CODE>).
	 * <P>
	 * Note: remains uninizialized (<CODE>false</CODE>) if this is a demo session.
	 * <P>
	 * Initialized by {@link #setLoggedIn}.
	 */
	private boolean loggedIn;

	/**
	 * Once logged in, the user ID of the logged in user.
	 * <P>
	 * Note: remains uninizialized if this is a demo session.
	 * <P>
	 * Initialized by {@link #setUserID}.
	 */
	private String userID;

	/**
	 * Once this session is started, the username of the session user.
	 * <P>
	 * Note: also initialized if this is a demo session (demo users are named "Guest x"
	 * where x is the session ID).
	 * <P>
	 * Initialized by {@link #setUserName}.
	 */
	private String userName;

	private EmailChecker emailChecker;

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
	private BaseTopic personalWorkspace;

	private Object comm;



	// *******************
	// *** Constructor ***
	// *******************


	/**
	 * @see		ApplicationService#createSession
	 */
	DeepaMehtaSession(int sessionID, String clientName, String clientAddress) {
		this.attributes = new Hashtable();
		this.sessionID = sessionID;
		this.clientName = clientName;
		this.clientAddress = clientAddress;
	}



	// *****************************************************************
	// *** Implementation of interface de.deepamehta.service.Session ***
	// *****************************************************************



	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public Enumeration getAttributeNames() {
		return attributes.keys();
	}

	public int getType() {
		return SESSION_JAVA_CLIENT;
	}

	/**
	 * @see		DeepaMehtaServer#unregisterClient
	 */
	public int getSessionID() {
		return sessionID;
	}

	/**
	 * References checked: 17.12.2001 (2.0a14-pre5)
	 *
	 * @see		ServerConsole#updateSessions
	 */
	public String getHostname() {
		return clientName;
	}

	/**
	 * References checked: 17.12.2001 (2.0a14-pre5)
	 *
	 * @see		ApplicationService#runsAtServerHost
	 */
	public String getAddress() {
		return clientAddress;
	}

	/**
	 * @see		CorporateDirectives#updateCorporateMemory
	 * @see		de.deepamehta.topics.TopicMapTopic#openSharedTopicmap
	 */
	public boolean isDemo() {
		return isDemo;
	}

	public String getDemoTopicmapID() {
		return demoTopicmapID;
	}

	public boolean loggedIn() {
		return loggedIn;
	}

	/**
	 * @see		de.deepamehta.topics.TopicMapTopic#executeChainedCommand
	 */
	public String getUserID() {
		return userID;
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * @see		ApplicationService#importTopicmap
	 * @see		ApplicationService#importCM
	 * @see		de.deepamehta.topics.TopicMapTopic#executeChainedCommand
	 * @see		de.deepamehta.topics.TopicMapTopic#openSharedTopicmap
	 * @see		de.deepamehta.topics.TopicMapTopic#publish
	 */
	public BaseTopic getPersonalWorkspace() throws DeepaMehtaException {
		if (personalWorkspace == null) {
			throw new DeepaMehtaException("User \"" + getUserName() + "\" has no " +
				"personal workspace");
		}
		return personalWorkspace;
	}

	public Object getCommunication() {
		return comm;
	}

	// ---

	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	/**
	 * References checked: 31.1.2008 (2.0b8)
	 *
	 * @see		ApplicationService#startSession		false
	 * @see		ApplicationService#startDemo		true
	 */
	public void setDemo(boolean isDemo) {
		this.isDemo = isDemo;
	}

	public void setDemoTopicmapID(String demoTopicmapID) {
		this.demoTopicmapID = demoTopicmapID;
	}

	/**
	 * @see		ApplicationService#startSession		true
	 * @see		ApplicationService#startDemo		true
	 */
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	/**
	 * @see		ApplicationService#startSession
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * @see		ApplicationService#startSession
	 * @see		ApplicationService#startDemo
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @see		ApplicationService#addPersonalWorkspace
	 */
	public void setPersonalWorkspace(BaseTopic personalWorkspace) {
		this.personalWorkspace = personalWorkspace;
	}

	public void setCommunication(Object comm) {
		this.comm = comm;
	}

	public void setEmailChecker(EmailChecker ec) {
		this.emailChecker = ec;
	}
}
